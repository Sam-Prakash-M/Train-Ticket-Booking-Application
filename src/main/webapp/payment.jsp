<%@ page contentType="text/html;charset=UTF-8"%>
<%
// Session Logic for Header
String userName = (String) session.getAttribute("user_name");
boolean isLoggedIn = (userName != null);
String userInitial = isLoggedIn ? String.valueOf(userName.charAt(0)).toUpperCase() : "U";

// Data Retrieval
double totalFare = (Double) request.getAttribute("totalFare");
double gst = (Double) request.getAttribute("gst");
double serviceCharge = (Double) request.getAttribute("serviceCharge");
double total = (Double) request.getAttribute("totalPayable");

String[] names = (String[]) request.getAttribute("names");
String[] ages = (String[]) request.getAttribute("ages");
String[] genders = (String[]) request.getAttribute("genders");
String[] berths = (String[]) request.getAttribute("berths");
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Payment Summary | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet" href="payment.css?v=2025_HEADER">
<link rel="icon" type="image/png" href="train_logo_all.png">

<script>
	// Theme Persistence
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="payment.js?v=2026"></script>
</head>
<body>

	<div id="pageLoader" class="loader-overlay">
		<div class="loader-content">
			<div class="spinner"></div>
			<p>Loading Gateway...</p>
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
							class="u-name">My Account</span> <i class="ri-arrow-down-s-line"></i>
					</button>
					<div class="dropdown-content glass">
						<div class="dd-header">
							<strong><%=userName%></strong> <small>Logged In <i
								class="ri-checkbox-circle-fill success-icon"></i></small>
						</div>
						<div class="divider"></div>
						<a href="ProfileUpdate"><i class="ri-user-line"></i> My Profile</a>
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

			<h1 class="page-title">Secure Payment</h1>

			<div class="payment-grid">

				<div class="journey-col">

					<div class="train-card glass animate-up">
						<div class="train-header">
							<div class="t-icon">
								<i class="ri-train-line"></i>
							</div>
							<div>
								<h3><%=session.getAttribute("trainName")%></h3>
								<span class="t-badge"><%=session.getAttribute("trainId")%></span>
							</div>
						</div>

						<div class="route-display">
							<div class="station">
								<span class="s-code"><%=session.getAttribute("source")%></span>
								<span class="s-time"><%=session.getAttribute("sourceDeparture")%></span>
							</div>
							<div class="route-line">
								<span class="travel-date"><%=session.getAttribute("travelDate")%></span>
								<div class="line">
									<i class="ri-arrow-right-line"></i>
								</div>
								<span class="travel-class"><%=session.getAttribute("classType")%></span>
							</div>
							<div class="station" style="text-align: right;">
								<span class="s-code"><%=session.getAttribute("destination")%></span>
								<span class="s-time"><%=session.getAttribute("destinationArrival")%></span>
							</div>
						</div>
					</div>

					<div class="passengers-card glass animate-up delay-1">
						<h3>
							<i class="ri-group-line"></i> Passengers
						</h3>
						<div class="p-list">
							<%
							for (int i = 0; i < names.length; i++) {
							%>
							<div class="p-item">
								<div class="p-icon">
									<i class="ri-user-smile-line"></i>
								</div>
								<div class="p-info">
									<strong><%=names[i]%></strong> <small><%=ages[i]%> yrs
										• <%=genders[i]%> • <%=berths[i]%></small>
								</div>
							</div>
							<%
							}
							%>
						</div>
					</div>

				</div>

				<div class="payment-col">
					<div class="pay-card glass animate-up delay-2">

						<div class="amount-box">
							<span>Total Payable</span>
							<div class="big-price">
								₹
								<%=String.format("%.2f", total)%></div>
						</div>

						<div class="bill-details">
							<div class="row">
								<span>Base Fare</span> <span>₹ <%=totalFare%></span>
							</div>
							<div class="row">
								<span>GST (5%)</span> <span>₹ <%=String.format("%.2f", gst)%></span>
							</div>
							<div class="row">
								<span>Service Charge</span> <span>₹ <%=serviceCharge%></span>
							</div>
							<div class="divider"></div>
							<div class="row total">
								<span>Total</span> <span>₹ <%=String.format("%.2f", total)%></span>
							</div>
						</div>

						<h3 class="method-title">Select Payment Method</h3>

						<div class="gateways">
							<button class="pay-btn razor"
								onclick="payWithRazorpay('<%=total%>')">
								<img
									src="https://upload.wikimedia.org/wikipedia/commons/8/89/Razorpay_logo.svg"
									alt="Razorpay"> <span>Pay via Razorpay</span> <i
									class="ri-arrow-right-line"></i>
							</button>

							<button class="pay-btn cashfree"
								onclick="payWithCashfree('<%=total%>')">
								<img
									src="https://images.seeklogo.com/logo-png/47/1/cashfree-payments-logo-png_seeklogo-479373.png"
									alt="Cashfree"> <span>Pay via Cashfree</span> <i
									class="ri-arrow-right-line"></i>
							</button>

							<button class="pay-btn paypal"
								onclick="payWithPaypal('<%=total%>')">
								<img
									src="https://upload.wikimedia.org/wikipedia/commons/b/b5/PayPal.svg"
									alt="PayPal"> <span>Pay via PayPal</span> <i
									class="ri-arrow-right-line"></i>
							</button>
						</div>

						<div class="secure-footer">
							<i class="ri-shield-check-fill"></i> 100% Secure Transaction
						</div>
					</div>
				</div>

			</div>
		</main>
	</div>

</body>
</html>