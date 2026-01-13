<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
// Server-side login check
String userName = (String) session.getAttribute("user_name");
boolean loggedIn = (userName != null);
String userInitial = loggedIn ? String.valueOf(userName.charAt(0)).toUpperCase() : "U";

// Date Logic
java.time.LocalDate today = java.time.LocalDate.now();
java.time.LocalDate maxDate = today.plusDays(90);
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Sam Railways | Next Gen Travel</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="theme-color" content="#0f172a">
<link rel="icon" type="image/png" href="train_logo_all.png">
<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<link rel="stylesheet" href="RailwayApplication.css?v=2025_ULTRA_FIX">

<script>
        const savedTheme = localStorage.getItem('sam_theme') || 'light';
        if (savedTheme === 'dark') document.documentElement.setAttribute('data-theme', 'dark');
    </script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="RailwayApplication.js?v=2025_ULTRA_FIX" defer></script>
</head>
<body>

	<div id="appLoader" class="loader-overlay">
		<div class="loader-track">
			<div class="train-head">
				<i class="ri-train-fill"></i>
				<div class="light-beam"></div>
			</div>
			<div class="track-lines"></div>
		</div>
		<h2 class="loader-text">
			Sam Railways <span class="dot">.</span><span class="dot">.</span><span
				class="dot">.</span>
		</h2>
	</div>

	<div class="ambient-glow"></div>
	<div class="grid-mesh"></div>

	<div class="app-container">

		<nav class="navbar" id="mainNav">
			<div class="nav-brand">
				<div class="brand-logo">
					<i class="ri-train-fill"></i>
				</div>
				<span class="brand-text">Sam Railways</span>
			</div>

			<div class="nav-links desktop-only">
				<a href="#" class="active">Home</a> <a href="TrainData">Trains</a>
				<a href="PnrStatus">PNR Status</a> <a href="charts.jsp">Charts</a>
			</div>

			<div class="nav-right">
				<button id="themeToggle" class="icon-btn">
					<i class="ri-moon-clear-line"></i>
				</button>

				<%
				if (!loggedIn) {
				%>
				<a href="login.jsp" class="btn btn-login">Login</a> <a
					href="register.jsp" class="btn btn-signup">Sign Up</a>
				<%
				} else {
				%>
				<div class="user-menu-wrap">
					<button class="user-pill">
						<span class="avatar"><%=userInitial%></span> <span class="name"><%=userName%></span>
						<i class="ri-arrow-down-s-line"></i>
					</button>
					<div class="dropdown-menu glass">
						<div class="dd-header">
							<strong><%=userName%></strong> <small>Logged In <i
								class="ri-checkbox-circle-fill success-icon"></i></small>
						</div>
						<div class="divider"></div>
						<a href="ProfileUpdate"><i class="ri-user-line"></i> My Profile</a>
						<a href="TransactionList"><i class="ri-exchange-dollar-line"></i>
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
				}
				%>
			</div>
		</nav>

		<main class="main-content">

			<section class="hero-section">
				<div class="hero-text">
					<div class="badge-pill">
						<span class="pulse"></span> Live Booking Open
					</div>
					<h1>
						Experience the <br>
						<span class="gradient-text">Future of Travel</span>
					</h1>
					<p>Seamless booking, live tracking, and instant PNR status.</p>
				</div>
			</section>

			<div class="search-widget glass">
				<form id="searchForm"
					action="<%=loggedIn ? "SearchServlet" : "login.jsp"%>"
					method="GET">

					<div class="search-grid">
						<div class="input-group">
							<label>From</label>
							<div class="field">
								<i class="ri-map-pin-line"></i> <input type="text"
									name="fromStation" id="fromStation" placeholder="Origin"
									required autocomplete="off">
							</div>
						</div>

						<button type="button" id="swapBtn" class="swap-btn">
							<i class="ri-arrow-left-right-line"></i>
						</button>

						<div class="input-group">
							<label>To</label>
							<div class="field">
								<i class="ri-map-pin-range-line"></i> <input type="text"
									name="toStation" id="toStation" placeholder="Destination"
									required autocomplete="off">
							</div>
						</div>

						<div class="input-group">
							<label>Date</label>
							<div class="field">
								<i class="ri-calendar-line"></i> <input type="text"
									name="travelDate" id="travelDate" placeholder="Select Date"
									required>
							</div>
						</div>

						<div class="input-group">
							<label>Class</label>
							<div class="field select-field">
								<i class="ri-armchair-line"></i> <select name="trainClass">
									<option value="ALL">All Classes</option>
									<option value="SL">Sleeper (SL)</option>
									<option value="3A">AC 3 Tier (3A)</option>
									<option value="2A">AC 2 Tier (2A)</option>
									<option value="1A">AC First Class (1A)</option>
									<option value="CC">Chair Car (CC)</option>
								</select> <i class="ri-arrow-down-s-line arrow-icon"></i>
							</div>
						</div>
					</div>

					<div class="search-footer">
						<div class="quota-options">
							<label class="radio-pill"><input type="radio"
								name="quota" value="GN" checked> <span>General</span></label> <label
								class="radio-pill"><input type="radio" name="quota"
								value="TQ"> <span>Tatkal</span></label> <label
								class="radio-pill"><input type="radio" name="quota"
								value="LD"> <span>Ladies</span></label><label
								class="radio-pill"><input type="radio" name="quota"
								value="SS"> <span>Senior Citizen</span></label>
								
						</div>
						<button type="submit" class="btn-search-glow">
							Search Trains <i class="ri-arrow-right-line"></i>
						</button>
					</div>

				</form>
			</div>

			<section class="features">
				<div class="feature-card">
					<div class="icon-box blue">
						<i class="ri-train-wifi-line"></i>
					</div>
					<h3>Live Status</h3>
					<p>Track your train in real-time with precision GPS.</p>
				</div>
				<div class="feature-card">
					<div class="icon-box purple">
						<i class="ri-coupon-3-line"></i>
					</div>
					<h3>Best Offers</h3>
					<p>Get exclusive discounts on your first booking.</p>
				</div>
				<div class="feature-card">
					<div class="icon-box orange">
						<i class="ri-customer-service-2-line"></i>
					</div>
					<h3>24/7 Support</h3>
					<p>We are here to help you anytime, anywhere.</p>
				</div>
			</section>

		</main>

		<footer class="footer">
            <div class="footer-content">
                <div class="footer-brand">
                    <div class="brand-logo small"><i class="ri-train-fill"></i></div>
                    <span class="brand-text">Sam Railways</span>
                    <p class="brand-tagline">Redefining the journey since 2025.</p>
                </div>

                <div class="footer-links">
                    <a href="#">About Us</a>
                    <a href="#">Privacy Policy</a>
                    <a href="#">Terms of Service</a>
                    <a href="#">Support</a>
                </div>

                <div class="footer-social">
                    <a href="#" class="social-icon"><i class="ri-twitter-x-line"></i></a>
                    <a href="#" class="social-icon"><i class="ri-linkedin-fill"></i></a>
                    <a href="#" class="social-icon"><i class="ri-instagram-line"></i></a>
                </div>
            </div>
            
            <div class="footer-bottom">
                <p>&copy; 2025 Sam Railways Inc. All rights reserved.</p>
                <p>Made with <i class="ri-heart-fill" style="color: #ef4444;"></i> for Travelers</p>
            </div>
        </footer>

	</div>

	<script>
        window.__CONFIG = {
            loggedIn: <%=loggedIn%>,
            minDate: "<%=today%>",
            maxDate: "<%=maxDate%>
		"
		};
	</script>

</body>
</html>