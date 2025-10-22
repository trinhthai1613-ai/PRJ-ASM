<%-- ===============================================
FILE: web/request-list.jsp
Xem danh sách đơn nghỉ phép (của mình và cấp dưới)
=============================================== --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.leavemgmt.model.User" %>
<%@ page import="com.leavemgmt.model.LeaveRequest" %>
<%@ page import="com.leavemgmt.util.SessionHelper" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("pageTitle", "Danh sách đơn nghỉ phép");
    User currentUser = SessionHelper.getCurrentUser(request);
    
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    String contextPath = request.getContextPath();
    String successMessage = SessionHelper.getSuccess(request);
    String errorMessage = SessionHelper.getError(request);
    
    @SuppressWarnings("unchecked")
    List<LeaveRequest> requests = (List<LeaveRequest>) request.getAttribute("requests");
    
    boolean isManager = currentUser.getRoles() != null && 
                       (currentUser.getRoles().contains("Trưởng nhóm") || 
                        currentUser.getRoles().contains("Trưởng phòng") ||
                        currentUser.getRoles().contains("Giám đốc"));
%>

<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/nav.jsp" />

<main class="main-content">
    <div class="container">
        
        <div class="page-header">
            <h2>📄 Danh sách đơn xin nghỉ phép</h2>
            <a href="<%= contextPath %>/request/create" class="btn btn-primary">
                