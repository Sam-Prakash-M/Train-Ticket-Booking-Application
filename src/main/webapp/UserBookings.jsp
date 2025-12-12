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
<link rel="stylesheet" href="UserBookings.css?v=2025_PNR">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="UserBookings.js?v=2025_PNR"></script>
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
						<a href="profile.jsp"><i class="ri-user-line"></i> My Profile</a>
						<a href="transactions.jsp"><i class="ri-exchange-dollar-line"></i>
							My Transactions</a> <a href="ticket_history.jsp" class="active"><i
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

			<h1 class="page-title">My Journey History</h1>

			<div class="tabs-wrapper">
				<div class="tabs glass">
					<button class="tab active" onclick="showTab('all', this)">All</button>
					<button class="tab" onclick="showTab('upcoming', this)">Upcoming</button>
					<button class="tab" onclick="showTab('past', this)">Completed</button>
				</div>
			</div>

			<div id="all" class="tab-content active">
				<c:choose>
					<c:when test="${not empty allBookings}">
						<c:forEach var="booking" items="${allBookings}">
							<div class="booking-card glass" id="card-${booking.pnrNo}"
								onclick="toggleDetails(this)">
								<div class="card-summary">
									<div class="train-info">
										<h2>${booking.traiName}</h2>
										<div class="route-row">
											<span class="station">${booking.source}</span> <i
												class="ri-arrow-right-line arrow"></i> <span class="station">${booking.destination}</span>
										</div>
										<div class="meta-row">
											<span class="date"><i class="ri-calendar-line"></i>
												${booking.travelDate}</span> <span class="class-badge">${booking.classType}</span>
										</div>
									</div>
									<div class="ticket-info">
										<div class="pnr-box">
											<small>PNR</small> <strong>${booking.pnrNo}</strong>
										</div>
										<div class="price-box">₹${booking.totalFare}</div>
										<i class="ri-arrow-down-s-line expand-icon"></i>
									</div>
								</div>

								<div class="card-details">
									<h3>Passenger List</h3>
									<div class="table-responsive">
										<table class="passenger-table" id="table-${booking.pnrNo}">
											<thead>
												<tr>
													<th>#</th>
													<th>Name</th>
													<th>Age/Sex</th>
													<th>Coach/Seat</th>
													<th>Status</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="p" items="${booking.associatedPassenger}"
													varStatus="loop">
													<tr data-pname="${p.name}">
														<td>${loop.index + 1}</td>
														<td><strong>${p.name}</strong></td>
														<td>${p.age}/ ${p.gender}</td>
														<td>${p.seatMetaData.coachNo}-
															${p.seatMetaData.seatNumber}</td>
														<td><span
															class="status-badge ${p.ticketStatus.startsWith('CNF') ? 'cnf' : (p.ticketStatus.startsWith('RAC') ? 'rac' : 'wl')}">
																${p.ticketStatus} </span></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
									<div class="card-actions">
										<button class="btn small secondary"
											onclick="event.stopPropagation(); getPNRStatus('${booking.pnrNo}', this)">
											<i class="ri-refresh-line"></i> Get PNR Status
										</button>

										<button class="btn small outline"
											onclick="event.stopPropagation(); window.open('PrintTicket?pnr=${booking.pnrNo}', '_blank')">
											<i class="ri-printer-line"></i> Print Ticket
										</button>
									</div>
								</div>
							</div>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<div class="empty-state glass">
							<i class="ri-ticket-line"></i>
							<h3>No bookings found</h3>
							<p>You haven't booked any tickets yet.</p>
							<a href="RailwayApplication.jsp" class="btn primary">Book Now</a>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
				<!-- ================= PAST BOOKINGS ================= -->
			<div id="past" class="tab-content">
				<c:choose>
					<c:when test="${not empty pastBookings}">
						<c:forEach var="booking" items="${pastBookings}">
							<div class="booking-card glass" id="card-${booking.pnrNo}"
								onclick="toggleDetails(this)">
								<div class="card-summary">
									<div class="train-info">
										<h2>${booking.traiName}</h2>
										<div class="route-row">
											<span class="station">${booking.source}</span> <i
												class="ri-arrow-right-line arrow"></i> <span class="station">${booking.destination}</span>
										</div>
										<div class="meta-row">
											<span class="date"><i class="ri-calendar-line"></i>
												${booking.travelDate}</span> <span class="class-badge">${booking.classType}</span>
										</div>
									</div>
									<div class="ticket-info">
										<div class="pnr-box">
											<small>PNR</small> <strong>${booking.pnrNo}</strong>
										</div>
										<div class="price-box">₹${booking.totalFare}</div>
										<i class="ri-arrow-down-s-line expand-icon"></i>
									</div>
								</div>

								<div class="card-details">
									<h3>Passenger List</h3>
									<div class="table-responsive">
										<table class="passenger-table" id="table-${booking.pnrNo}">
											<thead>
												<tr>
													<th>#</th>
													<th>Name</th>
													<th>Age/Sex</th>
													<th>Coach/Seat</th>
													<th>Status</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="p" items="${booking.associatedPassenger}"
													varStatus="loop">
													<tr data-pname="${p.name}">
														<td>${loop.index + 1}</td>
														<td><strong>${p.name}</strong></td>
														<td>${p.age}/ ${p.gender}</td>
														<td>${p.seatMetaData.coachNo}-
															${p.seatMetaData.seatNumber}</td>
														<td><span
															class="status-badge ${p.ticketStatus.startsWith('CNF') ? 'cnf' : (p.ticketStatus.startsWith('RAC') ? 'rac' : 'wl')}">
																${p.ticketStatus} </span></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
									<div class="card-actions">
										<button class="btn small secondary"
											onclick="event.stopPropagation(); getPNRStatus('${booking.pnrNo}', this)">
											<i class="ri-refresh-line"></i> Get PNR Status
										</button>

										<button class="btn small outline"
											onclick="event.stopPropagation(); window.open('PrintTicket?pnr=${booking.pnrNo}', '_blank')">
											<i class="ri-printer-line"></i> Print Ticket
										</button>
									</div>
								</div>
							</div>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<div class="empty-state glass">
							<i class="ri-ticket-line"></i>
							<h3>No bookings found</h3>
							<p>You haven't booked any tickets yet.</p>
							<a href="RailwayApplication.jsp" class="btn primary">Book Now</a>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
				<!-- ================= UPCOMING BOOKINGS ================= -->
			<div id="upcoming" class="tab-content">
				<c:choose>
					<c:when test="${not empty upcomingBookings}">
						<c:forEach var="booking" items="${upcomingBookings}">
							<div class="booking-card glass" id="card-${booking.pnrNo}"
								onclick="toggleDetails(this)">
								<div class="card-summary">
									<div class="train-info">
										<h2>${booking.traiName}</h2>
										<div class="route-row">
											<span class="station">${booking.source}</span> <i
												class="ri-arrow-right-line arrow"></i> <span class="station">${booking.destination}</span>
										</div>
										<div class="meta-row">
											<span class="date"><i class="ri-calendar-line"></i>
												${booking.travelDate}</span> <span class="class-badge">${booking.classType}</span>
										</div>
									</div>
									<div class="ticket-info">
										<div class="pnr-box">
											<small>PNR</small> <strong>${booking.pnrNo}</strong>
										</div>
										<div class="price-box">₹${booking.totalFare}</div>
										<i class="ri-arrow-down-s-line expand-icon"></i>
									</div>
								</div>

								<div class="card-details">
									<h3>Passenger List</h3>
									<div class="table-responsive">
										<table class="passenger-table" id="table-${booking.pnrNo}">
											<thead>
												<tr>
													<th>#</th>
													<th>Name</th>
													<th>Age/Sex</th>
													<th>Coach/Seat</th>
													<th>Status</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="p" items="${booking.associatedPassenger}"
													varStatus="loop">
													<tr data-pname="${p.name}">
														<td>${loop.index + 1}</td>
														<td><strong>${p.name}</strong></td>
														<td>${p.age}/ ${p.gender}</td>
														<td>${p.seatMetaData.coachNo}-
															${p.seatMetaData.seatNumber}</td>
														<td><span
															class="status-badge ${p.ticketStatus.startsWith('CNF') ? 'cnf' : (p.ticketStatus.startsWith('RAC') ? 'rac' : 'wl')}">
																${p.ticketStatus} </span></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
									<div class="card-actions">
										<button class="btn small secondary"
											onclick="event.stopPropagation(); getPNRStatus('${booking.pnrNo}', this)">
											<i class="ri-refresh-line"></i> Get PNR Status
										</button>

										<button class="btn small outline"
											onclick="event.stopPropagation(); window.open('PrintTicket?pnr=${booking.pnrNo}', '_blank')">
											<i class="ri-printer-line"></i> Print Ticket
										</button>
									</div>
								</div>
							</div>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<div class="empty-state glass">
							<i class="ri-ticket-line"></i>
							<h3>No bookings found</h3>
							<p>You haven't booked any tickets yet.</p>
							<a href="RailwayApplication.jsp" class="btn primary">Book Now</a>
						</div>
					</c:otherwise>
				</c:choose>
			</div>

		</main>
	</div>

	<div id="toast" class="toast"></div>

</body>
</html>