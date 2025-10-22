package com.leavemgmt.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.leavemgmt.dao.LeaveRequestDAO;
import com.leavemgmt.model.LeaveRequest;
import com.leavemgmt.model.User;
import com.leavemgmt.util.SessionHelper;

import java.io.IOException;
import java.util.List;

public class ViewRequestsServlet extends HttpServlet {
    
    private LeaveRequestDAO requestDAO;
    
    @Override
    public void init() throws ServletException {
        requestDAO = new LeaveRequestDAO();
        System.out.println("ViewRequestsServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        User currentUser = SessionHelper.getCurrentUser(request);
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // Get all requests for user and subordinates
            // Calls sp_GetLeaveRequestsByUser stored procedure
            List<LeaveRequest> requests = requestDAO.getRequestsByUser(
                currentUser.getUserId(), null, null, null);
            
            request.setAttribute("requests", requests);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tải danh sách đơn: " + e.getMessage());
        }
        
        // Forward to view requests page
        request.getRequestDispatcher("/request-list.jsp").forward(request, response);
    }
}