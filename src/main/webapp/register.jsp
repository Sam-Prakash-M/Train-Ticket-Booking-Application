<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Register | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/intl-tel-input@18.2.1/build/css/intlTelInput.css">

<link rel="stylesheet" href="register.css?v=2025_MOBILE">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="register.js?v=2025_MOBILE"></script>
<script
	src="https://cdn.jsdelivr.net/npm/intl-tel-input@18.2.1/build/js/intlTelInput.min.js"></script>
</head>
<body>

	<div id="pageLoader" class="loader-overlay">
		<div class="loader-content">
			<div class="train-icon">
				<i class="ri-train-line"></i>
			</div>
			<div class="loader-bar"></div>
			<p>Setting up your account...</p>
		</div>
	</div>
	<div class="ambient-light"></div>

	<div class="app-container">
		<nav class="navbar glass">
			<div class="nav-brand">
				<div class="logo-box">
					<i class="ri-train-fill"></i>
				</div>
				<span class="brand-text">Sam Railways</span>
			</div>
			<div class="nav-profile">
				<button id="themeToggle" class="icon-btn">
					<i class="ri-moon-line"></i>
				</button>
			</div>
		</nav>

		<main class="main-content">
			<div class="register-card glass animate-up">
				<div class="card-header">
					<h2>Create Account</h2>
					<p>Join millions of travelers today</p>
				</div>

				<%
				String message = (String) request.getAttribute("message");
				String status = request.getParameter("status");
				String error = request.getParameter("error");
				if (message != null) {
				%><div class="alert error">
					<i class="ri-error-warning-line"></i>
					<%=message%></div>
				<%
				}
				if ("success".equals(status)) {
				%><div
					class="alert success">
					<i class="ri-checkbox-circle-line"></i> Registration successful! <a
						href="login.jsp">Log in here</a>
				</div>
				<%
				}
				if ("exists".equals(error)) {
				%><div
					class="alert warning">
					<i class="ri-alert-line"></i> Username/Email already exists.
				</div>
				<%
				}
				if ("failed".equals(error)) {
				%><div class="alert error">
					<i class="ri-close-circle-line"></i> Something went wrong.
				</div>
				<%
				}
				%>

				<form action="RegisterServlet" method="post" class="register-form"
					id="regForm">

					<div class="input-group">
						<div class="field-box">
							<i class="ri-user-smile-line icon"></i> <input type="text"
								name="fullname" id="fullname" placeholder=" " required>
							<label for="fullname">Full Name</label>
						</div>
					</div>

					<div class="input-group">
						<div class="field-box">
							<i class="ri-mail-line icon"></i> <input type="email"
								name="email" id="email" placeholder=" " required> <label
								for="email">Email Address</label>
						</div>
					</div>

					<div class="input-group phone-group">
						<div class="field-box phone-box">
							<input type="tel" id="mobile" placeholder=" " required> <input
								type="hidden" name="contactno" id="fullMobileNumber">
							<span id="valid-msg" class="hide">✓ Valid</span> <span
								id="error-msg" class="hide"></span>
						</div>
					</div>

					<div class="input-group">
						<div class="field-box">
							<i class="ri-user-3-line icon"></i> <input type="text"
								name="username" id="username" placeholder=" " required>
							<label for="username">Username</label>
						</div>
					</div>

					<div class="input-group">
						<div class="field-box">
							<i class="ri-lock-line icon"></i> <input type="password"
								name="password" id="password" placeholder=" " required
								minlength="6"> <label for="password">Password</label>
							<button type="button" class="eye-btn" onclick="togglePassword()">
								<i class="ri-eye-off-line" id="eyeIcon"></i>
							</button>
						</div>
						<small class="hint">Must be at least 6 characters</small>
					</div>

					<button type="submit" class="btn-register">
						Create Account <i class="ri-arrow-right-line"></i>
					</button>

					<div class="login-link">
						Already have an account? <a href="login.jsp">Log In</a>
					</div>
				</form>
			</div>
		</main>
	</div>
</body>
</html>