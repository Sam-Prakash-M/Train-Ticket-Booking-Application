<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
// Session Logic for Header
String userName = (String) session.getAttribute("user_name");
boolean isLoggedIn = (userName != null);
String userInitial = isLoggedIn ? String.valueOf(userName.charAt(0)).toUpperCase() : "U";

// Date Logic
java.time.LocalDate today = java.time.LocalDate.now();
java.time.LocalDate maxDate = today.plusDays(60);
String todayFormatted = today.toString();
String maxDateFormatted = maxDate.toString();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Sam Railways | Booking Portal</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>

<link rel="stylesheet" href="ticketsearch.css?v=2025_HEADER">
<link rel="icon" type="image/png" href="train_logo_all.png">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="ticketsearch.js?v=2025_HEADER"></script>
</head>
<body>

	<div id="pageLoader" class="loader-overlay">
		<div class="loader-content">
			<div class="train-icon">
				<i class="ri-train-line"></i>
			</div>
			<div class="loader-bar"></div>
			<p>Loading Experience...</p>
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
				<a href="#" class="active"><i class="ri-home-5-line"></i> Home</a> <a
					href="#pnr-status"><i class="ri-qr-code-line"></i> PNR Status</a> <a
					href="#chart-vacancy"><i class="ri-bar-chart-horizontal-line"></i>
					Charts</a>
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
			<div class="booking-card glass">

				<div class="tab-header">
					<button class="tab-btn active" data-target="book-ticket">
						<i class="ri-ticket-2-line"></i> Book Ticket
					</button>
					<button class="tab-btn" data-target="pnr-status">
						<i class="ri-qr-code-line"></i> PNR Status
					</button>
					<button class="tab-btn" data-target="chart-vacancy">
						<i class="ri-file-list-3-line"></i> Charts
					</button>
				</div>

				<section id="book-ticket" class="tab-content active">
					<div class="content-header">
						<h2>Plan Your Journey</h2>
						<p>Select your route and preferences below.</p>
					</div>

					<form action="SearchServlet" method="get" class="modern-form">

						<div class="route-grid">
							<div class="input-wrap">
								<i class="ri-map-pin-line icon"></i> <input type="text"
									id="source" name="fromStation" placeholder="Source Station"
									required autocomplete="off"> <label>From</label>
							</div>

							<button type="button" id="swapStations" class="swap-btn">
								<i class="ri-arrow-left-right-line"></i>
							</button>

							<div class="input-wrap">
								<i class="ri-map-pin-range-line icon"></i> <input type="text"
									id="destination" name="toStation"
									placeholder="Destination Station" required autocomplete="off">
								<label>To</label>
							</div>
						</div>

						<div class="options-grid">
							<div class="input-wrap">
								<i class="ri-calendar-event-line icon"></i> <input
									id="travelDate" type="text" name="travelDate"
									data-min="<%=todayFormatted%>" data-max="<%=maxDateFormatted%>"
									placeholder="Select Date"> <label>Date</label>
							</div>

							<div class="input-wrap">
								<i class="ri-armchair-line icon"></i> <select name="trainClass"
									required>
									<option value="ALL">All Classes</option>
									<option value="3A">AC 3 Tier (3A)</option>
									<option value="2A">AC 2 Tier (2A)</option>
									<option value="SL">Sleeper (SL)</option>
									<option value="CC">Chair Car (CC)</option>
									<option value="2S">Second Sitting (2S)</option>
								</select> <label>Class</label>
							</div>

							<div class="input-wrap">
								<i class="ri-vip-crown-line icon"></i> <select name="quota"
									required>
									<option value="GN">General</option>
									<option value="LD">Ladies</option>
									<option value="SS">Senior Citizen</option>
									<option value="PWD">Person with Disability</option>
									<option value="TQ">Tatkal</option>
								</select> <label>Quota</label>
							</div>
						</div>

						<button type="submit" class="btn btn-primary btn-full">
							Search Trains <i class="ri-arrow-right-line"></i>
						</button>
					</form>
				</section>

				<section id="pnr-status" class="tab-content">
					<div class="content-header">
						<h2>Check PNR Status</h2>
						<p>Enter your 10-digit PNR number to check live status.</p>
					</div>
					<form action="PNRServlet" method="get" class="modern-form">
						<div class="input-wrap">
							<i class="ri-hashtag icon"></i> <input type="text" name="pnr"
								maxlength="10" placeholder="e.g. 4251234567" required> <label>PNR
								Number</label>
						</div>
						<button type="submit" class="btn btn-primary btn-full">
							Check Status</button>
					</form>
				</section>

				<section id="chart-vacancy" class="tab-content">
					<div class="content-header">
						<h2>Charts & Vacancy</h2>
						<p>See reserved charts and vacant berths.</p>
					</div>
					<form action="ChartServlet" method="get" class="modern-form">
						<div class="options-grid">
							<div class="input-wrap">
								<i class="ri-train-line icon"></i> <input type="text"
									name="trainNo" placeholder="Train No." required> <label>Train
									Number</label>
							</div>
							<div class="input-wrap">
								<i class="ri-calendar-line icon"></i> <input id="chartDate"
									type="text" name="chartDate" data-min="<%=todayFormatted%>"
									data-max="<%=maxDateFormatted%>" placeholder="Date"> <label>Journey
									Date</label>
							</div>
						</div>
						<button type="submit" class="btn btn-primary btn-full">
							Get Charts</button>
					</form>
				</section>

			</div>
		</main>
	</div>

</body>
</html>