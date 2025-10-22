package com.leavemgmt.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.leavemgmt.dao.LeaveRequestDAO;
import com.leavemgmt.model.User;
import com.leavemgmt.util.SessionHelper;
import com.leavemgmt.util.StringUtil;

import java.io.IOException;

public class CancelRequestServlet extends HttpServlet {
    
    private LeaveRequestDAO requestDAO;
    
    @Override
    public void init() throws ServletException {
        requestDAO = new LeaveRequestDAO();
        System.out.println("CancelRequestServlet initialized");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        User currentUser = SessionHelper.getCurrentUser(request);
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String requestIdStr = request.getParameter("requestId");
        String cancelReason = request.getParameter("cancelReason");
        
        if (StringUtil.isEmpty(requestIdStr)) {
            SessionHelper.setError(request, "ID đơn không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/request/list");
            return;
        }
        
        try {
            int requestId = Integer.parseInt(requestIdStr);
            
            // Cancel request (calls sp_CancelLeaveRequest)
            requestDAO.cancelLeaveRequest(
                requestId,
                currentUser.getUserId(),
                cancelReason
            );
            
            SessionHelper.setSuccess(request, "Đã hủy đơn thành công");
            response.sendRedirect(request.getContextPath() + "/request/list");
            
        } catch (Exception e) {
            e.printStackTrace();
            SessionHelper.setError(request, "Lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/request/list");
        }
    }
}