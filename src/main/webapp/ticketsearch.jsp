<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
String userName = (String) session.getAttribute("user_name");
boolean isLoggedIn = (userName != null);
String userInitial = isLoggedIn ? String.valueOf(userName.charAt(0)).toUpperCase() : "U";
String displayName = isLoggedIn ? userName : "Traveler";

java.time.LocalDate today = java.time.LocalDate.now();
java.time.LocalDate maxDate = today.plusDays(120);
String todayFormatted = today.toString();
String maxDateFormatted = maxDate.toString();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Sam Railways | NextGen Booking</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link
	href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&family=Space+Grotesk:wght@500;700&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<link rel="icon" type="image/png" href="train_logo_all.png">
<style>
/* Default Variables for Loader */
:root {
	--loader-bg: #f0f4f8;
	--loader-text: #0f172a;
	--loader-primary: #4f46e5;
}

[data-theme="dark"] {
	--loader-bg: #09090b;
	--loader-text: #f8fafc;
	--loader-primary: #818cf8;
}

/* Loader Layout */
#pageLoader {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: var(--loader-bg);
	color: var(--loader-text);
	z-index: 9999; /* Highest priority */
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	transition: opacity 0.5s ease, visibility 0.5s;
}

/* Loader Elements */
.loader-content {
	text-align: center;
	font-family: 'Outfit', sans-serif;
}

.train-icon {
	font-size: 3rem;
	color: var(--loader-primary);
	margin-bottom: 1rem;
	animation: bounce 1s infinite alternate;
}

.loader-bar {
	width: 200px;
	height: 4px;
	background: rgba(128, 128, 128, 0.2);
	border-radius: 4px;
	overflow: hidden;
	margin: 0 auto 10px;
	position: relative;
}

.loader-bar::after {
	content: '';
	position: absolute;
	left: 0;
	top: 0;
	bottom: 0;
	width: 40%;
	background: var(--loader-primary);
	animation: loadSlide 1.5s infinite linear;
}

@
keyframes bounce {to { transform:translateY(-10px);
	
}

}
@
keyframes loadSlide { 0% {
	left: -40%;
}
100
%
{
left
:
100%;
}
}
</style>

<link rel="stylesheet" href="ticketsearch.css?v=2026_FINAL">

<script>
	// Apply theme immediately to avoid white flash in dark mode
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script defer src="ticketsearch.js?v=2026_FINAL"></script>
</head>
<body>

	<div id="pageLoader">
		<div class="loader-content">
			<div class="train-icon">
				<i class="ri-train-line"></i>
			</div>
			<div class="loader-bar"></div>
			<p>Initializing Experience...</p>
		</div>
	</div>

	<div class="ambient-mesh"></div>

	<div class="app-container">

		<nav class="navbar glass-panel">
			<div class="nav-brand">
				<div class="logo-box">
					<i class="ri-train-fill"></i>
				</div>
				<span class="brand-text">Sam Railways</span>
			</div>

			<div class="nav-menu">
				<a href="#" class="nav-link active"><i class="ri-home-5-line"></i>
					<span>Home</span></a>
			</div>

			<div class="nav-profile">
				<button id="themeToggle" class="icon-btn theme-toggle-btn">
					<i class="ri-sun-line light-icon"></i> <i
						class="ri-moon-line dark-icon"></i>
				</button>

				<%
				if (isLoggedIn) {
				%>
				<div class="user-dropdown">
					<button class="user-btn">
						<span class="u-avatar"><%=userInitial%></span> <span
							class="u-name">My Account</span> <i class="ri-arrow-down-s-line"></i>
					</button>
					<div class="dropdown-menu glass-panel">
						<div class="dd-header">
							<strong><%=userName%></strong> <small>Logged In <i
								class="ri-checkbox-circle-fill success-icon"></i></small>
						</div>
						<div class="divider"></div>
						<a href="ProfileUpdate"><i class="ri-user-line"></i> My
							Profile</a> <a href="TransactionList"><i
							class="ri-exchange-dollar-line"></i> My Transactions</a> <a
							href="MyBookings"><i class="ri-history-line"></i> Booked
							History</a> <a href="refunds.jsp"><i class="ri-refund-2-line"></i>
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

			<div class="hero-section animate-fade">
				<h1>
					Hello, <span class="gradient-text"><%=displayName%></span> 👋
				</h1>
				<p>Where would you like to travel today?</p>
			</div>

			<div class="booking-card glass-panel animate-up">

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
					<form action="SearchServlet" method="get" class="modern-form">
						<div class="route-grid">
							<div class="input-group">
								<i class="ri-map-pin-line icon"></i> <input type="text"
									id="source" name="fromStation" placeholder=" " required
									autocomplete="off"> <label>From Station</label>
							</div>
							<button type="button" id="swapStations" class="swap-btn"
								title="Swap">
								<i class="ri-arrow-left-right-line"></i>
							</button>
							<div class="input-group">
								<i class="ri-map-pin-range-line icon"></i> <input type="text"
									id="destination" name="toStation" placeholder=" " required
									autocomplete="off"> <label>To Station</label>
							</div>
						</div>

						<div class="options-grid">
							<div class="input-group">
								<i class="ri-calendar-event-line icon"></i> <input
									id="travelDate" type="text" name="travelDate"
									data-min="<%=todayFormatted%>" data-max="<%=maxDateFormatted%>"
									placeholder=" " required> <label>Date</label>
							</div>
							<div class="input-group">
								<i class="ri-armchair-line icon"></i> <select name="trainClass"
									required>
									<option value="ALL">All Classes</option>
									<option value="3A">AC 3 Tier (3A)</option>
									<option value="2A">AC 2 Tier (2A)</option>
									<option value="SL">Sleeper (SL)</option>
									<option value="CC">Chair Car (CC)</option>
								</select> <label>Class</label>
							</div>
							<div class="input-group">
								<i class="ri-vip-crown-line icon"></i> <select name="quota"
									required>
									<option value="GN">General</option>
									<option value="LD">Ladies</option>
									<option value="SS">Senior Citizen</option>
									<option value="TQ">Tatkal</option>
								</select> <label>Quota</label>
							</div>
						</div>

						<button type="submit" class="btn-primary btn-full hover-glow">
							Search Trains <i class="ri-arrow-right-line"></i>
						</button>
					</form>
				</section>

				<section id="pnr-status" class="tab-content">
					<form id="pnrForm" class="modern-form" onsubmit="return false;">
						<div class="input-group">
							<i class="ri-hashtag icon"></i> <input type="text" id="pnrInput"
								name="pnr" maxlength="50" placeholder=" " required
								autocomplete="off"> <label>Enter 10-digit PNR</label>
						</div>
						<button type="submit" class="btn-primary btn-full hover-glow">
							<span id="pnrBtnText">Check Status</span> <i
								class="ri-loader-4-line ri-spin hidden" id="pnrLoader"></i>
						</button>
					</form>
					<div id="pnrResultContainer" class="pnr-result-box hidden"></div>
				</section>

				<section id="chart-vacancy" class="tab-content">
					<form action="ChartServlet" method="get" class="modern-form">
						<div class="options-grid">
							<div class="input-group">
								<i class="ri-train-line icon"></i> <input type="text"
									name="trainNo" placeholder=" " required> <label>Train
									Number</label>
							</div>
							<div class="input-group">
								<i class="ri-calendar-line icon"></i> <input id="chartDate"
									type="text" name="chartDate" data-min="<%=todayFormatted%>"
									data-max="<%=maxDateFormatted%>" placeholder=" "> <label>Date</label>
							</div>
						</div>
						<button type="submit" class="btn-primary btn-full hover-glow">Get
							Charts</button>
					</form>
				</section>

			</div>
		</main>
	</div>

</body>
</html>