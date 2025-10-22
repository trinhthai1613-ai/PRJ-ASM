package com.leavemgmt.dao;

import com.leavemgmt.model.AgendaItem;
import com.leavemgmt.util.DBConnection;
import com.leavemgmt.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendaDAO {
    
    /**
     * Get division agenda - Call sp_GetDivisionAgenda
     */
    public List<AgendaItem> getDivisionAgenda(int divisionId, Date fromDate, 
                                              Date toDate) throws SQLException {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<AgendaItem> agenda = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "{call sp_GetDivisionAgenda(?, ?, ?)}";
            stmt = conn.prepareCall(sql);
            stmt.setInt(1, divisionId);
            stmt.setDate(2, fromDate);
            stmt.setDate(3, toDate);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                AgendaItem item = new AgendaItem();
                item.setUserId(rs.getInt("UserID"));
                item.setFullName(rs.getString("FullName"));
                item.setEmployeeCode(rs.getString("EmployeeCode"));
                
                Date workDate = rs.getDate("WorkDate");
                if (workDate != null) {
                    item.setWorkDate(DateUtil.toLocalDate(workDate));
                }
                
                item.setStatus(rs.getString("Status"));
                item.setLeaveTypeName(rs.getString("LeaveTypeName"));
                item.setReason(rs.getString("Reason"));
                
                agenda.add(item);
            }
            
            return agenda;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
}
