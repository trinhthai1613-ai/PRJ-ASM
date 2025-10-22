package com.leavemgmt.dao;

import com.leavemgmt.model.LeaveRequest;
import com.leavemgmt.util.DBConnection;
import com.leavemgmt.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {
    
    /**
     * Create leave request - Call sp_CreateLeaveRequest
     */
    public int createLeaveRequest(int userId, int leaveTypeId, Date fromDate, 
                                  Date toDate, String reason) throws SQLException {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "{call sp_CreateLeaveRequest(?, ?, ?, ?, ?)}";
            stmt = conn.prepareCall(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, leaveTypeId);
            stmt.setDate(3, fromDate);
            stmt.setDate(4, toDate);
            stmt.setString(5, reason);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("RequestID");
            }
            
            return -1;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    /**
     * Get requests for user and subordinates - Call sp_GetLeaveRequestsByUser
     */
    public List<LeaveRequest> getRequestsByUser(int userId, Date fromDate, 
                                                 Date toDate, Integer statusId) throws SQLException {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<LeaveRequest> requests = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "{call sp_GetLeaveRequestsByUser(?, ?, ?, ?)}";
            stmt = conn.prepareCall(sql);
            stmt.setInt(1, userId);
            
            if (fromDate != null) {
                stmt.setDate(2, fromDate);
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            if (toDate != null) {
                stmt.setDate(3, toDate);
            } else {
                stmt.setNull(3, Types.DATE);
            }
            
            if (statusId != null) {
                stmt.setInt(4, statusId);
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                requests.add(extractLeaveRequest(rs));
            }
            
            return requests;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    /**
     * Get request by ID
     */
    public LeaveRequest getRequestById(int requestId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT * FROM vw_LeaveRequestDetails WHERE RequestID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, requestId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractLeaveRequest(rs);
            }
            
            return null;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    /**
     * Process leave request (approve/reject) - Call sp_ProcessLeaveRequest
     */
    public void processLeaveRequest(int requestId, int processedBy, 
                                    int statusId, String processNote) throws SQLException {
        Connection conn = null;
        CallableStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "{call sp_ProcessLeaveRequest(?, ?, ?, ?)}";
            stmt = conn.prepareCall(sql);
            stmt.setInt(1, requestId);
            stmt.setInt(2, processedBy);
            stmt.setInt(3, statusId);  // 2=Approved, 3=Rejected
            
            if (processNote != null && !processNote.isEmpty()) {
                stmt.setString(4, processNote);
            } else {
                stmt.setNull(4, Types.NVARCHAR);
            }
            
            stmt.execute();
            
        } finally {
            DBConnection.close(stmt, conn);
        }
    }
    
    /**
     * Cancel leave request - Call sp_CancelLeaveRequest
     */
    public void cancelLeaveRequest(int requestId, int userId, String cancelReason) throws SQLException {
        Connection conn = null;
        CallableStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "{call sp_CancelLeaveRequest(?, ?, ?)}";
            stmt = conn.prepareCall(sql);
            stmt.setInt(1, requestId);
            stmt.setInt(2, userId);
            
            if (cancelReason != null && !cancelReason.isEmpty()) {
                stmt.setString(3, cancelReason);
            } else {
                stmt.setNull(3, Types.NVARCHAR);
            }
            
            stmt.execute();
            
        } finally {
            DBConnection.close(stmt, conn);
        }
    }
    
    /**
     * Extract LeaveRequest from ResultSet
     */
    private LeaveRequest extractLeaveRequest(ResultSet rs) throws SQLException {
        LeaveRequest request = new LeaveRequest();
        request.setRequestId(rs.getInt("RequestID"));
        request.setUserId(rs.getInt("UserID"));
        request.setCreatedByName(rs.getString("CreatedBy"));
        request.setEmployeeCode(rs.getString("EmployeeCode"));
        request.setLeaveTypeId(rs.getInt("LeaveTypeID"));
        request.setLeaveTypeName(rs.getString("LeaveTypeName"));
        
        Date fromDate = rs.getDate("FromDate");
        if (fromDate != null) {
            request.setFromDate(DateUtil.toLocalDate(fromDate));
        }
        
        Date toDate = rs.getDate("ToDate");
        if (toDate != null) {
            request.setToDate(DateUtil.toLocalDate(toDate));
        }
        
        request.setTotalDays(rs.getInt("TotalDays"));
        request.setReason(rs.getString("Reason"));
        request.setStatusId(rs.getInt("StatusID"));
        request.setStatusName(rs.getString("Status"));
        request.setStatusCode(rs.getString("StatusCode"));
        
        int processedBy = rs.getInt("ProcessedBy");
        if (!rs.wasNull()) {
            request.setProcessedBy(processedBy);
            request.setProcessedByName(rs.getString("ProcessedBy"));
        }
        
        request.setProcessNote(rs.getString("ProcessNote"));
        
        Date createdAt = rs.getDate("CreatedAt");
        if (createdAt != null) {
            request.setCreatedAt(DateUtil.toLocalDate(createdAt));
        }
        
        Date processedAt = rs.getDate("ProcessedAt");
        if (processedAt != null) {
            request.setProcessedAt(DateUtil.toLocalDate(processedAt));
        }
        
        return request;
    }
}