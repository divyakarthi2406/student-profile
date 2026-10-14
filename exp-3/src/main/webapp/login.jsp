<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Profile Portal - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="login-body">

    <div class="login-card">
        <div class="login-icon">🎓</div>
        <h2>Student Profile Portal</h2>
        <p class="subtitle">Web Technologies College Portal</p>

        <!-- Display Error Message if any -->
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-danger">
                <span>⚠️</span>
                <span><%= request.getAttribute("errorMessage") %></span>
            </div>
        <% } %>

        <!-- Display Logout Success Message -->
        <% if ("true".equals(request.getParameter("logout"))) { %>
            <div class="alert alert-success">
                <span>✅</span>
                <span>You have been successfully logged out.</span>
            </div>
        <% } %>

        <!-- Login Form -->
        <form action="${pageContext.request.contextPath}/login" method="POST" autocomplete="off">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" 
                       id="username" 
                       name="username" 
                       class="form-control" 
                       placeholder="Enter username" 
                       value="<%= request.getAttribute("enteredUsername") != null ? request.getAttribute("enteredUsername") : "" %>"
                       required 
                       autofocus>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" 
                       id="password" 
                       name="password" 
                       class="form-control" 
                       placeholder="Enter password" 
                       required>
            </div>

            <button type="submit" class="btn-primary">Sign In</button>
        </form>

        <!-- Demo Credentials Helper -->
        <div class="login-hints">
            <p><strong>Default Demo Credentials:</strong></p>
            <div>Username: <code>admin</code> | Password: <code>admin123</code></div>
            <div>Username: <code>faculty</code> | Password: <code>faculty123</code></div>
        </div>
    </div>

</body>
</html>
