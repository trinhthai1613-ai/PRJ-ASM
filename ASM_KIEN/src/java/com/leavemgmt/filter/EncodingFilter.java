package com.leavemgmt.filter;

import jakarta.servlet.*;
import java.io.IOException;

/**
 * Filter to set UTF-8 encoding for all requests and responses
 */
public class EncodingFilter implements Filter {
    
    private String encoding = "UTF-8";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String encodingParam = filterConfig.getInitParameter("encoding");
        if (encodingParam != null) {
            encoding = encodingParam;
        }
        System.out.println("EncodingFilter initialized with encoding: " + encoding);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        // Set request encoding
        request.setCharacterEncoding(encoding);
        
        // Set response encoding
        response.setCharacterEncoding(encoding);
        response.setContentType("text/html; charset=" + encoding);
        
        // Continue the request
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        System.out.println("EncodingFilter destroyed");
    }
}