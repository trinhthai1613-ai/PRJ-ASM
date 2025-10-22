package com.leavemgmt.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.leavemgmt.dao.LeaveBalanceDAO;
import com.leavemgmt.dao.LeaveRequestDAO;
import com.leavemgmt.model.LeaveBalance;
import com.leavemgmt.model.LeaveRequest;
import com.leavemgmt.model.User;
import com.leavemgmt.util.SessionHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class HomeServlet extends HttpServlet {
    
    private LeaveBalanceDAO balanceDAO;
    private LeaveRequestDAO requestDAO;
    
    @Override
    public void init() throws ServletException {
        balanceDAO = new LeaveBalanceDAO();
        requestDAO = new LeaveRequestDAO();
        System.out.println("HomeServlet initialized");
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
            // Get leave balance for current year
            int currentYear = LocalDate.now().getYear();
            List<LeaveBalance> balances = balanceDAO.getLeaveBalance(
                currentUser.getUserId(), currentYear);
            request.setAttribute("leaveBalances", balances);
            
            // Get recent leave requests
            List<LeaveRequest> recentRequests = requestDAO.getRequestsByUser(
                currentUser.getUserId(), null, null, null);
            
            // Limit to 5 most recent
            if (recentRequests.size() > 5) {
                recentRequests = recentRequests.subList(0, 5);
            }
            request.setAttribute("recentRequests", recentRequests);
            
            // Count pending requests for subordinates (if manager)
            // TODO: Add count of pending approvals
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
        
        // Forward to home page
        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }
}