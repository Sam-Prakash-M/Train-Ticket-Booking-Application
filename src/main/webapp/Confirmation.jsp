<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
// Session Logic for Header
String userName = (String) session.getAttribute("user_name");
boolean isLoggedIn = (userName != null);
String userInitial = isLoggedIn ? String.valueOf(userName.charAt(0)).toUpperCase() : "U";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Confirm Booking | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet" href="Confirmation.css?v=2025_HEADER">

<script>
    const savedTheme = localStorage.getItem('sam_theme') || 'light';
    if (savedTheme === 'dark') document.documentElement.setAttribute('data-theme', 'dark');
    
    window.CLASS_TYPE_RAW = "<%=request.getAttribute("classType") != null ? request.getAttribute("classType") : "SL"%>";
    window.BASE_FARE = parseFloat("<%=request.getAttribute("fare") != null ? request.getAttribute("fare") : "0"%>");

	function extractClassCode(str) {
		if (!str)
			return "SL";
		const match = str.match(/\((.*?)\)/);
		return match ? match[1] : str;
	}
	window.TRAIN_CLASS_CODE = extractClassCode(window.CLASS_TYPE_RAW);
</script>
<script defer src="Confirmation.js?v=2025_HEADER"></script>
</head>
<body>

	<div id="pageLoader" class="loader-overlay">
		<div class="loader-content">
			<div class="spinner"></div>
			<p>Initializing Booking...</p>
		</div>
	</div>

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
					Home</a> <a href="#" class="active"><i class="ri-file-list-3-line"></i>
					Booking</a> <a href="pnrstatus.jsp"><i class="ri-qr-code-line"></i>
					PNR Status</a>
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
			<h1 class="page-title">Review Journey</h1>

			<form action="PaymentServlet" method="post" id="passengerForm"
				class="booking-grid">

				<div class="left-col">

					<section class="card glass" style="overflow: visible;">
						<div class="card-header">
							<h3>
								<i class="ri-group-line"></i> Passenger Details
							</h3>
							<span class="info-badge">Max 6</span>
						</div>
						<div id="passengerContainer">
							<div class="passenger-row">
								<div class="p-header">
									<span class="p-count">Passenger 1</span>
								</div>
								<div class="p-inputs">
									<input type="text" name="pname[]" placeholder="Full Name"
										required>
									<div class="row-split">
										<input type="number" name="page[]" min="1" max="120"
											placeholder="Age" required>
										<div class="select-wrap">
											<select name="pgender[]">
												<option value="Male">Male</option>
												<option value="Female">Female</option>
												<option value="Other">Other</option>
											</select> <i class="ri-arrow-down-s-line"></i>
										</div>
									</div>
									<div class="row-split">
										<div class="select-wrap">
											<select name="pnationality[]">
												<option value="India">India</option>
												<option value="Other">Other</option>
											</select> <i class="ri-arrow-down-s-line"></i>
										</div>
										<div class="select-wrap">
											<select name="berth[]" class="berth-select"></select> <i
												class="ri-arrow-down-s-line"></i>
										</div>
									</div>
								</div>
								<button type="button" class="remove-btn hidden"
									onclick="removePassenger(this)">
									<i class="ri-delete-bin-line"></i>
								</button>
							</div>
						</div>
						<button type="button" id="addPassengerBtn" class="add-btn">
							<i class="ri-add-line"></i> Add Passenger
						</button>
					</section>

					<section class="card glass">
						<div class="card-header">
							<h3>
								<i class="ri-contacts-book-line"></i> Contact Details
							</h3>
						</div>
						<div class="contact-grid">
							<div class="input-wrap">
								<label>Mobile Number</label>
								<div class="field-box">
									<span class="prefix">+91</span><input type="tel" name="mobile"
										required>
								</div>
							</div>
							<div class="input-wrap">
								<label>Email Address</label>
								<div class="field-box">
									<input type="email" name="email" required>
								</div>
							</div>
						</div>
					</section>

					<section class="card glass">
						<div class="card-header">
							<h3>
								<i class="ri-settings-4-line"></i> Preferences
							</h3>
						</div>
						<div class="pref-content">
							<label class="checkbox-container"> <input type="checkbox"
								name="autoUpgrade" value="true"> <span class="text">Consider
									for Auto Upgradation</span>
							</label>
						</div>
					</section>

				</div>

				<div class="right-col">
					<div class="summary-card glass sticky-card">
						<div class="summary-header">
							<h3>Journey Summary</h3>
						</div>
						<div class="train-details">
							<div class="train-top">
								<h4><%=request.getAttribute("trainName")%></h4>
								<span class="t-badge"><%=request.getAttribute("trainId")%></span>
							</div>
							<div class="route-line">
								<div>
									<span class="station-code"><%=request.getAttribute("source")%></span>
									<span class="s-time"><%=request.getAttribute("sourceDeparture")%></span>
								</div>
								<div class="arrow-line">
									<span><%=request.getAttribute("classType")%></span>
									<div class="line"></div>
									<i class="ri-train-line"></i>
								</div>
								<div style="text-align: right;">
									<span class="station-code"><%=request.getAttribute("destination")%></span>
									<span class="s-time"><%=request.getAttribute("destinationArrival")%></span>
								</div>
							</div>
							<div class="date-row">
								<i class="ri-calendar-event-line"></i>
								<%=request.getAttribute("travelDate")%>
							</div>
						</div>

						<div class="divider"></div>

						<div class="fare-breakdown">
							<div class="fare-row">
								<span>Base Fare</span> <span id="baseFareDisplay">₹<%=request.getAttribute("fare")%></span>
							</div>
							<div class="fare-row">
								<span>Passengers</span> <span id="pCountDisplay">x 1</span>
							</div>
							<div class="fare-row gst-row">
								<span>Tax & GST (5%)</span> <span id="taxDisplay">₹0.00</span>
							</div>
							<div class="divider-dashed"></div>
							<div class="fare-total">
								<span>Total Payable</span> <span id="totalFareDisplay">₹0.00</span>
							</div>
						</div>

						<div class="action-buttons">
							<button type="submit" class="pay-btn">
								Proceed to Pay <i class="ri-arrow-right-line"></i>
							</button>
							<button type="button" class="cancel-btn" onclick="history.back()">Cancel</button>
						</div>
					</div>
				</div>

				<input type="hidden" name="baseFare" id="inputBaseFare"
					value="<%=request.getAttribute("fare")%>"> <input
					type="hidden" name="totalFare" id="inputTotalFare" value="">
				<input type="hidden" name="trainName"
					value="<%=request.getAttribute("trainName")%>"> <input
					type="hidden" name="trainId"
					value="<%=request.getAttribute("trainId")%>"> <input
					type="hidden" name="source"
					value="<%=request.getAttribute("source")%>"> <input
					type="hidden" name="destination"
					value="<%=request.getAttribute("destination")%>"> <input
					type="hidden" name="sourceDeparture"
					value="<%=request.getAttribute("sourceDeparture")%>"> <input
					type="hidden" name="sourceArrival"
					value="<%=request.getAttribute("sourceArrival")%>"> <input
					type="hidden" name="destinationArrival"
					value="<%=request.getAttribute("destinationArrival")%>"> <input
					type="hidden" name="destinationDeparture"
					value="<%=request.getAttribute("destinationDeparture")%>">
				<input type="hidden" name="classType"
					value="<%=request.getAttribute("classType")%>"> <input
					type="hidden" name="travelDate"
					value="<%=request.getAttribute("travelDate")%>">

			</form>
		</main>
	</div>
	<div id="toast" class="toast"></div>
</body>
</html>