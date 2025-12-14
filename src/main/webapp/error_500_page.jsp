<%@ page isErrorPage="true"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.io.*"%>
<%
// Session Logic for Header
String userName = (String) session.getAttribute("user_name");
boolean isLoggedIn = (userName != null);
String userInitial = isLoggedIn ? String.valueOf(userName.charAt(0)).toUpperCase() : "U";

// Get Error Details
String errorMessage = (exception != null) ? exception.getMessage() : "Unknown Server Error";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Server Error | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet" href="error_500_page.css?v=2025">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="error_500_page.js?v=2025"></script>
</head>
<body>

	<div class="ambient-light-red"></div>

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
				if (isLoggedIn) {
				%>
				<div class="user-dropdown">
					<button class="user-btn">
						<span class="u-avatar"><%=userInitial%></span> <span
							class="u-name">My Account</span> <i class="ri-arrow-down-s-line"></i>
					</button>
					<div class="dropdown-content glass">
						<div class="dd-header">
							<strong><%=userName%></strong> <small>Logged In <i
								class="ri-checkbox-circle-fill success-icon"></i></small>
						</div>
						<div class="divider"></div>
						<a href="profile.jsp"><i class="ri-user-line"></i> My Profile</a>
						<a href="transactions.jsp"><i class="ri-exchange-dollar-line"></i>
							My Transactions</a> <a href="MyBookings"><i
							class="ri-history-line"></i> Booked Ticket History</a> <a
							href="refunds.jsp"><i class="ri-refund-2-line"></i> Ticket
							Refund History</a>
						<div class="divider"></div>
						<a href="logout" class="danger"><i class="ri-logout-box-line"></i>
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
					<div class="error-code">500</div>
					<div class="icon-pulse">
						<i class="ri-alarm-warning-fill"></i>
					</div>
				</div>

				<h1 class="error-title">Signal Failure</h1>
				<p class="error-desc">
					Something went wrong on our end. The train engine has encountered a
					critical error. <br>Please try refreshing the page or contact
					support.
				</p>

				<div class="action-buttons">
					<button onclick="location.reload()" class="btn secondary">
						<i class="ri-refresh-line"></i> Retry
					</button>
					<a href="RailwayApplication.jsp" class="btn primary"> <i
						class="ri-home-4-line"></i> Back to Home
					</a>
				</div>

				<div class="tech-details">
					<button class="btn-text" onclick="toggleDetails()">
						View Technical Details <i class="ri-arrow-down-s-line"
							id="toggleIcon"></i>
					</button>
					<div id="stackTrace" class="stack-box hidden">
						<p class="error-msg">
							<strong>Message:</strong>
							<%=errorMessage%></p>
						<pre>
<%
if (exception != null) {
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	exception.printStackTrace(pw);
	out.print(sw.toString());
}
%>
                        </pre>
					</div>
				</div>

			</div>

		</main>
	</div>

</body>
</html>