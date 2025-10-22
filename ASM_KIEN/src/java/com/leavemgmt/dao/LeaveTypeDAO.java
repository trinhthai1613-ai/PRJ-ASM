package com.leavemgmt.dao;

import com.leavemgmt.model.LeaveType;
import com.leavemgmt.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveTypeDAO {
    
    /**
     * Get all active leave types
     */
    public List<LeaveType> getAllActiveLeaveTypes() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<LeaveType> leaveTypes = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT LeaveTypeID, LeaveTypeName, LeaveTypeCode, " +
                        "Description, MaxDaysPerYear, RequiresApproval, IsActive " +
                        "FROM LeaveTypes " +
                        "WHERE IsActive = 1 " +
                        "ORDER BY LeaveTypeName";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                LeaveType leaveType = new LeaveType();
                leaveType.setLeaveTypeId(rs.getInt("LeaveTypeID"));
                leaveType.setLeaveTypeName(rs.getString("LeaveTypeName"));
                leaveType.setLeaveTypeCode(rs.getString("LeaveTypeCode"));
                leaveType.setDescription(rs.getString("Description"));
                leaveType.setMaxDaysPerYear(rs.getInt("MaxDaysPerYear"));
                leaveType.setRequiresApproval(rs.getBoolean("RequiresApproval"));
                leaveType.setActive(rs.getBoolean("IsActive"));
                leaveTypes.add(leaveType);
            }
            
            return leaveTypes;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    /**
     * Get leave type by ID
     */
    public LeaveType getLeaveTypeById(int leaveTypeId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT * FROM LeaveTypes WHERE LeaveTypeID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, leaveTypeId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                LeaveType leaveType = new LeaveType();
                leaveType.setLeaveTypeId(rs.getInt("LeaveTypeID"));
                leaveType.setLeaveTypeName(rs.getString("LeaveTypeName"));
                leaveType.setLeaveTypeCode(rs.getString("LeaveTypeCode"));
                leaveType.setDescription(rs.getString("Description"));
                leaveType.setMaxDaysPerYear(rs.getInt("MaxDaysPerYear"));
                leaveType.setRequiresApproval(rs.getBoolean("RequiresApproval"));
                leaveType.setActive(rs.getBoolean("IsActive"));
                return leaveType;
            }
            
            return null;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
}