<%@ page isErrorPage="true"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Station Not Found | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet" href="error_404_page.css?v=2025">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="error_404_page.js?v=2025"></script>
</head>
<body>

	<div class="ambient-light"></div>

	<div class="app-container">

		<nav class="navbar glass">
			<div class="nav-brand">
				<div class="logo-box">
					<i class="ri-train-fill"></i>
				</div>
				<span class="brand-text">Sam Railways</span>
			</div>

			<div class="nav-menu">
				<a href="RailwayApplication.jsp"><i class="ri-home-5-line"></i>
					Home</a> <a href="#"><i class="ri-customer-service-2-line"></i>
					Support</a>
			</div>

			<div class="nav-profile">
				<button id="themeToggle" class="icon-btn">
					<i class="ri-moon-line"></i>
				</button>

				<%
				String userName = (String) session.getAttribute("user_name");
				if (userName != null) {
				%>
				<div class="user-dropdown">
					<button class="user-btn">
						<span class="u-avatar"><%=userName.charAt(0)%></span> <span
							class="u-name">My Account</span> <i class="ri-arrow-down-s-line"></i>
					</button>
					<div class="dropdown-content glass">
						<a href="profile.jsp"><i class="ri-user-line"></i> Profile</a> <a
							href="logout" class="danger"><i class="ri-logout-box-line"></i>
							Logout</a>
					</div>
				</div>
				<%
				} else {
				%>
				<a href="login.jsp" class="btn-login">Log In</a>
				<%
				}
				%>
			</div>
		</nav>

		<main class="main-content">

			<div class="error-card glass">
				<div class="illustration">
					<div class="error-code">404</div>
					<div class="train-icon-big">
						<i class="ri-train-wifi-line"></i>
					</div>
				</div>

				<h1 class="error-title">Station Not Found</h1>
				<p class="error-desc">Oops! The train seems to have gone off the
					tracks. The page you are looking for doesn't exist or has been
					moved.</p>

				<div class="action-buttons">
					<button onclick="history.back()" class="btn secondary">
						<i class="ri-arrow-left-line"></i> Go Back
					</button>
					<a href="RailwayApplication.jsp" class="btn primary"> <i
						class="ri-home-4-line"></i> Back to Home
					</a>
				</div>
			</div>

		</main>
	</div>

</body>
</html>