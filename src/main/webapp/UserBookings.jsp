<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>My Bookings | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet" href="UserBookings.css?v=32">
<link rel="icon" type="image/png" href="train_logo_all.png">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="UserBookings.js?v=41"></script>
</head>
<body>

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
					Home</a> <a href="#" class="active"><i class="ri-ticket-2-line"></i>
					Bookings</a> <a href="pnrstatus.jsp"><i class="ri-qr-code-line"></i>
					PNR Status</a>
			</div>
			<div class="nav-profile">
				<button id="themeToggle" class="icon-btn">
					<i class="ri-moon-line"></i>
				</button>
				<div class="user-dropdown">
					<button class="user-btn">
						<span class="u-avatar"><%=session.getAttribute("user_name").toString().charAt(0)%></span>
						<span class="u-name">My Account</span> <i
							class="ri-arrow-down-s-line"></i>
					</button>
					<div class="dropdown-content glass">
						<div class="dd-header">
							<strong><%=session.getAttribute("user_name")%></strong> <small>Logged
								In <i class="ri-checkbox-circle-fill success-icon"></i>
							</small>
						</div>
						<div class="divider"></div>
						<a href="ProfileUpdate"><i class="ri-user-line"></i> My Profile</a>
						<a href="TransactionList"><i class="ri-exchange-dollar-line"></i>
							My Transactions</a> <a href="MyBookings" class="active"><i
							class="ri-history-line"></i> Booked Ticket History</a> <a
							href="refunds.jsp"><i class="ri-refund-2-line"></i> Ticket
							Refund History</a>
						<div class="divider"></div>
						<a href="logout" class="danger"><i class="ri-logout-box-line"></i>
							Logout</a>
					</div>
				</div>
			</div>
		</nav>

		<main class="main-content">

			<div class="header-row">
				<h1 class="page-title">My Journeys</h1>
				<div class="tabs glass-pill">
					<button class="tab active" onclick="showTab('upcoming', this)">Upcoming</button>
					<button class="tab" onclick="showTab('past', this)">Completed</button>
				</div>
			</div>

			<div id="upcoming" class="tab-content active">
				<c:choose>
					<c:when test="${not empty upcomingBookings}">
						<c:forEach var="booking" items="${upcomingBookings}">

							<div class="booking-card glass-card" id="card-${booking.pnrNo}"
								onclick="toggleDetails(this)">
								<div class="card-summary">
									<div class="train-info">
										<div class="top-row">
											<h2>${booking.traiName} (${booking.trainId})</h2>
											<span class="pnr-badge">PNR: ${booking.pnrNo}</span>
										</div>
										<div class="route-visual">
											<div class="station-node">
												<span class="code">${booking.source}</span><span class="lbl">Source</span>
											</div>
											<div class="path-line">
												<i class="ri-train-line"></i>
												<div class="dotted-line"></div>
											</div>
											<div class="station-node right">
												<span class="code">${booking.destination}</span><span
													class="lbl">Dest</span>
											</div>
										</div>
										<div class="meta-row">
											<span class="date"><i class="ri-calendar-event-line"></i>
												${booking.travelDate}</span> <span class="class-pill">${booking.classType}</span>
										</div>
									</div>
									<div class="price-section">
										<div class="price">₹${booking.totalFare}</div>
										<div class="expand-btn">
											<i class="ri-arrow-down-s-line"></i>
										</div>
									</div>
								</div>

								<div class="card-details">
									<div class="passengers-list">
										<c:forEach var="p" items="${booking.associatedPassenger}">
											<div class="p-row" data-name="${p.name}" data-age="${p.age}"
												data-gender="${p.gender}" data-status="${p.ticketStatus}"
												data-classType="${booking.classType}"
												data-seat="${p.seatMetaData.coachNo}-${p.seatMetaData.seatNumber}">
												<div class="p-icon">
													<i class="ri-user-3-line"></i>
												</div>
												<div class="p-info">
													<strong>${p.name}</strong><small>${p.age},
														${p.gender}</small>
												</div>
												<div class="p-seat">
													<span class="seat-badge">${p.seatMetaData.coachNo}
														${p.seatMetaData.seatNumber != '0' ? '-' : ''}
														${p.seatMetaData.seatNumber != '0' ? p.seatMetaData.seatNumber : ''}</span>
												</div>
												<div class="p-status">
													<span
														class="status-pill ${p.ticketStatus.startsWith('CNF') ? 'cnf' : (p.ticketStatus.startsWith('RAC') ? 'rac' : (p.ticketStatus.startsWith('CAN') ? 'can' : 'wl'))}">${p.ticketStatus}</span>
												</div>
											</div>
										</c:forEach>
									</div>

									<div class="action-bar">
										<button class="btn-action outline"
											onclick="event.stopPropagation(); window.open('PrintTicket?pnr=${booking.pnrNo}', '_blank')">
											<i class="ri-printer-line"></i> Print
										</button>
										<button class="btn-action secondary"
											onclick="event.stopPropagation(); getPNRStatus('${booking.pnrNo}', this)">
											<i class="ri-refresh-line"></i> Status
										</button>

										<button class="btn-action danger"
											onclick="event.stopPropagation(); openCancelModal('${booking.pnrNo}')">
											<i class="ri-close-circle-line"></i> Cancel Ticket
										</button>
									</div>
								</div>
							</div>

						</c:forEach>
					</c:when>
					<c:otherwise>
						<div class="empty-state">
							<i class="ri-ticket-line big-icon"></i>
							<h3>No upcoming journeys</h3>
							<a href="RailwayApplication.jsp" class="btn-primary">Book a
								Ticket</a>
						</div>
					</c:otherwise>
				</c:choose>
			</div>

			<div id="past" class="tab-content">
				<c:choose>
					<c:when test="${not empty pastBookings}">
						<c:forEach var="booking" items="${pastBookings}">

							<div class="booking-card glass-card" id="card-${booking.pnrNo}"
								onclick="toggleDetails(this)">
								<div class="card-summary">
									<div class="train-info">
										<div class="top-row">
											<h2>${booking.traiName} (${booking.trainId})</h2>
											<span class="pnr-badge">PNR: ${booking.pnrNo}</span>
										</div>
										<div class="route-visual">
											<div class="station-node">
												<span class="code">${booking.source}</span>
											</div>
											<div class="path-line">
												<i class="ri-check-double-line"></i>
												<div class="dotted-line"></div>
											</div>
											<div class="station-node right">
												<span class="code">${booking.destination}</span>
											</div>
										</div>
										<div class="meta-row">
											<span class="date">${booking.travelDate}</span> <span
												class="status-pill cnf">Completed</span>
										</div>
									</div>
									<div class="price-section">
										<div class="price">₹${booking.totalFare}</div>
										<div class="expand-btn">
											<i class="ri-arrow-down-s-line"></i>
										</div>
									</div>
								</div>

								<div class="card-details">
									<div class="passengers-list">
										<c:forEach var="p" items="${booking.associatedPassenger}">
											<div class="p-row">
												<div class="p-info">
													<strong>${p.name}</strong>
												</div>
												<div class="p-status">
													<span class="status-pill cnf">Completed</span>
												</div>
											</div>
										</c:forEach>
									</div>

									<div class="action-bar">
										<button class="btn-action outline"
											onclick="event.stopPropagation(); window.open('PrintTicket?pnr=${booking.pnrNo}', '_blank')">
											<i class="ri-printer-line"></i> Print Receipt
										</button>

										<button class="btn-action primary"
											onclick="event.stopPropagation(); rebookJourney('${booking.source}', '${booking.destination}')">
											<i class="ri-repeat-line"></i> Rebook Journey
										</button>
									</div>
								</div>
							</div>

						</c:forEach>
					</c:when>
					<c:otherwise>
						<div class="empty-state">
							<h3>No travel history found</h3>
						</div>
					</c:otherwise>
				</c:choose>
			</div>

		</main>
	</div>

	<div id="cancelModal" class="modal-overlay hidden">
		<div class="modal-box glass">
			<div class="modal-header">
				<h3>
					<i class="ri-user-unfollow-line"></i> Select Passengers to Cancel
				</h3>
				<button class="close-modal" onclick="closeModal()">
					<i class="ri-close-line"></i>
				</button>
			</div>

			<form action="CancelTicketServlet" method="POST" id="cancelForm">
				<input type="hidden" name="pnr" id="cancelPnrInput">

				<div class="passenger-select-list" id="passengerCheckboxes"></div>

				<div class="modal-warning">
					<i class="ri-error-warning-line"></i>
					<p>Refund will be processed to original source. This action
						cannot be undone.</p>
				</div>

				<div class="modal-actions">
					<button type="button" class="btn-modal cancel"
						onclick="closeModal()">Go Back</button>
					<button type="submit" class="btn-modal confirm">Cancel
						Selected</button>
				</div>
			</form>
		</div>
	</div>

	<div id="toast" class="toast"></div>

</body>
</html>