<%-- ===============================================
FILE: web/error.jsp
Trang hiển thị lỗi (404, 403, 500...)
=============================================== --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%
    String contextPath = request.getContextPath();
    Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    String errorMessage = (String) request.getAttribute("jakarta.servlet.error.message");
    Exception exception = (Exception) request.getAttribute("jakarta.servlet.error.exception");
    
    if (statusCode == null) {
        statusCode = 500;
    }
    
    String errorTitle = "";
    String errorDescription = "";
    String errorIcon = "";
    
    switch (statusCode) {
        case 404:
            errorTitle = "404 - Không tìm thấy trang";
            errorDescription = "Trang bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.";
            errorIcon = "🔍";
            break;
        case 403:
            errorTitle = "403 - Không có quyền truy cập";
            errorDescription = "Bạn không có quyền truy cập vào trang này.";
            errorIcon = "🚫";
            break;
        case 500:
            errorTitle = "500 - Lỗi máy chủ";
            errorDescription = "Đã xảy ra lỗi trên máy chủ. Vui lòng thử lại sau.";
            errorIcon = "⚠️";
            break;
        default:
            errorTitle = "Lỗi " + statusCode;
            errorDescription = "Đã xảy ra lỗi. Vui lòng thử lại.";
            errorIcon = "❌";
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= errorTitle %> - Leave Management</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
    <style>
        .error-page {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        .error-container {
            text-align: center;
            background: white;
            padding: 60px 40px;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            max-width: 600px;
            margin: 20px;
        }
        
        .error-icon {
            font-size: 120px;
            margin-bottom: 20px;
            animation: bounce 2s infinite;
        }
        
        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-20px); }
        }
        
        .error-code {
            font-size: 72px;
            font-weight: bold;
            color: #667eea;
            margin: 0;
        }
        
        .error-title {
            font-size: 28px;
            color: #333;
            margin: 20px 0;
        }
        
        .error-description {
            font-size: 16px;
            color: #666;
            margin: 20px 0;
            line-height: 1.6;
        }
        
        .error-actions {
            margin-top: 40px;
        }
        
        .error-actions .btn {
            display: inline-block;
            padding: 15px 30px;
            margin: 10px;
            text-decoration: none;
            border-radius: 8px;
            font-size: 16px;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #e0e0e0;
            color: #333;
        }
        
        .btn-secondary:hover {
            background: #d0d0d0;
            transform: translateY(-2px);
        }
        
        .error-details {
            margin-top: 30px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 10px;
            text-align: left;
            font-family: monospace;
            font-size: 12px;
            color: #666;
            max-height: 200px;
            overflow-y: auto;
        }
    </style>
</head>
<body class="error-page">
    
    <div class="error-container">
        <div class="error-icon"><%= errorIcon %></div>
        <h1 class="error-code"><%= statusCode %></h1>
        <h2 class="error-title"><%= errorTitle %></h2>
        <p class="error-description"><%= errorDescription %></p>
        
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <p class="error-description">
                <strong>Chi tiết:</strong> <%= errorMessage %>
            </p>
        <% } %>
        
        <div class="error-actions">
            <a href="<%= contextPath %>/home" class="btn btn-primary">
                🏠 Về trang chủ
            </a>
            <a href="javascript:history.back()" class="btn btn-secondary">
                ← Quay lại
            </a>
        </div>
        
        <% if (exception != null && application.getInitParameter("displayErrors") != null) { %>
            <div class="error-details">
                <strong>Stack Trace (Development Mode):</strong><br>
                <%= exception.toString() %><br>
                <% 
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                    exception.printStackTrace(pw);
                    String stackTrace = sw.toString().replaceAll("\n", "<br>");
                %>
                <%= stackTrace %>
            </div>
        <% } %>
        
        <p style="margin-top: 30px; color: #999; font-size: 14px;">
            Nếu lỗi này tiếp tục xảy ra, vui lòng liên hệ quản trị viên.
        </p>
    </div>
    
</body>
</html>