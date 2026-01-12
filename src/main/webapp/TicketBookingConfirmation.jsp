<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
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
<title>Booking Confirmed | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet"
	href="TicketBookingConfirmation.css?v=2025_HEADER">
	<link rel="icon" type="image/png" href="train_logo_all.png">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="TicketBookingConfirmation.js?v=2025_HEADER"></script>
</head>
<body>

	<div class="app-container">

		<nav class="navbar glass no-print">
			<div class="nav-brand">
				<div class="logo-box">
					<i class="ri-train-fill"></i>
				</div>
				<span class="brand-text">Sam Railways</span>
			</div>

			<div class="nav-menu">
				<a href="RailwayApplication.jsp"><i class="ri-home-5-line"></i>
					Home</a> <a href="MyBookings"><i class="ri-ticket-2-line"></i>
					Bookings</a> <a href="pnrstatus.jsp"><i class="ri-qr-code-line"></i>
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

			<c:if test="${not empty errorMessage}">
				<div class="card error-card glass">
					<div class="icon-box error">
						<i class="ri-close-circle-fill"></i>
					</div>
					<h2>Booking Failed</h2>
					<p>${errorMessage}</p>
					<div class="actions">
						<a href="RailwayApplication.jsp" class="btn primary">Try Again</a>
					</div>
				</div>
			</c:if>

			<c:if test="${not empty ConfirmedTicket}">
				<div class="ticket-wrapper">

					<div class="success-banner glass no-print">
						<div class="banner-content">
							<div class="check-icon">
								<i class="ri-check-line"></i>
							</div>
							<div>
								<h1>Booking Confirmed!</h1>
								<p>Your ticket has been sent to your email.</p>
							</div>
						</div>
						<div class="banner-actions">
							<button class="btn outline" id="printBtn">
								<i class="ri-printer-line"></i> Print
							</button>
							<button class="btn primary"
								onclick="window.location.href='RailwayApplication.jsp'">Book
								Another</button>
						</div>
					</div>

					<div class="ticket-card glass" id="printableTicket">

						<div class="ticket-header">
							<div class="pnr-box">
								<span class="label">PNR NUMBER</span> <span class="value"
									id="pnrText">${ConfirmedTicket.pnrNumber}</span>
								<button class="copy-icon no-print" id="copyPnr" title="Copy PNR">
									<i class="ri-file-copy-line"></i>
								</button>
							</div>
							<div class="train-info">
								<h2>${ConfirmedTicket.trainName}</h2>
								<span class="train-badge">${ConfirmedTicket.trainId}</span>
							</div>
							<div class="qr-placeholder">
								<i class="ri-qr-code-line"></i>
							</div>
						</div>

						<div class="divider"></div>

						<div class="route-info">
							<div class="station">
								<span class="code">${ConfirmedTicket.sourceArr}</span> <span
									class="lbl">Source</span>
							</div>
							<div class="path-line">
								<span class="class-lbl">${ConfirmedTicket.className}</span>
								<div class="line">
									<i class="ri-train-line"></i>
								</div>
								<span class="date-lbl">Transaction:
									${ConfirmedTicket.transactionId}</span>
							</div>
							<div class="station right">
								<span class="code">${ConfirmedTicket.destinationArr}</span> <span
									class="lbl">Destination</span>
							</div>
						</div>

						<div class="passenger-section">
							<h3>Passenger Details</h3>
							<table class="p-table">
								<thead>
									<tr>
										<th>#</th>
										<th>Name</th>
										<th>Age/Sex</th>
										<th>Preference</th>
										<th>Status</th>
										<th>Coach/Seat</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="p"
										items="${ConfirmedTicket.associatedPassenger}"
										varStatus="loop">
										<tr>
											<td>${loop.index + 1}</td>
											<td><strong>${p.name}</strong></td>
											<td>${p.age}/${p.gender}</td>
											<td>${p.preference}</td>

											<td><span
												class="status-badge ${p.ticketStatus.startsWith('CNF') ? 'cnf' : 'wl'}">
													${p.ticketStatus.startsWith('CNF') ? 'CONFIRMED' : p.ticketStatus}
											</span></td>

											<td><c:choose>
													<c:when test="${p.ticketStatus.startsWith('CNF')}">
														<b>${p.seatMetaData.coachNo}</b> - ${p.seatMetaData.seatNumber}
                                                    </c:when>
													<c:otherwise>--</c:otherwise>
												</c:choose></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>

						<div class="ticket-footer">
							<p>Wish you a happy journey • Sam Railways</p>
						</div>
					</div>

				</div>
			</c:if>

		</main>
	</div>

	<div id="toast" class="toast"></div>

</body>
</html>