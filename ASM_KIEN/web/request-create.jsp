<%-- ===============================================
FILE: web/request-create.jsp
Trang tạo đơn xin nghỉ phép
=============================================== --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.leavemgmt.model.User" %>
<%@ page import="com.leavemgmt.model.LeaveType" %>
<%@ page import="com.leavemgmt.util.SessionHelper" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%
    request.setAttribute("pageTitle", "Tạo đơn xin nghỉ phép");
    User currentUser = SessionHelper.getCurrentUser(request);
    
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    String contextPath = request.getContextPath();
    String errorMessage = SessionHelper.getError(request);
    
    @SuppressWarnings("unchecked")
    List<LeaveType> leaveTypes = (List<LeaveType>) request.getAttribute("leaveTypes");
    
    // Get today's date for min date
    String today = LocalDate.now().toString();
%>

<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/nav.jsp" />

<main class="main-content">
    <div class="container">
        
        <div class="page-header">
            <h2>➕ Tạo đơn xin nghỉ phép</h2>
            <a href="<%= contextPath %>/request/list" class="btn btn-secondary">
                ← Quay lại danh sách
            </a>
        </div>
        
        <% if (errorMessage != null) { %>
            <div class="alert alert-error">
                <strong>⚠️ Lỗi:</strong> <%= errorMessage %>
            </div>
        <% } %>
        
        <div class="form-container">
            <form action="<%= contextPath %>/request/create" method="post" id="createRequestForm">
                
                <!-- User Info (Read-only) -->
                <div class="form-section">
                    <h3>👤 Thông tin người tạo</h3>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Họ tên:</label>
                            <input type="text" value="<%= currentUser.getFullName() %>" readonly class="readonly-input">
                        </div>
                        <div class="form-group">
                            <label>Mã nhân viên:</label>
                            <input type="text" value="<%= currentUser.getEmployeeCode() %>" readonly class="readonly-input">
                        </div>
                        <div class="form-group">
                            <label>Phòng ban:</label>
                            <input type="text" value="<%= currentUser.getDivisionName() %>" readonly class="readonly-input">
                        </div>
                    </div>
                </div>
                
                <!-- Leave Request Details -->
                <div class="form-section">
                    <h3>📝 Thông tin đơn nghỉ phép</h3>
                    
                    <div class="form-group">
                        <label for="leaveTypeId">
                            <span class="required">*</span> Loại nghỉ phép:
                        </label>
                        <select id="leaveTypeId" name="leaveTypeId" required>
                            <option value="">-- Chọn loại nghỉ phép --</option>
                            <% if (leaveTypes != null) {
                                for (LeaveType type : leaveTypes) { %>
                                    <option value="<%= type.getLeaveTypeId() %>" 
                                            data-code="<%= type.getLeaveTypeCode() %>">
                                        <%= type.getLeaveTypeName() %> 
                                        (Tối đa: <%= type.getMaxDaysPerYear() %> ngày/năm)
                                    </option>
                            <%  }
                            } %>
                        </select>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="fromDate">
                                <span class="required">*</span> Từ ngày:
                            </label>
                            <input type="date" 
                                   id="fromDate" 
                                   name="fromDate" 
                                   min="<%= today %>"
                                   required>
                        </div>
                        
                        <div class="form-group">
                            <label for="toDate">
                                <span class="required">*</span> Đến ngày:
                            </label>
                            <input type="date" 
                                   id="toDate" 
                                   name="toDate" 
                                   min="<%= today %>"
                                   required>
                        </div>
                        
                        <div class="form-group">
                            <label>Tổng số ngày:</label>
                            <input type="text" 
                                   id="totalDays" 
                                   readonly 
                                   class="readonly-input"
                                   value="0 ngày">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="reason">
                            <span class="required">*</span> Lý do nghỉ phép:
                            <span id="reasonRequired" class="hint" style="display:none; color: red;">
                                (Bắt buộc khi chọn loại "Khác")
                            </span>
                        </label>
                        <textarea id="reason" 
                                  name="reason" 
                                  rows="5" 
                                  placeholder="Nhập lý do xin nghỉ phép..."
                                  required></textarea>
                        <small class="form-text">Tối thiểu 10 ký tự</small>
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary btn-large">
                        ✉️ Gửi đơn xin nghỉ phép
                    </button>
                    <a href="<%= contextPath %>/request/list" class="btn btn-secondary btn-large">
                        ❌ Hủy
                    </a>
                </div>
                
            </form>
        </div>
        
    </div>
</main>

<script>
// Calculate total days between two dates
function calculateDays() {
    const fromDate = document.getElementById('fromDate').value;
    const toDate = document.getElementById('toDate').value;
    
    if (fromDate && toDate) {
        const from = new Date(fromDate);
        const to = new Date(toDate);
        const diff = Math.ceil((to - from) / (1000 * 60 * 60 * 24)) + 1;
        
        if (diff > 0) {
            document.getElementById('totalDays').value = diff + ' ngày';
        } else {
            document.getElementById('totalDays').value = '0 ngày';
            alert('Ngày kết thúc phải sau ngày bắt đầu!');
        }
    }
}

// Auto-update toDate min when fromDate changes
document.getElementById('fromDate').addEventListener('change', function() {
    document.getElementById('toDate').min = this.value;
    calculateDays();
});

document.getElementById('toDate').addEventListener('change', calculateDays);

// Check if "Khác" (OTHER) is selected - require detailed reason
document.getElementById('leaveTypeId').addEventListener('change', function() {
    const selectedOption = this.options[this.selectedIndex];
    const leaveCode = selectedOption.getAttribute('data-code');
    const reasonHint = document.getElementById('reasonRequired');
    
    if (leaveCode === 'OTHER') {
        reasonHint.style.display = 'inline';
    } else {
        reasonHint.style.display = 'none';
    }
});

// Form validation before submit
document.getElementById('createRequestForm').addEventListener('submit', function(e) {
    const reason = document.getElementById('reason').value.trim();
    const fromDate = document.getElementById('fromDate').value;
    const toDate = document.getElementById('toDate').value;
    
    // Validate reason length
    if (reason.length < 10) {
        e.preventDefault();
        alert('Lý do nghỉ phép phải có ít nhất 10 ký tự!');
        return false;
    }
    
    // Validate dates
    if (new Date(toDate) < new Date(fromDate)) {
        e.preventDefault();
        alert('Ngày kết thúc phải sau ngày bắt đầu!');
        return false;
    }
    
    // Confirm before submit
    const totalDays = document.getElementById('totalDays').value;
    const leaveType = document.getElementById('leaveTypeId').options[document.getElementById('leaveTypeId').selectedIndex].text;
    
    const confirmMsg = 'Xác nhận gửi đơn xin nghỉ phép?\n\n' +
                      'Loại: ' + leaveType + '\n' +
                      'Từ: ' + fromDate + ' → ' + toDate + '\n' +
                      'Tổng: ' + totalDays;
    
    if (!confirm(confirmMsg)) {
        e.preventDefault();
        return false;
    }
});
</script>

<jsp:include page="/includes/footer.jsp" />