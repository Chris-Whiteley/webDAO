/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import webdao.LockException;
import webdao.PersistenceRequest;
import webdao.TransactionTimeoutError;
import webdao.VoidReturn;

/**
 *
 * @author Chris Whiteley
 */
class DaoRequestProcessor {

    static final String EM_ATTRIBUTE = "em";

    /**
     * Process this PersistenceRequest by determining the type of request,
     * processing the request and returning the results of running the request
     * in the response object. If no results to return then returns a VoidReturn
     * object.
     *
     * @param pr the Persistence Request
     * @param session the HTTP session
     * @param daoPackageName the name of the package where the dao classes
     * reside
     * @return the response object results to be returned to the client.
     * @throws Exception
     */
    public Object processRequest(PersistenceRequest pr, HttpSession session, String daoPackageName) throws Exception {

        EntityManager em = getEntityManager(pr, session);
        Object response = new VoidReturn();

        switch (pr.getPersistenceType()) {
            case BEGIN_TRANSACTION:
                beginTransaction(em, session);
                break;
            case COMMIT_TRANSACTION:
                commitTransaction(em, session);
                break;
            case ROLLBACK_TRANSACTION:
                rollbackTransaction(em, session);
                break;
            case READ:
                response = runMethod(pr, em, daoPackageName);
                if (pr.isOngoingTransaction()) {
                    em.close();
                }
                break;
            case CREATE:
            case UPDATE:
            case DELETE:
                if (pr.isOngoingTransaction()) {
                    response = runMethod(pr, em, daoPackageName);
                } else {
                    response = runMethodInNewTransaction(em, pr, daoPackageName);
                }
        }

        return response;
    }

    private Object runMethodInNewTransaction(EntityManager em, PersistenceRequest pr, String daoPackageName) throws RuntimeException {
        Object response = null;
        
        em.getTransaction().begin();
        try {
            try {
                response = runDaoMethod(pr, em, daoPackageName);
                em.getTransaction().commit();
            } catch (Throwable t) {
                handleException(t);
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
        return response;
    }

    private Object runMethod(PersistenceRequest pr, EntityManager em, String daoPackageName) throws RuntimeException {
        Object response = null;

        try {
            response = runDaoMethod(pr, em, daoPackageName);
        } catch (Throwable t) {
            handleException(t);
        }

        return response;
    }

    /**
     * Method to get the JPA Entity Manager. If we are in an ongoing transaction
     * then use the one that has been stored in the HTTP session (by the begin
     * transaction request). Otherwise just get it from
     * LocalEntityManagerFactory.
     *
     * @param pr the persistence request
     * @param session the HTTP session
     * @return
     */
    private EntityManager getEntityManager(PersistenceRequest pr, HttpSession session) {
        EntityManager em;

        if (session == null) {
            // not running this in a session so get local transaction
            em = LocalEntityManagerFactory.createEntityManager();
        } else if (pr.isOngoingTransaction()) {
            if (session.getAttribute(EM_ATTRIBUTE) == null) {
                // transaction has timed out
                throw new TransactionTimeoutError();
            } else {
                em = (EntityManager) session.getAttribute(EM_ATTRIBUTE);
            }
        } else {
            // not ongoing transaction   
            em = LocalEntityManagerFactory.createEntityManager();
        }

        return em;
    }

    private void beginTransaction(EntityManager em, HttpSession session) {
        try {
            em.getTransaction().begin();
        } catch (Throwable t) {
            handleException(t);
        } finally {
            session.setAttribute(EM_ATTRIBUTE, em);
        }
    }

    private void commitTransaction(EntityManager em, HttpSession session) {
        try {
            em.getTransaction().commit();
        } catch (Throwable t) {
            handleException(t);
        } finally {
            session.invalidate();
        }
    }

    private void rollbackTransaction(EntityManager em, HttpSession session) {
        try {
            em.getTransaction().rollback();
        } catch (Throwable t) {
            handleException(t);
        } finally {
            session.invalidate();
        }
    }

    /**
     * Use java Reflection to run the DAO method.
     *
     * @param pr Persistence Request containing info about the DAO class and
     * method to run.
     * @return the results from the DAO method.
     * @throws InstantiationException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws RuntimeException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     */
    private Object runDaoMethod(PersistenceRequest pr, EntityManager em, String daoPackageName) throws InstantiationException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, RuntimeException, InvocationTargetException, ClassNotFoundException {
        Object response = null;
        Class daoClass = getDaoClass(pr.getClassName(), daoPackageName);
        Object dao = getDaoInstance(daoClass, em);

        // run the named method on the dao and return the results
        Method method = getMethod(daoClass, pr.getMethodName(), pr.getParameterTypes());
        response = method.invoke(dao, pr.getParameters());

        if (response == null) {
            response = new VoidReturn();
        }

        return response;
    }

    private Method getMethod(Class daoClass, String methodName, Class[] parameterTypes) throws NoSuchMethodException {
        Method daoMethod = null;
        // if there is only one method with this name then we have found it
        Method[] methods = daoClass.getMethods();
        int count = 0;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                daoMethod = method;
                count++;
            }
        }

        if (count == 0) {
            throw new RuntimeException("Method " + methodName + " does not exist in class " + daoClass.getName());
        }

        // if more than one method with this name then use parameter Types to differentiate
        if (count > 1) {
            daoMethod = daoClass.getMethod(methodName, parameterTypes);
        }

        return daoMethod;

    }

    private Object getDaoInstance(Class daoClass, EntityManager em) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Get the required constructor (i.e. with EntityManager as argument)
        Constructor constructor = daoClass.getConstructor(new Class[]{EntityManager.class
        }
        );
        // construct the dao instance
        return constructor.newInstance(em);
    }

    private Class getDaoClass(String className, String daoPackageName) throws ClassNotFoundException {
        StringBuilder daoFullClassName = new StringBuilder(daoPackageName);
        int last = daoFullClassName.length() - 1;
        if (last >= 1 && daoFullClassName.charAt(last) != '.') {
            daoFullClassName.append('.');
        }

        daoFullClassName.append(className);
        Class daoClass = Class.forName(daoFullClassName.toString());
        return daoClass;
    }

    private String getStackTrace(Throwable e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    private void handleException(Throwable e) throws RuntimeException {
        // get the cause of the method invocation exception and return this to the client
        e.printStackTrace();

        // for exceptions that can't be handled by the client return the stack trace of the exception
        Throwable ex = new Throwable(e);
        Throwable cause = e;
        boolean clientCanHandle = true;
        String lockExceptionTrace = null;

        while ((cause != null)) {
            if (cause instanceof OptimisticLockException) {
                lockExceptionTrace = getStackTrace(cause);
            }

            clientCanHandle = !(cause instanceof EclipseLinkException || cause instanceof PersistenceException || lockExceptionTrace != null);
            ex = new Throwable(cause);
            cause = cause.getCause();
        }

        if (clientCanHandle) {
            throw new RuntimeException(ex);
        } else if (lockExceptionTrace != null) {
            throw new LockException(lockExceptionTrace);
        } else {
            throw new RuntimeException(getStackTrace(ex));
        }
    }
}
