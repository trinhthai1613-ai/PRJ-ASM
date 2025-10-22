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
                ➕ Tạo đơn mới
            </a>
        </div>
        
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
        
        <!-- Statistics Summary -->
        <% if (requests != null && !requests.isEmpty()) { 
            int totalRequests = requests.size();
            int pendingCount = 0, approvedCount = 0, rejectedCount = 0;
            
            for (LeaveRequest req : requests) {
                if (req.getStatusId() == 1) pendingCount++;
                else if (req.getStatusId() == 2) approvedCount++;
                else if (req.getStatusId() == 3) rejectedCount++;
            }
        %>
            <div class="stats-summary">
                <div class="stat-card">
                    <div class="stat-number"><%= totalRequests %></div>
                    <div class="stat-label">Tổng số đơn</div>
                </div>
                <div class="stat-card pending">
                    <div class="stat-number"><%= pendingCount %></div>
                    <div class="stat-label">Chờ xử lý</div>
                </div>
                <div class="stat-card approved">
                    <div class="stat-number"><%= approvedCount %></div>
                    <div class="stat-label">Đã duyệt</div>
                </div>
                <div class="stat-card rejected">
                    <div class="stat-number"><%= rejectedCount %></div>
                    <div class="stat-label">Từ chối</div>
                </div>
            </div>
        <% } %>
        
        <!-- Requests Table -->
        <div class="table-container">
            <% if (requests != null && !requests.isEmpty()) { %>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Người tạo</th>
                            <th>Loại phép</th>
                            <th>Từ ngày</th>
                            <th>Đến ngày</th>
                            <th>Số ngày</th>
                            <th>Lý do</th>
                            <th>Trạng thái</th>
                            <th>Người duyệt</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (LeaveRequest req : requests) { 
                            boolean isOwner = req.getUserId() == currentUser.getUserId();
                        %>
                            <tr class="<%= isOwner ? "own-request" : "" %>">
                                <td><strong>#<%= req.getRequestId() %></strong></td>
                                <td>
                                    <%= req.getCreatedByName() %>
                                    <% if (isOwner) { %>
                                        <span class="badge badge-info">Của tôi</span>
                                    <% } %>
                                    <br><small><%= req.getEmployeeCode() %></small>
                                </td>
                                <td><%= req.getLeaveTypeName() %></td>
                                <td><%= req.getFromDate() %></td>
                                <td><%= req.getToDate() %></td>
                                <td class="text-center">
                                    <strong><%= req.getTotalDays() %></strong> ngày
                                </td>
                                <td>
                                    <div class="reason-preview" title="<%= req.getReason() %>">
                                        <%= req.getReason().length() > 50 ? 
                                            req.getReason().substring(0, 50) + "..." : 
                                            req.getReason() %>
                                    </div>
                                </td>
                                <td>
                                    <span class="status-badge status-<%= req.getStatusCode().toLowerCase() %>">
                                        <%= req.getStatusName() %>
                                    </span>
                                </td>
                                <td>
                                    <% if (req.getProcessedByName() != null) { %>
                                        <%= req.getProcessedByName() %>
                                        <br><small><%= req.getProcessedAt() %></small>
                                    <% } else { %>
                                        <span class="text-muted">-</span>
                                    <% } %>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <!-- View details button -->
                                        <button class="btn btn-sm btn-info" 
                                                onclick="viewDetails(<%= req.getRequestId() %>)">
                                            👁️ Xem
                                        </button>
                                        
                                        <!-- Approve button (for managers, pending requests only) -->
                                        <% if (isManager && !isOwner && req.getStatusId() == 1) { %>
                                            <a href="<%= contextPath %>/request/review?id=<%= req.getRequestId() %>" 
                                               class="btn btn-sm btn-success">
                                                ✅ Duyệt
                                            </a>
                                        <% } %>
                                        
                                        <!-- Cancel button (for own requests, pending only) -->
                                        <% if (isOwner && req.getStatusId() == 1) { %>
                                            <button class="btn btn-sm btn-danger" 
                                                    onclick="cancelRequest(<%= req.getRequestId() %>)">
                                                ❌ Hủy
                                            </button>
                                        <% } %>
                                    </div>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <div class="no-data-message">
                    <div class="no-data-icon">📭</div>
                    <h3>Chưa có đơn xin nghỉ phép nào</h3>
                    <p>Bạn chưa tạo đơn nào hoặc không có đơn của cấp dưới.</p>
                    <a href="<%= contextPath %>/request/create" class="btn btn-primary">
                        ➕ Tạo đơn đầu tiên
                    </a>
                </div>
            <% } %>
        </div>
        
    </div>
</main>

<!-- Modal for viewing request details -->
<div id="detailsModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <div id="modalBody">
            <!-- Content will be loaded here -->
        </div>
    </div>
</div>

<!-- Hidden form for canceling request -->
<form id="cancelForm" action="<%= contextPath %>/request/cancel" method="post" style="display:none;">
    <input type="hidden" id="cancelRequestId" name="requestId">
    <input type="hidden" name="cancelReason" value="Người dùng hủy đơn">
</form>

<script>
// View request details in modal
function viewDetails(requestId) {
    const modal = document.getElementById('detailsModal');
    const modalBody = document.getElementById('modalBody');
    
    // Find request data from table
    const row = event.target.closest('tr');
    const cells = row.cells;
    
    let detailsHTML = '<h3>Chi tiết đơn #' + requestId + '</h3>';
    detailsHTML += '<table class="details-table">';
    detailsHTML += '<tr><th>Người tạo:</th><td>' + cells[1].textContent.trim() + '</td></tr>';
    detailsHTML += '<tr><th>Loại phép:</th><td>' + cells[2].textContent + '</td></tr>';
    detailsHTML += '<tr><th>Từ ngày:</th><td>' + cells[3].textContent + '</td></tr>';
    detailsHTML += '<tr><th>Đến ngày:</th><td>' + cells[4].textContent + '</td></tr>';
    detailsHTML += '<tr><th>Số ngày:</th><td>' + cells[5].textContent + '</td></tr>';
    detailsHTML += '<tr><th>Lý do:</th><td>' + cells[6].querySelector('.reason-preview').title + '</td></tr>';
    detailsHTML += '<tr><th>Trạng thái:</th><td>' + cells[7].innerHTML + '</td></tr>';
    detailsHTML += '<tr><th>Người duyệt:</th><td>' + (cells[8].textContent.trim() || 'Chưa xử lý') + '</td></tr>';
    detailsHTML += '</table>';
    
    modalBody.innerHTML = detailsHTML;
    modal.style.display = 'block';
}

// Close modal
function closeModal() {
    document.getElementById('detailsModal').style.display = 'none';
}

// Cancel request
function cancelRequest(requestId) {
    if (confirm('Bạn có chắc chắn muốn HỦY đơn #' + requestId + '?\n\nHành động này không thể hoàn tác!')) {
        document.getElementById('cancelRequestId').value = requestId;
        document.getElementById('cancelForm').submit();
    }
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('detailsModal');
    if (event.target == modal) {
        closeModal();
    }
}
</script>

<jsp:include page="/includes/footer.jsp" />