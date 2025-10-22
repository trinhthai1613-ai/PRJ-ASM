<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.leavemgmt.model.User" %>
<%@ page import="com.leavemgmt.util.SessionHelper" %>
<%
    String contextPath = request.getContextPath();
    User currentUser = SessionHelper.getCurrentUser(request);
    String currentPath = request.getRequestURI();
%>

<% if (currentUser != null) { %>
<nav class="main-nav">
    <div class="container">
        <ul class="nav-menu">
            <li><a href="<%= contextPath %>/home" class="<%= currentPath.contains("/home") ? "active" : "" %>">🏠 Trang chủ</a></li>
            <li><a href="<%= contextPath %>/request/create" class="<%= currentPath.contains("/request/create") ? "active" : "" %>">➕ Tạo đơn</a></li>
            <li><a href="<%= contextPath %>/request/list" class="<%= currentPath.contains("/request/list") ? "active" : "" %>">📄 Xem đơn</a></li>
            
            <% 
            // Check if user has manager role
            if (currentUser.getRoles() != null && 
                (currentUser.getRoles().contains("Trưởng nhóm") || 
                 currentUser.getRoles().contains("Trưởng phòng") ||
                 currentUser.getRoles().contains("Giám đốc"))) { 
            %>
                <li><a href="<%= contextPath %>/request/review" class="<%= currentPath.contains("/request/review") ? "active" : "" %>">✅ Duyệt đơn</a></li>
            <% } %>
            
            <% 
            // Check if user is division leader
            if (currentUser.getRoles() != null && 
                (currentUser.getRoles().contains("Trưởng phòng") ||
                 currentUser.getRoles().contains("Giám đốc"))) { 
            %>
                <li><a href="<%= contextPath %>/agenda/view" class="<%= currentPath.contains("/agenda") ? "active" : "" %>">📅 Agenda</a></li>
            <% } %>
            
            <li class="nav-right"><a href="<%= contextPath %>/logout" class="logout-btn">🚪 Đăng xuất</a></li>
        </ul>
    </div>
</nav>
<% } %>