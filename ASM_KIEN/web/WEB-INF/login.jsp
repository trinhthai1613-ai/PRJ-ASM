<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.leavemgmt.util.SessionHelper" %>
<%
    // Nếu đã đăng nhập rồi thì redirect về home
    if (SessionHelper.isLoggedIn(request)) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }
    
    String contextPath = request.getContextPath();
    String errorMessage = SessionHelper.getError(request);
    String successMessage = SessionHelper.getSuccess(request);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - Leave Management System</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
</head>
<body class="login-page">
    
    <div class="login-container">
        <div class="login-box">
            <div class="login-header">
                <h1>📋 Leave Management</h1>
                <p>Hệ thống quản lý đơn nghỉ phép</p>
            </div>
            
            <% if (errorMessage != null) { %>
                <div class="alert alert-error">
                    <strong>⚠️ Lỗi:</strong> <%= errorMessage %>
                </div>
            <% } %>
            
            <% if (successMessage != null) { %>
                <div class="alert alert-success">
                    <strong>✓ Thành công:</strong> <%= successMessage %>
                </div>
            <% } %>
            
            <form action="<%= contextPath %>/login" method="post" class="login-form">
                <div class="form-group">
                    <label for="username">
                        <span class="icon">👤</span> Username
                    </label>
                    <input type="text" 
                           id="username" 
                           name="username" 
                           placeholder="Nhập username" 
                           required 
                           autofocus>
                </div>
                
                <div class="form-group">
                    <label for="password">
                        <span class="icon">🔒</span> Password
                    </label>
                    <input type="password" 
                           id="password" 
                           name="password" 
                           placeholder="Nhập password" 
                           required>
                </div>
                
                <button type="submit" class="btn btn-primary btn-block">
                    🚪 Đăng nhập
                </button>
            </form>
            
            <div class="login-footer">
                <h3>Tài khoản demo:</h3>
                <table class="demo-accounts">
                    <tr>
                        <th>Username</th>
                        <th>Password</th>
                        <th>Role</th>
                    </tr>
                    <tr>
                        <td><code>mra</code></td>
                        <td><code>Password123!</code></td>
                        <td>Trưởng phòng IT</td>
                    </tr>
                    <tr>
                        <td><code>mrb</code></td>
                        <td><code>Password123!</code></td>
                        <td>Trưởng nhóm IT</td>
                    </tr>
                    <tr>
                        <td><code>mrd</code></td>
                        <td><code>Password123!</code></td>
                        <td>Nhân viên IT</td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
    
    <script>
        // Auto-fill demo account on click
        document.querySelectorAll('.demo-accounts tr').forEach((row, index) => {
            if (index > 0) { // Skip header
                row.style.cursor = 'pointer';
                row.addEventListener('click', function() {
                    const username = this.cells[0].textContent.trim();
                    const password = this.cells[1].textContent.trim();
                    document.getElementById('username').value = username;
                    document.getElementById('password').value = password;
                });
            }
        });
    </script>
</body>
</html>