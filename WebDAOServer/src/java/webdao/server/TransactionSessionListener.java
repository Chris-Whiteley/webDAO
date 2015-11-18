/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.server;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author Chris Whiteley
 */
public class TransactionSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        EntityManager em = (EntityManager)se.getSession().getAttribute(DaoRequestProcessor.EM_ATTRIBUTE);
        if (em != null) {
            if (em.isOpen()) {
                em.close();
            }
            em = null;
        }
    }    
}
