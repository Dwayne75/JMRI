/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmri.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static jmri.web.servlet.ServletUtil.UTF8_TEXT_HTML;

/**
 * Servlet that simply sends an HTTP 403 FORBIDDEN error.
 *
 * Passing requests for certain resources protects those resources from network
 * access.
 *
 * @author rhwood
 */
public class DenialServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 5496689594665029622L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(UTF8_TEXT_HTML);
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

}
