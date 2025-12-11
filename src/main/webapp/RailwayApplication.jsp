<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
// Server-side login check
Boolean loggedIn = (session.getAttribute("user_name") != null);
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Sam Railways | Next Gen Booking</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="theme-color" content="#0f172a">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />

<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<link rel="stylesheet" href="RailwayApplication.css?v=2025">

<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="RailwayApplication.js?v=2025" defer></script>
</head>
<body>

	<div class="ambient-light"></div>

	<div class="app-container">

		<nav class="navbar glass">
			<div class="nav-brand">
				<div class="logo-icon">
					<i class="ri-train-fill"></i>
				</div>
				<div class="brand-info">
					<span class="brand-name">Sam Railways</span> <span
						class="brand-tag">Est. 2025</span>
				</div>
			</div>

			<div class="nav-links">
				<a href="RailwayApplication.jsp" class="active"><i
					class="ri-home-5-line"></i> Home</a> <a href="trains.jsp"><i
					class="ri-map-pin-time-line"></i> Trains</a> <a href="pnrstatus.jsp"><i
					class="ri-ticket-line"></i> PNR Status</a> <a href="charts.jsp"><i
					class="ri-bar-chart-grouped-line"></i> Charts</a>
			</div>

			<div class="nav-actions">
				<button id="themeToggle" class="btn-icon" aria-label="Toggle Theme">
					<i class="ri-moon-clear-line"></i>
				</button>

				<%
				if (!loggedIn) {
				%>
				<a href="login.jsp" class="btn btn-ghost">Log In</a> <a
					href="register.jsp" class="btn btn-primary">Sign Up</a>
				<%
				} else {
				%>
				<div class="user-pill">
					<i class="ri-user-smile-line"></i> <span><%=session.getAttribute("user_name")%></span>
				</div>
				<a href="logout" class="btn btn-sm btn-danger"><i
					class="ri-logout-box-r-line"></i></a>
				<%
				}
				%>
			</div>
		</nav>

		<main class="main-content">

			<section class="hero">
				<div class="hero-content">
					<span class="badge">New Experience</span>
					<h1>
						Your Journey,<br>Reimagined.
					</h1>
					<p>Experience the fastest, most secure way to book train
						tickets across the nation.</p>
				</div>
			</section>

			<section class="booking-wrapper">
				<div class="booking-card glass">
					<div class="card-header">
						<h2>
							<i class="ri-search-line"></i> Find Train
						</h2>
					</div>

					<form id="searchForm" class="booking-form" method="GET"
						data-action="<%=loggedIn ? "searchtrains" : "login.jsp"%>">

						<div class="form-grid">
							<div class="route-group">
								<div class="input-wrap">
									<i class="ri-map-pin-line icon"></i> <input id="fromStation"
										name="from" type="text" placeholder="From Station" required
										autocomplete="off"> <label>Source</label>
								</div>

								<button type="button" id="swapStations" class="swap-btn"
									aria-label="Swap Stations">
									<i class="ri-arrow-left-right-line"></i>
								</button>

								<div class="input-wrap">
									<i class="ri-map-pin-range-line icon"></i> <input
										id="toStation" name="to" type="text" placeholder="To Station"
										required autocomplete="off"> <label>Destination</label>
								</div>
							</div>

							<div class="prefs-group">
								<div class="input-wrap">
									<i class="ri-calendar-event-line icon"></i> <input
										id="journeyDate" type="text" name="date"
										placeholder="Select Date" required> <label>Date</label>
								</div>

								<div class="input-wrap">
									<i class="ri-armchair-line icon"></i> <select name="class">
										<option value="ALL">All Classes</option>
										<option value="3A">AC 3 Tier (3A)</option>
										<option value="2A">AC 2 Tier (2A)</option>
										<option value="SL">Sleeper (SL)</option>
										<option value="CC">Chair Car (CC)</option>
										<option value="2S">Second Sitting (2S)</option>
									</select> <label>Class</label>
								</div>

								<div class="input-wrap">
									<i class="ri-vip-crown-line icon"></i> <select name="quota">
										<option value="GENERAL">General</option>
										<option value="TATKAL">Tatkal</option>
										<option value="LADIES">Ladies</option>
										<option value="SENIOR CITIZEN">Senior Citizen</option>
									</select> <label>Quota</label>
								</div>
							</div>
						</div>

						<div class="form-actions">
							<button id="searchBtn" type="submit"
								class="btn btn-primary btn-lg btn-glow">
								Search Trains <i class="ri-arrow-right-line"></i>
							</button>
						</div>
					</form>

					<div id="authNotice" class="auth-notice hidden">
						<div class="notice-content">
							<i class="ri-lock-2-line"></i> <span>Authentication
								required to proceed.</span>
						</div>
						<button id="goLogin" class="btn btn-sm btn-light">Login
							Now</button>
					</div>
				</div>
			</section>

			<section class="features-grid">
				<div class="bento-box feature-pnr">
					<i class="ri-qr-code-line big-icon"></i>
					<h3>PNR Status</h3>
					<p>Check real-time status</p>
				</div>
				<div class="bento-box feature-chart">
					<i class="ri-file-list-3-line big-icon"></i>
					<h3>Charts & Vacancy</h3>
					<p>View seat availability</p>
				</div>
				<div class="bento-box feature-track">
					<i class="ri-map-2-line big-icon"></i>
					<h3>Live Tracking</h3>
					<p>Where is my train?</p>
				</div>
				<div class="bento-box feature-support">
					<i class="ri-customer-service-2-line big-icon"></i>
					<h3>24/7 Support</h3>
				</div>
			</section>

		</main>

		<footer class="footer">
			<div class="footer-content">
				<p>
					&copy; <span id="year"></span> Sam Railways. Built for the future.
				</p>
				<div class="footer-links">
					<a href="#">Privacy</a> <a href="#">Terms</a>
				</div>
			</div>
		</footer>

	</div>

	<div id="toast" class="toast hidden">
		<i class="ri-notification-3-line"></i> <span id="toastMsg">Notification</span>
	</div>

	<script>
		// Inline config passed to external JS
		window.__APP = {
			loggedIn :
	<%=loggedIn ? "true" : "false"%>
		,
			loginUrl : "login.jsp",
			searchAction : "searchtrains"
		};
	</script>
</body>
</html>