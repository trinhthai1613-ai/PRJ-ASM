package com.leavemgmt.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Pure JDBC Database Connection Utility
 * NO FRAMEWORKS - Direct JDBC Connection
 */
public class DBConnection {
    
    private static String driver;
    private static String url;
    private static String username;
    private static String password;
    
    // Static block to load config and driver
    static {
        try {
            loadConfig();
            Class.forName(driver);
            System.out.println("✓ SQL Server JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ SQL Server JDBC Driver NOT FOUND!");
            System.err.println("  Download from: https://learn.microsoft.com/en-us/sql/connect/jdbc/");
            throw new RuntimeException("JDBC Driver not found", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    /**
     * Load database configuration from properties file
     */
    private static void loadConfig() throws Exception {
        Properties props = new Properties();
        InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties");
        
        if (input == null) {
            throw new Exception("db.properties file not found!");
        }
        
        props.load(input);
        
        driver = props.getProperty("db.driver");
        url = props.getProperty("db.url");
        username = props.getProperty("db.username");
        password = props.getProperty("db.password");
        
        input.close();
        
        System.out.println("✓ Database configuration loaded");
        System.out.println("  URL: " + url);
    }
    
    /**
     * Get a new database connection
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    
    /**
     * Close ResultSet, Statement and Connection
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    /**
     * Close Statement and Connection
     */
    public static void close(Statement stmt, Connection conn) {
        close(null, stmt, conn);
    }
    
    /**
     * Close Connection only
     */
    public static void close(Connection conn) {
        close(null, null, conn);
    }
    
    /**
     * Test database connection (Main method for testing)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Testing Database Connection...");
        System.out.println("========================================");
        
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                
                // Test query
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM Users");
                if (rs.next()) {
                    System.out.println("✓ Found " + rs.getInt("total") + " users in database");
                }
                close(rs, stmt, null);
            }
        } catch (SQLException e) {
            System.err.println("✗ Database connection FAILED!");
            System.err.println("  Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close(conn);
        }
        
        System.out.println("========================================");
    }
}