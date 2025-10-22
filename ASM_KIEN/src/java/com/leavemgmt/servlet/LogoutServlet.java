package com.leavemgmt.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.leavemgmt.util.SessionHelper;

import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Logout
        SessionHelper.logout(request);
        
        // Redirect to login page
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}