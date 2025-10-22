package com.leavemgmt.dao;

import com.leavemgmt.model.Division;
import com.leavemgmt.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DivisionDAO {
    
    /**
     * Get all active divisions
     */
    public List<Division> getAllActiveDivisions() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Division> divisions = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT DivisionID, DivisionName, DivisionCode, " +
                        "Description, IsActive " +
                        "FROM Divisions " +
                        "WHERE IsActive = 1 " +
                        "ORDER BY DivisionName";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Division division = new Division();
                division.setDivisionId(rs.getInt("DivisionID"));
                division.setDivisionName(rs.getString("DivisionName"));
                division.setDivisionCode(rs.getString("DivisionCode"));
                division.setDescription(rs.getString("Description"));
                division.setActive(rs.getBoolean("IsActive"));
                divisions.add(division);
            }
            
            return divisions;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    /**
     * Get division by ID
     */
    public Division getDivisionById(int divisionId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT * FROM Divisions WHERE DivisionID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, divisionId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Division division = new Division();
                division.setDivisionId(rs.getInt("DivisionID"));
                division.setDivisionName(rs.getString("DivisionName"));
                division.setDivisionCode(rs.getString("DivisionCode"));
                division.setDescription(rs.getString("Description"));
                division.setActive(rs.getBoolean("IsActive"));
                return division;
            }
            
            return null;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
}