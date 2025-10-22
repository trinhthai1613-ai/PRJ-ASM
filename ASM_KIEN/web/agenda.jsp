<%-- ===============================================
FILE: web/agenda.jsp
Xem lịch nghỉ phép của cả phòng ban (Agenda)
=============================================== --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.leavemgmt.model.User" %>
<%@ page import="com.leavemgmt.model.AgendaItem" %>
<%@ page import="com.leavemgmt.util.SessionHelper" %>
<%@ page import="java.util.*" %>
<%@ page import="java.time.LocalDate" %>
<%
    request.setAttribute("pageTitle", "Agenda phòng ban");
    User currentUser = SessionHelper.getCurrentUser(request);
    
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    String contextPath = request.getContextPath();
    
    @SuppressWarnings("unchecked")
    Map<Integer, Map<LocalDate, AgendaItem>> agendaMap = 
        (Map<Integer, Map<LocalDate, AgendaItem>>) request.getAttribute("agendaMap");
    
    @SuppressWarnings("unchecked")
    Map<Integer, String> userNames = (Map<Integer, String>) request.getAttribute("userNames");
    
    @SuppressWarnings("unchecked")
    Set<LocalDate> dates = (Set<LocalDate>) request.getAttribute("dates");
    
    String fromDate = (String) request.getAttribute("fromDate");
    String toDate = (String) request.getAttribute("toDate");
%>

<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/nav.jsp" />

<main class="main-content">
    <div class="container">
        
        <div class="page-header">
            <h2>📅 Agenda - Tình hình lao động phòng <%= currentUser.getDivisionName() %></h2>
        </div>
        
        <!-- Date Range Filter -->
        <div class="filter-section">
            <form action="<%= contextPath %>/agenda/view" method="get" class="filter-form">
                <div class="form-row">
                    <div class="form-group">
                        <label for="fromDate">Từ ngày:</label>
                        <input type="date" 
                               id="fromDate" 
                               name="fromDate" 
                               value="<%= fromDate != null ? fromDate : "" %>"
                               required>
                    </div>
                    
                    <div class="form-group">
                        <label for="toDate">Đến ngày:</label>
                        <input type="date" 
                               id="toDate" 
                               name="toDate" 
                               value="<%= toDate != null ? toDate : "" %>"
                               required>
                    </div>
                    
                    <div class="form-group">
                        <label>&nbsp;</label>
                        <button type="submit" class="btn btn-primary">
                            🔍 Xem
                        </button>
                    </div>
                    
                    <div class="form-group">
                        <label>&nbsp;</label>
                        <button type="button" class="btn btn-secondary" onclick="setThisWeek()">
                            📆 Tuần này
                        </button>
                    </div>
                    
                    <div class="form-group">
                        <label>&nbsp;</label>
                        <button type="button" class="btn btn-secondary" onclick="setThisMonth()">
                            📅 Tháng này
                        </button>
                    </div>
                </div>
            </form>
        </div>
        
        <!-- Legend -->
        <div class="legend-box">
            <h4>📖 Chú thích:</h4>
            <span class="legend-item working">🟢 Đi làm</span>
            <span class="legend-item leave">🔴 Nghỉ phép</span>
            <span class="legend-item weekend">⚪ Cuối tuần</span>
        </div>
        
        <!-- Agenda Table -->
        <div class="agenda-container">
            <% if (agendaMap != null && !agendaMap.isEmpty() && dates != null && !dates.isEmpty()) { %>
                <div class="table-wrapper">
                    <table class="agenda-table">
                        <thead>
                            <tr>
                                <th class="sticky-col">Nhân viên</th>
                                <% for (LocalDate date : dates) { 
                                    java.time.DayOfWeek dayOfWeek = date.getDayOfWeek();
                                    String dayName = "";
                                    switch(dayOfWeek) {
                                        case MONDAY: dayName = "T2"; break;
                                        case TUESDAY: dayName = "T3"; break;
                                        case WEDNESDAY: dayName = "T4"; break;
                                        case THURSDAY: dayName = "T5"; break;
                                        case FRIDAY: dayName = "T6"; break;
                                        case SATURDAY: dayName = "T7"; break;
                                        case SUNDAY: dayName = "CN"; break;
                                    }
                                    boolean isWeekend = dayOfWeek.getValue() == 6 || dayOfWeek.getValue() == 7;
                                %>
                                    <th class="<%= isWeekend ? "weekend-header" : "" %>">
                                        <%= dayName %>
                                        <br>
                                        <small><%= date %></small>
                                    </th>
                                <% } %>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            for (Map.Entry<Integer, String> entry : userNames.entrySet()) {
                                int userId = entry.getKey();
                                String userName = entry.getValue();
                                Map<LocalDate, AgendaItem> userSchedule = agendaMap.get(userId);
                            %>
                                <tr>
                                    <td class="sticky-col employee-name">
                                        <strong><%= userName %></strong>
                                    </td>
                                    <% for (LocalDate date : dates) {
                                        AgendaItem item = userSchedule != null ? userSchedule.get(date) : null;
                                        String status = item != null ? item.getStatus() : "Đi làm";
                                        String cellClass = "";
                                        String cellTitle = "";
                                        String cellIcon = "";
                                        
                                        if (status.contains("Nghỉ phép")) {
                                            cellClass = "status-leave";
                                            cellIcon = "🔴";
                                            cellTitle = (item != null && item.getLeaveTypeName() != null) ? 
                                                       item.getLeaveTypeName() + ": " + (item.getReason() != null ? item.getReason() : "") : 
                                                       "Nghỉ phép";
                                        } else if (status.contains("Cuối tuần")) {
                                            cellClass = "status-weekend";
                                            cellIcon = "⚪";
                                            cellTitle = "Cuối tuần";
                                        } else {
                                            cellClass = "status-working";
                                            cellIcon = "🟢";
                                            cellTitle = "Đi làm";
                                        }
                                    %>
                                        <td class="<%= cellClass %>" title="<%= cellTitle %>">
                                            <span class="status-icon"><%= cellIcon %></span>
                                        </td>
                                    <% } %>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                
                <!-- Summary Statistics -->
                <div class="agenda-summary">
                    <h4>📊 Thống kê:</h4>
                    <%
                    int totalEmployees = userNames.size();
                    int totalDays = dates.size();
                    int leaveCount = 0;
                    
                    if (agendaMap != null) {
                        for (Map<LocalDate, AgendaItem> schedule : agendaMap.values()) {
                            for (AgendaItem item : schedule.values()) {
                                if (item.getStatus().contains("Nghỉ phép")) {
                                    leaveCount++;
                                }
                            }
                        }
                    }
                    %>
                    <p>👥 Tổng số nhân viên: <strong><%= totalEmployees %></strong></p>
                    <p>📅 Số ngày hiển thị: <strong><%= totalDays %></strong> ngày</p>
                    <p>🔴 Tổng lượt nghỉ phép: <strong><%= leaveCount %></strong> lượt</p>
                </div>
                
            <% } else { %>
                <div class="no-data-message">
                    <div class="no-data-icon">📭</div>
                    <h3>Không có dữ liệu</h3>
                    <p>Vui lòng chọn khoảng thời gian để xem agenda.</p>
                </div>
            <% } %>
        </div>
        
    </div>
</main>

<script>
// Set from/to date to this week
function setThisWeek() {
    const today = new Date();
    const dayOfWeek = today.getDay(); // 0 = Sunday, 1 = Monday, ...
    
    // Calculate Monday of this week
    const monday = new Date(today);
    monday.setDate(today.getDate() - (dayOfWeek === 0 ? 6 : dayOfWeek - 1));
    
    // Calculate Sunday of this week
    const sunday = new Date(monday);
    sunday.setDate(monday.getDate() + 6);
    
    document.getElementById('fromDate').value = formatDate(monday);
    document.getElementById('toDate').value = formatDate(sunday);
}

// Set from/to date to this month
function setThisMonth() {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);
    
    document.getElementById('fromDate').value = formatDate(firstDay);
    document.getElementById('toDate').value = formatDate(lastDay);
}

// Format date to YYYY-MM-DD
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
}

// Update toDate min when fromDate changes
document.getElementById('fromDate').addEventListener('change', function() {
    document.getElementById('toDate').min = this.value;
});
</script>

<jsp:include page="/includes/footer.jsp" />