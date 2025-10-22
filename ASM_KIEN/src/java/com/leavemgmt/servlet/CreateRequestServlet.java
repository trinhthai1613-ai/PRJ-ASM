package com.leavemgmt.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.leavemgmt.dao.LeaveRequestDAO;
import com.leavemgmt.dao.LeaveTypeDAO;
import com.leavemgmt.model.LeaveType;
import com.leavemgmt.model.User;
import com.leavemgmt.util.DateUtil;
import com.leavemgmt.util.SessionHelper;
import com.leavemgmt.util.StringUtil;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class CreateRequestServlet extends HttpServlet {
    
    private LeaveTypeDAO leaveTypeDAO;
    private LeaveRequestDAO requestDAO;
    
    @Override
    public void init() throws ServletException {
        leaveTypeDAO = new LeaveTypeDAO();
        requestDAO = new LeaveRequestDAO();
        System.out.println("CreateRequestServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Load leave types for dropdown
            List<LeaveType> leaveTypes = leaveTypeDAO.getAllActiveLeaveTypes();
            request.setAttribute("leaveTypes", leaveTypes);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
        
        // Forward to create request page
        request.getRequestDispatcher("/request-create.jsp").forward(request, response);
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
        String leaveTypeIdStr = request.getParameter("leaveTypeId");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");
        String reason = request.getParameter("reason");
        
        // Validation
        if (StringUtil.isEmpty(leaveTypeIdStr) || StringUtil.isEmpty(fromDateStr) || 
            StringUtil.isEmpty(toDateStr) || StringUtil.isEmpty(reason)) {
            SessionHelper.setError(request, "Vui lòng điền đầy đủ thông tin");
            response.sendRedirect(request.getContextPath() + "/request/create");
            return;
        }
        
        try {
            int leaveTypeId = Integer.parseInt(leaveTypeIdStr);
            LocalDate fromDate = DateUtil.parseDate(fromDateStr);
            LocalDate toDate = DateUtil.parseDate(toDateStr);
            
            // Validate dates
            if (fromDate == null || toDate == null) {
                SessionHelper.setError(request, "Ngày không hợp lệ");
                response.sendRedirect(request.getContextPath() + "/request/create");
                return;
            }
            
            if (!DateUtil.isValidRange(fromDate, toDate)) {
                SessionHelper.setError(request, "Ngày kết thúc phải sau ngày bắt đầu");
                response.sendRedirect(request.getContextPath() + "/request/create");
                return;
            }
            
            // Create leave request (calls sp_CreateLeaveRequest)
            int requestId = requestDAO.createLeaveRequest(
                currentUser.getUserId(),
                leaveTypeId,
                DateUtil.toSqlDate(fromDate),
                DateUtil.toSqlDate(toDate),
                reason
            );
            
            if (requestId > 0) {
                SessionHelper.setSuccess(request, 
                    "Tạo đơn xin nghỉ phép thành công! Mã đơn: #" + requestId);
                response.sendRedirect(request.getContextPath() + "/request/list");
            } else {
                SessionHelper.setError(request, "Không thể tạo đơn. Vui lòng thử lại");
                response.sendRedirect(request.getContextPath() + "/request/create");
            }
            
        } catch (NumberFormatException e) {
            SessionHelper.setError(request, "Dữ liệu không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/request/create");
        } catch (Exception e) {
            e.printStackTrace();
            SessionHelper.setError(request, "Lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/request/create");
        }
    }
}