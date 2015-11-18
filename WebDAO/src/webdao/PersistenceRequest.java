/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao;

import java.io.Serializable;

/**
 *
 * @author Chris Whiteley
 */
public class PersistenceRequest implements Serializable {

    private Class[] parameterTypes;
    private Object[] parameters;
    private String className;
    private String methodName;
    private String userName;
    private PersistenceType persistenceType = PersistenceType.READ;
    private boolean ongoingTransaction;

    /**
     * @return the parameters
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;

        // set the persistence type based on the method name
        if (methodName != null) {
            String upperName = methodName.toUpperCase();
            if (upperName.contains("CREATE")) {
                persistenceType = PersistenceType.CREATE;
            } else if (upperName.contains("UPDATE")) {
                persistenceType = PersistenceType.UPDATE;
            } else if (upperName.contains("DELETE")) {
                persistenceType = PersistenceType.DELETE;
            }
        }
    }

    /**
     * @return the parameterTypes
     */
    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * @param parameterTypes the parameterTypes to set
     */
    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the persistenceType
     */
    public PersistenceType getPersistenceType() {
        return persistenceType;
    }

    /**
     * @param persistenceType the persistenceType to set
     */
    public void setPersistenceType(PersistenceType persistenceType) {
        this.persistenceType = persistenceType;
    }

    public boolean isOngoingTransaction() {
        return ongoingTransaction;
    }

    /**
     * @param ongoingTransaction the ongoingTransaction to set
     */
    public void setOngoingTransaction(boolean ongoingTransaction) {
        this.ongoingTransaction = ongoingTransaction;
    }
}
