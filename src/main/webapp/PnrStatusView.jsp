<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.samprakash.ticketbookmodel.Ticket"%>
<%@ page import="com.samprakash.paymentmodel.Passenger"%>
<%@ page import="java.util.Set"%>

<%
// Data from Servlet
Ticket ticket = (Ticket) request.getAttribute("pnrTicket");
String pnrStatus = (String) request.getAttribute("pnrStatus");
String statusMsg = (String) request.getAttribute("statusMsg");
String error = (String) request.getAttribute("error");

// Header Logic
String userName = (String) session.getAttribute("user_name");
boolean isLoggedIn = (userName != null);
String userInitial = isLoggedIn ? String.valueOf(userName.charAt(0)).toUpperCase() : "U";
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Check PNR Status | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="icon" type="image/png"
	href="https://cdn-icons-png.flaticon.com/512/1036/1036137.png">
<link rel="stylesheet" href="PnrStatusView.css?v=2026_FULL">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="PnrStatusView.js?v=2026_FULL"></script>
</head>
<body>

	<div class="ambient-light-neon"></div>

	<div class="app-container">

		<nav class="navbar glass">
			<div class="nav-brand">
				<div class="logo-box">
					<i class="ri-train-line"></i>
				</div>
				<span class="brand-text">Sam Railways</span>
			</div>
			<div class="nav-menu">
				<a href="RailwayApplication.jsp"><i class="ri-home-5-line"></i>
					Home</a> <a href="MyBookings"><i class="ri-ticket-2-line"></i>
					Bookings</a>
			</div>
			<div class="nav-profile">
				<button id="themeToggle" class="theme-btn">
					<i class="ri-sun-line light-icon"></i> <i
						class="ri-moon-line dark-icon"></i>
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
				} else {
				%>
				<a href="login.jsp" class="btn-login">Login</a>
				<%
				}
				%>
			</div>
		</nav>

		<main class="main-content">

			<div class="search-card glass animate-up">
				<h1>PNR Status</h1>
				<p>Enter your 10-digit PNR number to check live status.</p>

				<form action="PnrStatus" method="post" class="pnr-search-form">
					<div class="input-wrapper">
						<i class="ri-qr-code-line icon"></i> <input type="text"
							name="pnrInput" placeholder="Enter PNR Number" maxlength="25"
							required autocomplete="off"
							value="<%=(ticket != null) ? ticket.getPnrNumber() : ""%>">
					</div>
					<button type="submit" class="btn-search">
						Check Status <i class="ri-search-2-line"></i>
					</button>
				</form>
			</div>

			<%
			if (ticket != null) {
			%>
			<div class="result-card glass animate-up-delay">

				<div class="ticket-header">
					<div class="train-info">
						<h2><%=ticket.getTrainName()%></h2>
						<span class="train-id">Train #<%=ticket.getTrainId()%></span>

						<div class="route-info">
							<span><%=ticket.getSourceArr()%></span> <i
								class="ri-arrow-right-line"></i> <span><%=ticket.getDestinationArr()%></span>
						</div>
					</div>

					<div class="meta-info">
						<div class="meta-item">
							<span class="label">Journey Date</span> <span class="val"><%=ticket.getBookingDate()%></span>
						</div>
						<div class="meta-item">
							<span class="label">Class</span> <span class="val"><%=ticket.getClassName()%></span>
						</div>
					</div>
				</div>

				<div
					class="status-banner <%="FLUSHED".equals(pnrStatus) ? "banner-flushed" : "banner-live"%>">
					<%
					if ("FLUSHED".equals(pnrStatus)) {
					%>
					<i class="ri-history-line"></i> Journey Completed / Chart Flushed
					<%
					} else {
					%>
					<i class="ri-broadcast-line"></i> Live Status
					<%
					}
					%>
				</div>

				<div class="passenger-list">
					<h4>Passenger Details</h4>
					<div class="table-responsive">
						<table class="pax-table">
							<thead>
								<tr>
									<th>#</th>
									<th>Name</th>
									<th>Current Status</th>
									<th>Coach / Seat</th>
								</tr>
							</thead>
							<tbody>
								<%
								int i = 1;
								for (Passenger p : ticket.getAssociatedPassenger()) {
									String rawStatus = p.getTicketStatus();
									String displayStatus = rawStatus;
									String statusClass = "st-pending";
									String coachSeat = "-";

									if (rawStatus.contains("CNF")) {
										statusClass = "st-cnf";
										displayStatus = "Confirmed";
										if (p.getSeatMetaData() != null) {
									coachSeat = p.getSeatMetaData().getCoachNo() + " / " + p.getSeatMetaData().getSeatNumber();
										} else if (rawStatus.contains("/")) {
									coachSeat = rawStatus.substring(rawStatus.indexOf("/") + 1);
										}
									} else if (rawStatus.contains("RAC")) {
										statusClass = "st-rac";
										displayStatus = "RAC";
									} else if (rawStatus.contains("WL")) {
										statusClass = "st-wl";
										displayStatus = "Waitlist";
									} else if (rawStatus.contains("CAN")) {
										statusClass = "st-can";
										displayStatus = "Cancelled";
									}
								%>
								<tr>
									<td><%=i++%></td>
									<td><%=p.getName()%></td>
									<td><span class="status-badge <%=statusClass%>"><%=displayStatus%></span></td>
									<td class="seat-info"><%=coachSeat%></td>
								</tr>
								<%
								}
								%>
							</tbody>
						</table>
					</div>
				</div>

				<div class="ticket-footer">
					<p>
						Total Fare: <strong>₹<%=ticket.getTotalFare()%></strong>
					</p>
					<button class="btn-print" onclick="window.print()">
						<i class="ri-printer-line"></i> Print Ticket
					</button>
				</div>
			</div>
			<%
			}
			%>

			<%
			if (error != null) {
			%>
			<div class="error-card glass animate-up-delay">
				<div class="icon-box-error">
					<i class="ri-error-warning-line"></i>
				</div>
				<h2>No Details Found</h2>
				<p><%=error%></p>
			</div>
			<%
			}
			%>

		</main>
	</div>

</body>
</html>