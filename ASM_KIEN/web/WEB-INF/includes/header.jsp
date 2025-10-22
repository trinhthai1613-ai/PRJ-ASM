<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.leavemgmt.model.User" %>
<%@ page import="com.leavemgmt.util.SessionHelper" %>
<%
    String contextPath = request.getContextPath();
    User currentUser = SessionHelper.getCurrentUser(request);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= request.getAttribute("pageTitle") != null ? request.getAttribute("pageTitle") : "Leave Management System" %></title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
</head>
<body>
    <header class="main-header">
        <div class="container">
            <div class="header-content">
                <h1 class="logo">📋 Leave Management</h1>
                <% if (currentUser != null) { %>
                    <div class="user-info">
                        <span class="user-name">👤 <%= currentUser.getFullName() %></span>
                        <span class="user-division">🏢 <%= currentUser.getDivisionName() %></span>
                    </div>
                <% } %>
            </div>
        </div>
    </header>