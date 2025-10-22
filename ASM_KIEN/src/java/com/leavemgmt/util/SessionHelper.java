package com.leavemgmt.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.leavemgmt.model.User;

/**
 * Helper class for managing HTTP sessions
 */
public class SessionHelper {
    
    // Session attribute keys
    public static final String ATTR_USER = "currentUser";
    public static final String ATTR_TOKEN = "sessionToken";
    public static final String ATTR_ERROR = "errorMessage";
    public static final String ATTR_SUCCESS = "successMessage";
    
    /**
     * Get current logged-in user from session
     */
    public static User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute(ATTR_USER);
        }
        return null;
    }
    
    /**
     * Set current user in session
     */
    public static void setCurrentUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(ATTR_USER, user);
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }
    
    /**
     * Get session token
     */
    public static String getSessionToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute(ATTR_TOKEN);
        }
        return null;
    }
    
    /**
     * Set session token
     */
    public static void setSessionToken(HttpServletRequest request, String token) {
        HttpSession session = request.getSession(true);
        session.setAttribute(ATTR_TOKEN, token);
    }
    
    /**
     * Logout - invalidate session
     */
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
    
    /**
     * Set error message in session
     */
    public static void setError(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(ATTR_ERROR, message);
    }
    
    /**
     * Get and clear error message
     */
    public static String getError(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String msg = (String) session.getAttribute(ATTR_ERROR);
            session.removeAttribute(ATTR_ERROR);
            return msg;
        }
        return null;
    }
    
    /**
     * Set success message in session
     */
    public static void setSuccess(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(ATTR_SUCCESS, message);
    }
    
    /**
     * Get and clear success message
     */
    public static String getSuccess(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String msg = (String) session.getAttribute(ATTR_SUCCESS);
            session.removeAttribute(ATTR_SUCCESS);
            return msg;
        }
        return null;
    }
    
    /**
     * Check if user has a specific role
     */
    public static boolean hasRole(HttpServletRequest request, String roleName) {
        User user = getCurrentUser(request);
        if (user != null && user.getRoles() != null) {
            return user.getRoles().contains(roleName);
        }
        return false;
    }
}