package com.leavemgmt.util;

/**
 * String utility methods
 */
public class StringUtil {
    
    /**
     * Check if string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if string is not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * Safe trim
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }
    
    /**
     * Get string or default value
     */
    public static String orDefault(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }
    
    /**
     * Escape HTML to prevent XSS
     */
    public static String escapeHtml(String str) {
        if (str == null) return null;
        
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#x27;");
    }
}