/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import webdao.PersistenceRequest;
import webdao.PersistenceType;
import webdao.TransactionTimeoutError;

/**
 *
 * @author y125070
 */
public class PersistenceInterface {

    private final String PERSISTENCE_SERVLET = "/PersistenceServlet";
    private final String CONTEXT_PATH = "/WebDAOServer";
    private final URL servletURL;
    //   private boolean ongoingTransaction;
    private ThreadLocal<Boolean> threadsOngoingTransactions = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    public PersistenceInterface(URL serverURL) throws MalformedURLException {
        servletURL = new URL(serverURL.getProtocol() + "://" + serverURL.getHost() + ":" + serverURL.getPort() + CONTEXT_PATH + PERSISTENCE_SERVLET);
    }

    private boolean isOngoingTransaction() {
        return (threadsOngoingTransactions.get());
    }

    private void setOngoingTransaction(boolean ongoing) {
        threadsOngoingTransactions.set(ongoing);
    }

    public void beginTransaction() {
        if (!isOngoingTransaction()) {
            PersistenceRequest pr = new PersistenceRequest();
            pr.setPersistenceType(PersistenceType.BEGIN_TRANSACTION);
            sendRequest(pr);
            setOngoingTransaction(true);
        }
    }

    public void commitTransaction() {
        if (isOngoingTransaction()) {
            PersistenceRequest pr = new PersistenceRequest();
            pr.setPersistenceType(PersistenceType.COMMIT_TRANSACTION);
            sendRequest(pr);
            setOngoingTransaction(false);
        }
    }

    public void rollbackTransaction() {
        if (isOngoingTransaction()) {
            PersistenceRequest pr = new PersistenceRequest();
            pr.setPersistenceType(PersistenceType.ROLLBACK_TRANSACTION);
            sendRequest(pr);
            setOngoingTransaction(false);
        }
    }

    public <T extends Object> T sendRequest(String className, String methodName) {
        PersistenceRequest pr = new PersistenceRequest();
        pr.setClassName(className);
        pr.setMethodName(methodName);
        return sendRequest(pr);
    }

    public <T extends Object> T sendRequest(String className, String methodName, Object... args) {
        PersistenceRequest pr = new PersistenceRequest();
        pr.setClassName(className);
        pr.setMethodName(methodName);
        pr.setParameters(args);
        return sendRequest(pr);
    }

    public <T extends Object> T sendRequest(PersistenceRequest pr) {
        Object results = null;

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) servletURL.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            try (
                    ObjectOutputStream oos = new ObjectOutputStream(urlConnection.getOutputStream())) {
                oos.writeObject(preProcessRequest(pr));
            }

            try (
                    BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                    ObjectInputStream ois = new ObjectInputStream(bis)) {
                results = ois.readObject();
            }
        } catch (ConnectException ce) {
            setOngoingTransaction(false);
            throw new RuntimeException("Cannot connect to " + servletURL + ": " + getStackTrace(ce));
        } catch (IOException | ClassNotFoundException e) {
            setOngoingTransaction(false);
            throw new RuntimeException(e);
        }

        if (results instanceof TransactionTimeoutError) {
            setOngoingTransaction(false);
            throw (TransactionTimeoutError) results;
        }

        if (results instanceof RuntimeException) {
            throw (RuntimeException) results;
        }

        if (results instanceof Throwable) {
            Throwable t = (Throwable) results;
            setOngoingTransaction(false);
            throw new RuntimeException(t);
        }

        return (T) results;
    }

    private String getStackTrace(Throwable e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    private PersistenceRequest preProcessRequest(PersistenceRequest pr) {
        pr.setOngoingTransaction(isOngoingTransaction());
//        switch (pr.getPersistenceType()) {
        // for CREATE and UPDATE requests handle auditing fields
//            case CREATE:
//                for (Object parameter : pr.getParameters()) {
//                    if (parameter instanceof Audit) {
//                       ((Audit) parameter).setAudit();
//                    }
//                }
//                break;
//            case UPDATE:
//                for (Object parameter : pr.getParameters()) {
//                    if (parameter instanceof Audit) {
//                        ((Audit) parameter).setAudit();
//                    }
//                }
//                break;
        return pr;
    }

}
