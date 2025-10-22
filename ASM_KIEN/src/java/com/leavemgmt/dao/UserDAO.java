package com.leavemgmt.dao;

import com.leavemgmt.model.User;
import com.leavemgmt.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    /**
     * Login - Call stored procedure sp_Login
     */
    public User login(String username, String password, String ipAddress) throws SQLException {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Call sp_Login stored procedure
            String sql = "{call sp_Login(?, ?, ?)}";
            stmt = conn.prepareCall(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);  // In production, password should be hashed
            stmt.setString(3, ipAddress);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setFullName(rs.getString("FullName"));
                user.setEmployeeCode(rs.getString("EmployeeCode"));
                user.setDivisionName(rs.getString("DivisionName"));
                
                // Parse roles (comma-separated string)
                String rolesStr = rs.getString("Roles");
                if (rolesStr != null && !rolesStr.isEmpty()) {
                    List<String> roles = new ArrayList<>();
                    for (String role : rolesStr.split(",")) {
                        roles.add(role.trim());
                    }
                    user.setRoles(roles);
                }
                
                return user;
            }
            
            return null;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT u.UserID, u.Username, u.Email, u.FullName, " +
                        "u.PhoneNumber, u.EmployeeCode, u.DivisionID, " +
                        "d.DivisionName, u.ManagerID, m.FullName AS ManagerName, " +
                        "u.IsActive " +
                        "FROM Users u " +
                        "INNER JOIN Divisions d ON u.DivisionID = d.DivisionID " +
                        "LEFT JOIN Users m ON u.ManagerID = m.UserID " +
                        "WHERE u.UserID = ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUser(rs);
            }
            
            return null;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    /**
     * Get all users in a division
     */
    public List<User> getUsersByDivision(int divisionId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT u.UserID, u.Username, u.FullName, u.EmployeeCode, " +
                        "d.DivisionName, u.IsActive " +
                        "FROM Users u " +
                        "INNER JOIN Divisions d ON u.DivisionID = d.DivisionID " +
                        "WHERE u.DivisionID = ? AND u.IsActive = 1 " +
                        "ORDER BY u.FullName";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, divisionId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setFullName(rs.getString("FullName"));
                user.setEmployeeCode(rs.getString("EmployeeCode"));
                user.setDivisionName(rs.getString("DivisionName"));
                user.setActive(rs.getBoolean("IsActive"));
                users.add(user);
            }
            
            return users;
            
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    /**
     * Extract User object from ResultSet
     */
    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("UserID"));
        user.setUsername(rs.getString("Username"));
        user.setEmail(rs.getString("Email"));
        user.setFullName(rs.getString("FullName"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));
        user.setEmployeeCode(rs.getString("EmployeeCode"));
        user.setDivisionId(rs.getInt("DivisionID"));
        user.setDivisionName(rs.getString("DivisionName"));
        
        int managerId = rs.getInt("ManagerID");
        if (!rs.wasNull()) {
            user.setManagerId(managerId);
            user.setManagerName(rs.getString("ManagerName"));
        }
        
        user.setActive(rs.getBoolean("IsActive"));
        return user;
    }
}