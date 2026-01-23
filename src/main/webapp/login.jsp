<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Login | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet" href="login.css">
<link rel="icon" type="image/png" href="train_logo_all.png">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'dark';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="login.js"></script>
</head>
<body>

	<!-- Loading Screen -->
	<div id="pageLoader" class="loader-overlay">
		<div class="loader-content">
			<div class="loader-glow"></div>
			<div class="train-icon-wrapper">
				<i class="ri-train-line train-icon"></i>
			</div>
			<div class="loader-track">
				<div class="loader-train"></div>
			</div>
			<p class="loader-text">Connecting to Rails...</p>
		</div>
	</div>

	<!-- Animated Background -->
	<div class="bg-gradient">
		<div class="orb orb-1"></div>
		<div class="orb orb-2"></div>
		<div class="orb orb-3"></div>
	</div>

	<div class="app-container">

		<!-- Navbar -->
		<nav class="navbar">
			<div class="nav-content">
				<div class="nav-brand">
					<div class="logo-wrapper">
						<i class="ri-train-fill"></i>
					</div>
					<span class="brand-text">Sam Railways</span>
				</div>
				<button id="themeToggle" class="theme-btn">
					<i class="ri-moon-line"></i>
				</button>
			</div>
		</nav>

		<!-- Main Content -->
		<main class="main-content">

			<div class="login-card">

				<!-- Card Header -->
				<div class="card-header">
					<div class="header-icon">
						<i class="ri-train-fill"></i>
					</div>
					<h2>Welcome Back</h2>
					<p>Enter your credentials to continue</p>
				</div>

				<!-- Alerts -->
				<%
				String message = (String) request.getAttribute("message");
				String isRegistered = request.getParameter("registered");
				String error = request.getParameter("error");

				if (message != null) {
				%>
				<div class="alert alert-error">
					<i class="ri-error-warning-line"></i> <span><%=message%></span>
				</div>
				<%
				}
				if ("true".equals(isRegistered)) {
				%>
				<div class="alert alert-success">
					<i class="ri-checkbox-circle-line"></i> <span>Registration
						successful! Please log in.</span>
				</div>
				<%
				}
				if ("mismatch".equals(error)) {
				%>
				<div class="alert alert-error">
					<i class="ri-close-circle-line"></i> <span>Invalid Username
						or Password.</span>
				</div>
				<%
				}
				%>

				<!-- Login Form -->
				<div class="login-form">

					<!-- Username Field -->
					<div class="input-group">
						<div class="input-glow"></div>
						<div class="input-wrapper">
							<i class="ri-user-line input-icon"></i> <input type="text"
								name="username" id="username" placeholder="Username"
								autocomplete="username" required>
						</div>
					</div>

					<!-- Password Field -->
					<div class="input-group">
						<div class="input-glow"></div>
						<div class="input-wrapper">
							<i class="ri-lock-line input-icon"></i> <input type="password"
								name="password" id="password" placeholder="Password"
								autocomplete="current-password" required>
							<button type="button" class="eye-btn" id="togglePassword">
								<i class="ri-eye-off-line"></i>
							</button>
						</div>
					</div>

					<!-- Forgot Links -->
					<div class="forgot-links">
						<a href="forgotUsername.jsp">Forgot Username?</a> <a
							href="forgotPassword.jsp">Forgot Password?</a>
					</div>

					<!-- Submit Button -->
					<button type="button" class="btn-login" id="loginBtn">
						<span class="btn-gradient"></span> <span
							class="btn-gradient-hover"></span> <span class="btn-content">
							<span>Log In</span> <i class="ri-arrow-right-line"></i>
						</span>
					</button>

					<!-- Register Link -->
					<div class="register-link">
						Don't have an account? <a href="register.jsp">Create Account</a>
					</div>
				</div>

			</div>

		</main>
	</div>

	<!-- Hidden form for actual submission -->
	<form id="hiddenForm" action="LoginServlet" method="post"
		style="display: none;">
		<input type="text" name="username" id="hiddenUsername"> <input
			type="password" name="password" id="hiddenPassword">
	</form>

</body>
</html>