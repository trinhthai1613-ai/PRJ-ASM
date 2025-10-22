package com.leavemgmt.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.leavemgmt.dao.LeaveRequestDAO;
import com.leavemgmt.model.LeaveRequest;
import com.leavemgmt.model.User;
import com.leavemgmt.util.SessionHelper;
import com.leavemgmt.util.StringUtil;

import java.io.IOException;

public class ApprovalServlet extends HttpServlet {
    
    private LeaveRequestDAO requestDAO;
    
    @Override
    public void init() throws ServletException {
        requestDAO = new LeaveRequestDAO();
        System.out.println("ApprovalServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        User currentUser = SessionHelper.getCurrentUser(request);
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String requestIdStr = request.getParameter("id");
        
        if (StringUtil.isEmpty(requestIdStr)) {
            SessionHelper.setError(request, "ID đơn không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/request/list");
            return;
        }
        
        try {
            int requestId = Integer.parseInt(requestIdStr);
            
            // Get request details
            LeaveRequest leaveRequest = requestDAO.getRequestById(requestId);
            
            if (leaveRequest == null) {
                SessionHelper.setError(request, "Không tìm thấy đơn nghỉ phép");
                response.sendRedirect(request.getContextPath() + "/request/list");
                return;
            }
            
            // Check if request is still pending
            if (leaveRequest.getStatusId() != 1) {
                SessionHelper.setError(request, "Đơn này đã được xử lý");
                response.sendRedirect(request.getContextPath() + "/request/list");
                return;
            }
            
            request.setAttribute("leaveRequest", leaveRequest);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        // Forward to approval page
        request.getRequestDispatcher("/request-approve.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        User currentUser = SessionHelper.getCurrentUser(request);
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get form parameters
        String requestIdStr = request.getParameter("requestId");
        String action = request.getParameter("action");
        String processNote = request.getParameter("processNote");
        
        if (StringUtil.isEmpty(requestIdStr) || StringUtil.isEmpty(action)) {
            SessionHelper.setError(request, "Dữ liệu không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/request/list");
            return;
        }
        
        try {
            int requestId = Integer.parseInt(requestIdStr);
            int statusId;
            
            if ("approve".equals(action)) {
                statusId = 2; // Approved
            } else if ("reject".equals(action)) {
                statusId = 3; // Rejected
            } else {
                SessionHelper.setError(request, "Hành động không hợp lệ");
                response.sendRedirect(request.getContextPath() + "/request/list");
                return;
            }
            
            // Process request (calls sp_ProcessLeaveRequest)
            requestDAO.processLeaveRequest(
                requestId,
                currentUser.getUserId(),
                statusId,
                processNote
            );
            
            String message = statusId == 2 ? "Đã duyệt đơn thành công" : "Đã từ chối đơn";
            SessionHelper.setSuccess(request, message);
            response.sendRedirect(request.getContextPath() + "/request/list");
            
        } catch (Exception e) {
            e.printStackTrace();
            SessionHelper.setError(request, "Lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/request/list");
        }
    }
}