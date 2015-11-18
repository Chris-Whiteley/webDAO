/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.client;

import com.sonalb.net.http.cookie.Client;
import com.sonalb.net.http.cookie.CookieJar;

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
    private URL servletURL;
    private boolean ongoingTransaction;
    private final Client client;
    private final CookieJar cookieJar;

    public PersistenceInterface(URL serverURL) throws MalformedURLException {
        servletURL = new URL(serverURL.getProtocol() + "://" + serverURL.getHost() + ":" + serverURL.getPort() + CONTEXT_PATH + PERSISTENCE_SERVLET);
        client = new Client();
        cookieJar = new CookieJar();
    }

 
    public void beginTransaction() {
        if (!ongoingTransaction) {
            PersistenceRequest pr = new PersistenceRequest();
            pr.setPersistenceType(PersistenceType.BEGIN_TRANSACTION);
            sendRequest(pr);
            ongoingTransaction = true;
        }
    }

    public void commitTransaction() {
        if (ongoingTransaction) {
            PersistenceRequest pr = new PersistenceRequest();
            pr.setPersistenceType(PersistenceType.COMMIT_TRANSACTION);
            sendRequest(pr);
            ongoingTransaction = false;
        }
    }

    public void rollbackTransaction() {
        if (ongoingTransaction) {
            PersistenceRequest pr = new PersistenceRequest();
            pr.setPersistenceType(PersistenceType.ROLLBACK_TRANSACTION);
            sendRequest(pr);
            ongoingTransaction = false;
        }
    }

    public <T extends Object> T sendRequest(PersistenceRequest pr) {
        Object results = null;
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;

        try {
            // create in constructor
            HttpURLConnection urlConnection = (HttpURLConnection) servletURL.openConnection();
            client.setCookies(urlConnection, cookieJar);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            oos = new ObjectOutputStream(urlConnection.getOutputStream());
            oos.writeObject(preProcessRequest(pr));
            cookieJar.addAll(client.getCookies(urlConnection));
            BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
            ois = new ObjectInputStream(bis);
            results = ois.readObject();
        } catch (ConnectException ce) {
            ongoingTransaction = false;
            throw new RuntimeException("Cannot connect to " + servletURL + ": " + getStackTrace(ce));
        } catch (Exception e) {
            ongoingTransaction = false;
            throw new RuntimeException(e);
        } finally {
            try {
                if (oos != null) {

                    oos.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
            }
        }

        if (results != null) {
            if (results instanceof TransactionTimeoutError) {
                ongoingTransaction = false;
                throw (TransactionTimeoutError) results;
            }

            if (results instanceof RuntimeException) {
                throw (RuntimeException) results;
            }

            if (results instanceof Throwable) {
                Throwable t = (Throwable) results;
                ongoingTransaction = false;
                throw new RuntimeException(t);
            }
        }

        return (T) results;
    }

    private String getStackTrace(Throwable e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    private PersistenceRequest preProcessRequest(PersistenceRequest pr) {
        pr.setOngoingTransaction(ongoingTransaction);
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
//            case BEGIN_TRANSACTION:
 //           case COMMIT_TRANSACTION:
 //           case ROLLBACK_TRANSACTION:
 //               pr.setOngoingTransaction(ongoingTransaction);
 //       }

        return pr;
    }

}
