<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<title>My Bookings</title>
<link rel="stylesheet" href="UserBookings.css?v=2">
<script defer src="UserBookings.js?v=2"></script>
</head>
<body>

	<div class="app-container">

		<h1 class="page-title">🚆 My Bookings</h1>

		<!-- Tabs -->
		<div class="tabs">
			<button class="tab active" onclick="showTab('all', this)">All</button>
			<button class="tab" onclick="showTab('upcoming', this)">Upcoming</button>
			<button class="tab" onclick="showTab('past', this)">Past</button>
		</div>

		<!-- ================= ALL BOOKINGS ================= -->
		<div id="all" class="tab-content active">
			<c:forEach var="booking" items="${allBookings}">
				<div class="booking-card" onclick="toggleDetails(this)">
					<div class="booking-summary">
						<div>
							<h2>${booking.traiName}</h2>
							<p>${booking.source}→ ${booking.destination}</p>
							<p>${booking.travelDate}| ${booking.classType}</p>
						</div>
						<div class="right">
							<span class="pnr">${booking.pnrNo}</span> <span class="price">₹${booking.totalFare}</span>
						</div>
					</div>

					<div class="booking-details">
						<h3>Passenger Details</h3>

						<table class="passenger-table">
							<thead>
								<tr>
									<th>#</th>
									<th>Passenger Name</th>
									<th>Age</th>
									<th>Gender</th>
									<th>Coach</th>
									<th>Seat No</th>
									<th>Status</th>
									<th>Current Status</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach var="p" items="${booking.associatedPassenger}"
									varStatus="loop">

									<tr>
										<td>${loop.index + 1}</td>
										<td>${p.name}</td>
										<td>${p.age}</td>
										<td>${p.gender}</td>
										<td>${p.seatMetaData.coachNo}</td>
										<td>${p.seatMetaData.seatNumber}</td>

										<!-- STATUS COLUMN -->
										<td><c:choose>
												<c:when test="${p.ticketStatus.startsWith('CNF')}">
													<span class="tag tag-cnf">CNF</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('RAC')}">
													<span class="tag tag-rac">RAC</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('WL')}">
													<span class="tag tag-wl">WL</span>
												</c:when>
											</c:choose></td>

										<!-- CURRENT STATUS (Yes, IRCTC shows both) -->
										<td><c:choose>
												<c:when test="${p.ticketStatus.startsWith('CNF')}">
													<span class="tag tag-cnf">${p.ticketStatus}</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('RAC')}">
													<span class="tag tag-rac">${p.ticketStatus}</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('WL')}">
													<span class="tag tag-wl">${p.ticketStatus}</span>
												</c:when>
											</c:choose></td>
									</tr>

								</c:forEach>
							</tbody>
						</table>
					</div>

				</div>
			</c:forEach>
		</div>

		<!-- ================= UPCOMING BOOKINGS ================= -->
		<div id="upcoming" class="tab-content">
			<c:forEach var="booking" items="${upcomingBookings}">
				<div class="booking-card" onclick="toggleDetails(this)">
					<div class="booking-summary">
						<div>
							<h2>${booking.traiName}</h2>
							<p>${booking.source}→ ${booking.destination}</p>
							<p>${booking.travelDate}| ${booking.classType}</p>
						</div>
						<div class="right">
							<span class="pnr">${booking.pnrNo}</span> <span class="price">₹${booking.totalFare}</span>
						</div>
					</div>

					<div class="booking-details">
						<h3>Passenger Details</h3>

						<table class="passenger-table">
							<thead>
								<tr>
									<th>#</th>
									<th>Passenger Name</th>
									<th>Age</th>
									<th>Gender</th>
									<th>Coach</th>
									<th>Seat No</th>
									<th>Status</th>
									<th>Current Status</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach var="p" items="${booking.associatedPassenger}"
									varStatus="loop">

									<tr>
										<td>${loop.index + 1}</td>
										<td>${p.name}</td>
										<td>${p.age}</td>
										<td>${p.gender}</td>
										<td>${p.seatMetaData.coachNo}</td>
										<td>${p.seatMetaData.seatNumber}</td>

										<!-- STATUS COLUMN -->
										<td><c:choose>
												<c:when test="${p.ticketStatus.startsWith('CNF')}">
													<span class="tag tag-cnf">CNF</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('RAC')}">
													<span class="tag tag-rac">RAC</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('WL')}">
													<span class="tag tag-wl">WL</span>
												</c:when>
											</c:choose></td>

										<!-- CURRENT STATUS (Yes, IRCTC shows both) -->
										<td><c:choose>
												<c:when test="${p.ticketStatus.startsWith('CNF')}">
													<span class="tag tag-cnf">${p.ticketStatus}</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('RAC')}">
													<span class="tag tag-rac">${p.ticketStatus}</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('WL')}">
													<span class="tag tag-wl">${p.ticketStatus}</span>
												</c:when>
											</c:choose></td>
									</tr>

								</c:forEach>
							</tbody>
						</table>
					</div>

				</div>
			</c:forEach>
		</div>

		<!-- ================= PAST BOOKINGS ================= -->
		<div id="past" class="tab-content">
			<c:forEach var="booking" items="${pastBookings}">
				<div class="booking-card" onclick="toggleDetails(this)">
					<div class="booking-summary">
						<div>
							<h2>${booking.traiName}</h2>
							<p>${booking.source}→ ${booking.destination}</p>
							<p>${booking.travelDate}| ${booking.classType}</p>
						</div>
						<div class="right">
							<span class="pnr">${booking.pnrNo}</span> <span class="price">₹${booking.totalFare}</span>
						</div>
					</div>

					<div class="booking-details">
						<h3>Passenger Details</h3>

						<table class="passenger-table">
							<thead>
								<tr>
									<th>#</th>
									<th>Passenger Name</th>
									<th>Age</th>
									<th>Gender</th>
									<th>Coach</th>
									<th>Seat No</th>
									<th>Status</th>
									<th>Current Status</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach var="p" items="${booking.associatedPassenger}"
									varStatus="loop">

									<tr>
										<td>${loop.index + 1}</td>
										<td>${p.name}</td>
										<td>${p.age}</td>
										<td>${p.gender}</td>
										<td>${p.seatMetaData.coachNo}</td>
										<td>${p.seatMetaData.seatNumber}</td>

										<!-- STATUS COLUMN -->
										<td><c:choose>
												<c:when test="${p.ticketStatus.startsWith('CNF')}">
													<span class="tag tag-cnf">CNF</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('RAC')}">
													<span class="tag tag-rac">RAC</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('WL')}">
													<span class="tag tag-wl">WL</span>
												</c:when>
											</c:choose></td>

										<!-- CURRENT STATUS (Yes, IRCTC shows both) -->
										<td><c:choose>
												<c:when test="${p.ticketStatus.startsWith('CNF')}">
													<span class="tag tag-cnf">${p.ticketStatus}</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('RAC')}">
													<span class="tag tag-rac">${p.ticketStatus}</span>
												</c:when>
												<c:when test="${p.ticketStatus.startsWith('WL')}">
													<span class="tag tag-wl">${p.ticketStatus}</span>
												</c:when>
											</c:choose></td>
									</tr>

								</c:forEach>
							</tbody>
						</table>
					</div>

				</div>
			</c:forEach>
		</div>

	</div>

</body>
</html>
