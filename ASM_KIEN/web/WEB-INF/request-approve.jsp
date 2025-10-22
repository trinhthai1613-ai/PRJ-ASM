<%-- ===============================================
FILE: web/request-approve.jsp
Trang duyệt/từ chối đơn nghỉ phép
=============================================== --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.leavemgmt.model.User" %>
<%@ page import="com.leavemgmt.model.LeaveRequest" %>
<%@ page import="com.leavemgmt.util.SessionHelper" %>
<%
    request.setAttribute("pageTitle", "Xét duyệt đơn nghỉ phép");
    User currentUser = SessionHelper.getCurrentUser(request);
    
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    String contextPath = request.getContextPath();
    String errorMessage = SessionHelper.getError(request);
    
    LeaveRequest leaveRequest = (LeaveRequest) request.getAttribute("leaveRequest");
    
    if (leaveRequest == null) {
        response.sendRedirect(contextPath + "/request/list");
        return;
    }
%>

<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/nav.jsp" />

<main class="main-content">
    <div class="container">
        
        <div class="page-header">
            <h2>✅ Xét duyệt đơn xin nghỉ phép #<%= leaveRequest.getRequestId() %></h2>
            <a href="<%= contextPath %>/request/list" class="btn btn-secondary">
                ← Quay lại danh sách
            </a>
        </div>
        
        <% if (errorMessage != null) { %>
            <div class="alert alert-error">
                <strong>⚠️ Lỗi:</strong> <%= errorMessage %>
            </div>
        <% } %>
        
        <!-- Request Details Card -->
        <div class="approval-container">
            
            <div class="request-details-card">
                <h3>📋 Thông tin đơn xin nghỉ phép</h3>
                
                <table class="details-table">
                    <tr>
                        <th>Mã đơn:</th>
                        <td><strong>#<%= leaveRequest.getRequestId() %></strong></td>
                    </tr>
                    <tr>
                        <th>Người tạo:</th>
                        <td>
                            <strong><%= leaveRequest.getCreatedByName() %></strong>
                            <br>Mã NV: <%= leaveRequest.getEmployeeCode() %>
                        </td>
                    </tr>
                    <tr>
                        <th>Loại nghỉ phép:</th>
                        <td><span class="badge badge-info"><%= leaveRequest.getLeaveTypeName() %></span></td>
                    </tr>
                    <tr>
                        <th>Thời gian nghỉ:</th>
                        <td>
                            <strong><%= leaveRequest.getFromDate() %></strong> 
                            → 
                            <strong><%= leaveRequest.getToDate() %></strong>
                            <br>
                            <span class="highlight">
                                Tổng: <%= leaveRequest.getTotalDays() %> ngày
                            </span>
                        </td>
                    </tr>
                    <tr>
                        <th>Lý do nghỉ:</th>
                        <td>
                            <div class="reason-box">
                                <%= leaveRequest.getReason() %>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <th>Ngày tạo đơn:</th>
                        <td><%= leaveRequest.getCreatedAt() %></td>
                    </tr>
                    <tr>
                        <th>Trạng thái:</th>
                        <td>
                            <span class="status-badge status-<%= leaveRequest.getStatusCode().toLowerCase() %>">
                                <%= leaveRequest.getStatusName() %>
                            </span>
                        </td>
                    </tr>
                </table>
            </div>
            
            <!-- Approval Form -->
            <div class="approval-form-card">
                <h3>📝 Quyết định xét duyệt</h3>
                
                <form action="<%= contextPath %>/request/review" method="post" id="approvalForm">
                    <input type="hidden" name="requestId" value="<%= leaveRequest.getRequestId() %>">
                    
                    <div class="form-group">
                        <label>👤 Người duyệt:</label>
                        <input type="text" 
                               value="<%= currentUser.getFullName() %> (<%= currentUser.getEmployeeCode() %>)" 
                               readonly 
                               class="readonly-input">
                    </div>
                    
                    <div class="form-group">
                        <label for="processNote">
                            💬 Ghi chú (không bắt buộc):
                        </label>
                        <textarea id="processNote" 
                                  name="processNote" 
                                  rows="4" 
                                  placeholder="Nhập ghi chú về quyết định duyệt/từ chối..."></textarea>
                        <small class="form-text">Ví dụ: "Đã xác nhận lịch làm việc, đồng ý cho nghỉ"</small>
                    </div>
                    
                    <div class="approval-actions">
                        <button type="submit" 
                                name="action" 
                                value="approve" 
                                class="btn btn-success btn-large"
                                onclick="return confirmApprove()">
                            ✅ DUYỆT ĐƠN
                        </button>
                        
                        <button type="submit" 
                                name="action" 
                                value="reject" 
                                class="btn btn-danger btn-large"
                                onclick="return confirmReject()">
                            ❌ TỪ CHỐI
                        </button>
                        
                        <a href="<%= contextPath %>/request/list" 
                           class="btn btn-secondary btn-large">
                            🔙 Quay lại
                        </a>
                    </div>
                </form>
                
                <!-- Warning Box -->
                <div class="warning-box">
                    <strong>⚠️ Lưu ý:</strong>
                    <ul>
                        <li>Quyết định duyệt/từ chối <strong>không thể hoàn tác</strong></li>
                        <li>Nhân viên sẽ nhận được thông báo về quyết định</li>
                        <li>Nếu duyệt, số ngày phép của nhân viên sẽ được trừ tự động</li>
                    </ul>
                </div>
            </div>
            
        </div>
        
    </div>
</main>

<script>
function confirmApprove() {
    const totalDays = <%= leaveRequest.getTotalDays() %>;
    const employeeName = '<%= leaveRequest.getCreatedByName() %>';
    const fromDate = '<%= leaveRequest.getFromDate() %>';
    const toDate = '<%= leaveRequest.getToDate() %>';
    
    const message = 'XÁC NHẬN DUYỆT ĐƠN?\n\n' +
                   '👤 Nhân viên: ' + employeeName + '\n' +
                   '📅 Thời gian: ' + fromDate + ' → ' + toDate + '\n' +
                   '📊 Số ngày: ' + totalDays + ' ngày\n\n' +
                   'Sau khi duyệt, đơn này sẽ được chấp nhận và số ngày phép sẽ được trừ.\n\n' +
                   'Bạn có chắc chắn muốn DUYỆT đơn này?';
    
    return confirm(message);
}

function confirmReject() {
    const employeeName = '<%= leaveRequest.getCreatedByName() %>';
    const note = document.getElementById('processNote').value.trim();
    
    if (note === '') {
        alert('⚠️ Vui lòng nhập lý do TỪ CHỐI trong phần ghi chú!');
        document.getElementById('processNote').focus();
        return false;
    }
    
    const message = 'XÁC NHẬN TỪ CHỐI ĐƠN?\n\n' +
                   '👤 Nhân viên: ' + employeeName + '\n' +
                   '💬 Lý do từ chối: ' + note + '\n\n' +
                   'Nhân viên sẽ nhận được thông báo về việc từ chối.\n\n' +
                   'Bạn có chắc chắn muốn TỪ CHỐI đơn này?';
    
    return confirm(message);
}

// Validate before submit
document.getElementById('approvalForm').addEventListener('submit', function(e) {
    const action = e.submitter.value;
    
    // If rejecting, must have note
    if (action === 'reject') {
        const note = document.getElementById('processNote').value.trim();
        if (note === '') {
            e.preventDefault();
            alert('⚠️ Vui lòng nhập lý do TỪ CHỐI!');
            document.getElementById('processNote').focus();
            return false;
        }
    }
});
</script>

<jsp:include page="/includes/footer.jsp" />