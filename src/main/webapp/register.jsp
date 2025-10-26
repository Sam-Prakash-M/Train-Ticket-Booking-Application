<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Railway Booking - Register</title>
  <link rel="stylesheet" href="register.css" />
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet" />
</head>
<body class="dark-mode">
  <!-- Loader -->
  <div id="loader">
    <div class="train-loader"></div>
    <p>Setting Tracks...</p>
  </div>

  <!-- Theme Toggle -->
  <div class="theme-toggle" onclick="toggleTheme()">
    <div class="icon">🌙</div>
  </div>

  <!-- Background -->
  <div class="background">
    <div class="train-track"></div>
    <div class="train"></div>
  </div>

  <!-- Register Card -->
  <div class="register-card">
    <h2>🚆 Create Account</h2>
    <p class="subtitle">Join the Railway Booking Portal</p>

    <!-- JSP message display section -->
    <%
        String message = (String) request.getAttribute("message");
        if (message != null) {
    %>
        <p style="color: red; text-align: center;"><%= message %></p>
    <%
        }
        if ("success".equals(request.getParameter("status"))) {
    %>
        <p style="color: green; text-align: center;">Registration successful! Please log in.</p>
    <%
        } else if ("exists".equals(request.getParameter("error"))) {
    %>
        <p style="color: red; text-align: center;">If Your are the User.! Please log in instead. Or Use Different UserName</p>
    <%
        } else if ("failed".equals(request.getParameter("error"))) {
    %>
        <p style="color: red; text-align: center;">Something went wrong. Please try again.</p>
    <%
        }
    %>

    <form action="RegisterServlet" method="post" class="form">
      <div class="input-container">
        <input type="text" name="fullname" id="fullname" required />
        <label for="fullname">Full Name</label>
      </div>

      <div class="input-container">
        <input type="email" name="email" id="email" required />
        <label for="email">Email</label>
      </div>

      <div class="input-container">
        <input type="text" name="username" id="username" required />
        <label for="username">Username</label>
      </div>

      <div class="input-container">
        <input type="password" name="password" id="password" required />
        <label for="password">Password</label>
      </div>

      <button type="submit" class="register-btn">Register</button>

      <p class="login-text">
        Already have an account? <a href="login.jsp">Login Here</a>
      </p>
    </form>
  </div>

  <script src="register.js"></script>
</body>
</html>
