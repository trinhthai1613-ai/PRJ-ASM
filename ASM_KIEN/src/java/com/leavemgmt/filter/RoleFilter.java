package com.leavemgmt.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.leavemgmt.model.User;
import com.leavemgmt.util.SessionHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter to check if user has required roles
 * For role-based access control
 */
public class RoleFilter implements Filter {
    
    private List<String> allowedRoles;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String roles = filterConfig.getInitParameter("allowedRoles");
        if (roles != null && !roles.isEmpty()) {
            allowedRoles = Arrays.asList(roles.split(","));
            System.out.println("RoleFilter initialized with roles: " + allowedRoles);
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        User currentUser = SessionHelper.getCurrentUser(httpRequest);
        
        if (currentUser == null) {
            // Not logged in
            SessionHelper.setError(httpRequest, "Vui lòng đăng nhập");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }
        
        // Check if user has any of the allowed roles
        if (allowedRoles != null && !allowedRoles.isEmpty()) {
            boolean hasRole = false;
            
            if (currentUser.getRoles() != null) {
                for (String userRole : currentUser.getRoles()) {
                    if (allowedRoles.contains(userRole.trim())) {
                        hasRole = true;
                        break;
                    }
                }
            }
            
            if (!hasRole) {
                // User doesn't have required role
                SessionHelper.setError(httpRequest, 
                    "Bạn không có quyền truy cập tính năng này");
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
                return;
            }
        }
        
        // User has required role - continue
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        System.out.println("RoleFilter destroyed");
    }
}
