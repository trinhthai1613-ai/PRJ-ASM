<%-- ===============================================
FILE: web/home.jsp
Trang chủ / Dashboard
=============================================== --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.leavemgmt.model.User" %>
<%@ page import="com.leavemgmt.model.LeaveBalance" %>
<%@ page import="com.leavemgmt.model.LeaveRequest" %>
<%@ page import="com.leavemgmt.util.SessionHelper" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("pageTitle", "Trang chủ - Leave Management");
    User currentUser = SessionHelper.getCurrentUser(request);
    
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    String contextPath = request.getContextPath();
    String successMessage = SessionHelper.getSuccess(request);
    String errorMessage = SessionHelper.getError(request);
    
    @SuppressWarnings("unchecked")
    List<LeaveBalance> leaveBalances = (List<LeaveBalance>) request.getAttribute("leaveBalances");
    
    @SuppressWarnings("unchecked")
    List<LeaveRequest> recentRequests = (List<LeaveRequest>) request.getAttribute("recentRequests");
%>

<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/nav.jsp" />

<main class="main-content">
    <div class="container">
        
        <% if (successMessage != null) { %>
            <div class="alert alert-success">
                <strong>✓</strong> <%= successMessage %>
            </div>
        <% } %>
        
        <% if (errorMessage != null) { %>
            <div class="alert alert-error">
                <strong>⚠️</strong> <%= errorMessage %>
            </div>
        <% } %>
        
        <!-- Welcome Section -->
        <section class="welcome-section">
            <h2>Chào mừng, <%= currentUser.getFullName() %>! 👋</h2>
            <div class="user-details">
                <p><strong>Mã nhân viên:</strong> <%= currentUser.getEmployeeCode() %></p>
                <p><strong>Phòng ban:</strong> <%= currentUser.getDivisionName() %></p>
                <% if (currentUser.getRoles() != null && !currentUser.getRoles().isEmpty()) { %>
                    <p><strong>Vai trò:</strong> 
                        <% for (int i = 0; i < currentUser.getRoles().size(); i++) { %>
                            <span class="badge"><%= currentUser.getRoles().get(i) %></span>
                            <%= (i < currentUser.getRoles().size() - 1) ? ", " : "" %>
                        <% } %>
                    </p>
                <% } %>
            </div>
        </section>
        
        <!-- Dashboard Grid -->
        <div class="dashboard-grid">
            
            <!-- Leave Balance Card -->
            <div class="card">
                <div class="card-header">
                    <h3>📊 Số ngày phép còn lại</h3>
                </div>
                <div class="card-body">
                    <% if (leaveBalances != null && !leaveBalances.isEmpty()) { %>
                        <table class="balance-table">
                            <thead>
                                <tr>
                                    <th>Loại phép</th>
                                    <th>Tổng</th>
                                    <th>Đã dùng</th>
                                    <th>Còn lại</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (LeaveBalance balance : leaveBalances) { %>
                                    <tr>
                                        <td><strong><%= balance.getLeaveTypeName() %></strong></td>
                                        <td><%= balance.getTotalDays() %> ngày</td>
                                        <td><%= balance.getUsedDays() %> ngày</td>
                                        <td>
                                            <span class="remaining-days <%= balance.getRemainingDays() < 3 ? "low" : "" %>">
                                                <%= balance.getRemainingDays() %> ngày
                                            </span>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    <% } else { %>
                        <p class="no-data">Không có dữ liệu số ngày phép</p>
                    <% } %>
                </div>
            </div>
            
            <!-- Recent Requests Card -->
            <div class="card">
                <div class="card-header">
                    <h3>📝 Đơn xin nghỉ gần đây</h3>
                    <a href="<%= contextPath %>/request/list" class="view-all">Xem tất cả →</a>
                </div>
                <div class="card-body">
                    <% if (recentRequests != null && !recentRequests.isEmpty()) { %>
                        <div class="requests-list">
                            <% for (LeaveRequest req : recentRequests) { %>
                                <div class="request-item">
                                    <div class="request-info">
                                        <strong><%= req.getLeaveTypeName() %></strong>
                                        <span class="request-date">
                                            <%= req.getFromDate() %> → <%= req.getToDate() %>
                                            (<%= req.getTotalDays() %> ngày)
                                        </span>
                                        <span class="status-badge status-<%= req.getStatusCode().toLowerCase() %>">
                                            <%= req.getStatusName() %>
                                        </span>
                                    </div>
                                </div>
                            <% } %>
                        </div>
                    <% } else { %>
                        <p class="no-data">Chưa có đơn xin nghỉ nào</p>
                    <% } %>
                </div>
            </div>
            
        </div>
        
        <!-- Quick Actions -->
        <section class="quick-actions">
            <h3>⚡ Thao tác nhanh</h3>
            <div class="action-buttons">
                <a href="<%= contextPath %>/request/create" class="btn btn-primary">
                    ➕ Tạo đơn xin nghỉ phép
                </a>
                <a href="<%= contextPath %>/request/list" class="btn btn-secondary">
                    📄 Xem tất cả đơn
                </a>
                <% if (currentUser.getRoles() != null && 
                      (currentUser.getRoles().contains("Trưởng nhóm") || 
                       currentUser.getRoles().contains("Trưởng phòng"))) { %>
                    <a href="<%= contextPath %>/request/review" class="btn btn-warning">
                        ✅ Duyệt đơn của cấp dưới
                    </a>
                <% } %>
            </div>
        </section>
        
    </div>
</main>

<jsp:include page="/includes/footer.jsp" />