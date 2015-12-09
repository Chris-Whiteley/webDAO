/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.server;

import javax.servlet.http.HttpSession;
import webdao.PersistenceRequest;

/**
 *
 * @author Chris
 */
public interface DAORequestProcessor {

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
    Object processRequest(PersistenceRequest pr, HttpSession session, String daoPackageName);
    
}
