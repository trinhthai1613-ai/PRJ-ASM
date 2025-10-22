package com.leavemgmt.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Filter to add security headers
 * Prevent XSS, clickjacking, etc.
 */
public class SecurityFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("SecurityFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Prevent XSS attacks
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Prevent clickjacking
        httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
        
        // Prevent MIME sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // Disable caching for sensitive pages
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Expires", "0");
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        System.out.println("SecurityFilter destroyed");
    }
}