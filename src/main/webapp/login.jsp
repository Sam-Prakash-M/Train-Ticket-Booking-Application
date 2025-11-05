<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Railway Booking Login</title>
  <link rel="stylesheet" href="login.css" />
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet" />
</head>
<body class="dark-mode">
  <!-- Loader -->
  <div id="loader">
    <div class="train-loader"></div>
    <p>Starting Engines...</p>
  </div>

  <!-- Dark / Light Toggle -->
  <div class="theme-toggle" onclick="toggleTheme()">
    <div class="icon">🌙</div>
  </div>

  <!-- Background -->
  <div class="background">
    <div class="train-track"></div>
    <div class="train"></div>
  </div>

  <!-- Login Card -->
  <div class="login-card">
    <h2>🚆 Railway Booking</h2>
    <p class="subtitle">Welcome back! Please log in</p>

    <!-- Dynamic message section -->
    <%
        String message = (String) request.getAttribute("message");
        if (message != null) {
    %>
        <p style="color: red; text-align: center;"><%= message %></p>
    <%
        }
        if ("true".equals(request.getParameter("registered"))) {
    %>
        <p style="color: green; text-align: center;">Registration successful! Please log in.</p>
    <%
        }
        if ("mismatch".equals(request.getParameter("error"))) {
    %>
        <p style="color: red; text-align: center;">Please Enter Valid Username and Password.</p>
    <%
        }
    %>

    <form action="LoginServlet" method="post" class="form">
      <div class="input-container">
        <input type="text" name="username" id="username" required />
        <label for="username">Username</label>
      </div>
      <div class="input-container">
        <input type="password" name="password" id="password" required />
        <label for="password">Password</label>
      </div>
      <button type="submit" class="login-btn">Login</button>
      <p class="register-text">Don’t have an account? <a href="register.jsp">Register Now</a></p>
    </form>
  </div>

  <script src="login.js"></script>
</body>
</html>
