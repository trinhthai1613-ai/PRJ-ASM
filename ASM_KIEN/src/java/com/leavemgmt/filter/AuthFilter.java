package com.leavemgmt.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.leavemgmt.util.SessionHelper;

import java.io.IOException;

/**
 * Filter to check if user is authenticated
 * Protects pages that require login
 */
public class AuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Check if user is logged in
        if (!SessionHelper.isLoggedIn(httpRequest)) {
            // User not logged in - redirect to login page
            String contextPath = httpRequest.getContextPath();
            
            // Save the original requested URL
            String requestedUrl = httpRequest.getRequestURI();
            String queryString = httpRequest.getQueryString();
            if (queryString != null) {
                requestedUrl += "?" + queryString;
            }
            
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("redirectAfterLogin", requestedUrl);
            
            SessionHelper.setError(httpRequest, "Vui lòng đăng nhập để tiếp tục");
            httpResponse.sendRedirect(contextPath + "/login.jsp");
            return;
        }
        
        // User is logged in - continue
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        System.out.println("AuthFilter destroyed");
    }
}