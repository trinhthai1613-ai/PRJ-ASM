package com.leavemgmt.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.leavemgmt.dao.UserDAO;
import com.leavemgmt.model.User;
import com.leavemgmt.util.SessionHelper;
import com.leavemgmt.util.StringUtil;

import java.io.IOException;

public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        System.out.println("LoginServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // If already logged in, redirect to home
        if (SessionHelper.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        // Show login page
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validation
        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            SessionHelper.setError(request, "Vui lòng nhập đầy đủ thông tin");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // Get client IP
            String ipAddress = request.getRemoteAddr();
            
            // Call DAO to login (calls sp_Login stored procedure)
            User user = userDAO.login(username, password, ipAddress);
            
            if (user != null) {
                // Login successful
                SessionHelper.setCurrentUser(request, user);
                SessionHelper.setSuccess(request, "Đăng nhập thành công! Chào mừng " + user.getFullName());
                
                // Check if there's a redirect URL
                HttpSession session = request.getSession(false);
                String redirectUrl = null;
                if (session != null) {
                    redirectUrl = (String) session.getAttribute("redirectAfterLogin");
                    session.removeAttribute("redirectAfterLogin");
                }
                
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/home");
                }
                
            } else {
                // Login failed
                SessionHelper.setError(request, "Tên đăng nhập hoặc mật khẩu không đúng");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            SessionHelper.setError(request, "Lỗi hệ thống: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}