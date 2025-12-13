<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login | Sam Railways</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <link href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css" rel="stylesheet"/>
    <link rel="stylesheet" href="login.css?v=2">
    
    <script>
        const savedTheme = localStorage.getItem('sam_theme') || 'light';
        if (savedTheme === 'dark') document.documentElement.setAttribute('data-theme', 'dark');
    </script>
    <script defer src="login.js?v=2"></script>
</head>
<body>

    <div id="pageLoader" class="loader-overlay">
        <div class="loader-content">
            <div class="train-icon"><i class="ri-train-line"></i></div>
            <div class="loader-bar"></div>
            <p>Connecting to Rails...</p>
        </div>
    </div>

    <div class="ambient-light"></div>

    <div class="app-container">
        
        <nav class="navbar glass">
            <div class="nav-brand">
                <div class="logo-box"><i class="ri-train-fill"></i></div>
                <span class="brand-text">Sam Railways</span>
            </div>
            <div class="nav-profile">
                <button id="themeToggle" class="icon-btn"><i class="ri-moon-line"></i></button>
            </div>
        </nav>

        <main class="main-content">
            
            <div class="login-card glass animate-up">
                
                <div class="card-header">
                    <h2>Welcome Back</h2>
                    <p>Enter your credentials to continue</p>
                </div>

                <% 
                String message = (String) request.getAttribute("message");
                String isRegistered = request.getParameter("registered");
                String error = request.getParameter("error");

                if (message != null) { %>
                    <div class="alert error"><i class="ri-error-warning-line"></i> <%=message%></div>
                <% } 
                if ("true".equals(isRegistered)) { %>
                    <div class="alert success"><i class="ri-checkbox-circle-line"></i> Registration successful! Please log in.</div>
                <% } 
                if ("mismatch".equals(error)) { %>
                    <div class="alert error"><i class="ri-close-circle-line"></i> Invalid Username or Password.</div>
                <% } %>

                <form action="LoginServlet" method="post" class="login-form">
                    
                    <div class="input-group">
                        <div class="field-box">
                            <i class="ri-user-line icon"></i>
                            <input type="text" name="username" id="username" placeholder=" " required>
                            <label for="username">Username</label>
                        </div>
                    </div>

                    <div class="input-group">
                        <div class="field-box">
                            <i class="ri-lock-line icon"></i>
                            <input type="password" name="password" id="password" placeholder=" " required>
                            <label for="password">Password</label>
                            <button type="button" class="eye-btn" onclick="togglePassword()">
                                <i class="ri-eye-off-line" id="eyeIcon"></i>
                            </button>
                        </div>
                    </div>

                    <div class="forgot-links">
                        <a href="forgot_username.jsp">Forgot Username?</a>
                        <a href="forgot_password.jsp">Forgot Password?</a>
                    </div>

                    <button type="submit" class="btn-login">
                        Log In <i class="ri-arrow-right-line"></i>
                    </button>

                    <div class="register-link">
                        Don't have an account? <a href="register.jsp">Create Account</a>
                    </div>
                </form>
            </div>

        </main>
    </div>

</body>
</html>