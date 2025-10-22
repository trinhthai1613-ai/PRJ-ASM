package com.leavemgmt.dao;

import com.leavemgmt.model.LeaveBalance;
import com.leavemgmt.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveBalanceDAO {
    
    /**
     * Get leave balance for user - Call sp_GetLeaveBalance
     */
    public List<LeaveBalance> getLeaveBalance(int userId, int year) throws SQLException {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<LeaveBalance> balances = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "{call sp_GetLeaveBalance(?, ?)}";
            stmt = conn.prepareCall(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, year);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                LeaveBalance balance = new LeaveBalance();
                balance.setBalanceId(rs.getInt("BalanceID"));
                balance.setLeaveTypeName(rs.getString("LeaveTypeName"));
                balance.setYear(rs.getInt("Year"));
                balance.setTotalDays(rs.getInt("TotalDays"));
                balance.setUsedDays(rs.getInt("UsedDays"));
                balance.setRemainingDays(rs.getInt("RemainingDays"));
                balances.add(balance);
            }
            
            return balances;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
}