/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import webdao.PersistenceRequest;

/**
 *
 * @author Chris Whiteley
 */
public class PersistenceServlet extends HttpServlet {

    private String DAO_PACKAGE_LOCATION;

    @Override
    public void init(ServletConfig config) throws ServletException {
        DAO_PACKAGE_LOCATION = config.getInitParameter("daoPackageLocation");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        try (ObjectInputStream inputStream = new ObjectInputStream(request.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(response.getOutputStream());) {
            PersistenceRequest pr = (PersistenceRequest) inputStream.readObject();
            DaoRequestProcessor processor = new DaoRequestProcessor();
            Object result = processor.processRequest(pr, request.getSession(), DAO_PACKAGE_LOCATION);
            outputStream.writeObject(result);
        } catch (ClassNotFoundException | IOException ex) {
            log("Error in PersistenceServlet.processRequest", ex);
            throw new ServletException("Error in PersistenceServlet.processRequest", ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Handles Persistence Requests from the client.";
    }// </editor-fold>

}
