<%@ page contentType="text/html; charset=UTF-8"%>
<%
// Session Logic
String userName = (String) session.getAttribute("user_name");
boolean isLoggedIn = (userName != null);
String userInitial = isLoggedIn ? String.valueOf(userName.charAt(0)).toUpperCase() : "U";

// Data from Servlet
String sessionId = (String) request.getAttribute("paymentSessionId");
String orderId = (String) request.getAttribute("orderId");
String amount = (String) request.getAttribute("amount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Processing Payment | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet" href="cashfreeCheckout.css?v=2025_MODERN_CF">
<link rel="icon" type="image/png" href="train_logo_all.png">

<script>
    const savedTheme = localStorage.getItem('sam_theme') || 'light';
    if (savedTheme === 'dark') document.documentElement.setAttribute('data-theme', 'dark');
</script>
</head>
<body>

	<div class="ambient-light-purple"></div>

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
					Home</a> <a href="#" class="active"><i
					class="ri-secure-payment-line"></i> Payment</a>
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
							class="u-name">My Account</span>
					</button>
				</div>
				<%
				}
				%>
			</div>
		</nav>

		<main class="main-content">
			<div class="process-card glass">
				<div class="logo-area">
					<img src="https://cashfree.com/assets/images/logo.svg"
						alt="Cashfree" class="cf-logo">
				</div>

				<h2 class="amount-text">
					<small>₹</small>
					<%=amount%></h2>
				<p class="desc-text">Securely pay via UPI, Credit/Debit Card, or
					NetBanking.</p>

				<div id="initialState">
					<button id="payBtn" class="pay-btn-glitch" onclick="startPayment()">
						Proceed to Pay <i class="ri-arrow-right-line"></i>
					</button>
					<button class="cancel-link" onclick="history.back()">Cancel
						Transaction</button>
				</div>

				<div id="processingState" class="hidden">
					<div class="loader-pulse"></div>
					<p class="status-text">Redirecting to Bank...</p>
				</div>

				<div class="secure-footer">
					<i class="ri-shield-check-fill"></i> Secured by Cashfree Payments
				</div>
			</div>
		</main>

	</div>

	<script src="https://sdk.cashfree.com/js/v3/cashfree.js"></script>
	<script src="cashfreeCheckout.js?v=2025_MODERN_CF"></script>
	<script>
        // Set Session ID for JS
        window.cfSessionId = "<%=sessionId%>";
	</script>

</body>
</html>