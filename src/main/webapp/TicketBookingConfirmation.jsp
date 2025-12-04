
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Ticket Confirmation</title>
<link rel="stylesheet" href="TicketBookingConfirmation.css?v=5">
</head>
<body>
	<div class="container">
		<!-- Error Card -->
		<c:if test="${not empty errorMessage}">
			<div class="card error-card" role="alert">
				<div class="card-body">
					<h2>Booking Failed</h2>
					<p>${errorMessage}</p>
					<div class="actions">
						<a href="BookingPage.jsp" class="btn">Try Again</a> <a
							href="index.jsp" class="btn btn-ghost">Home</a>
					</div>
				</div>
			</div>
		</c:if>

		<!-- Success Card -->
		<c:if test="${not empty ConfirmedTicket}">
			<div class="card success-card">
				<div class="card-header">
					<div>
						<h1>Booking Confirmed</h1>
						<p class="muted">Your ticket has been successfully booked.</p>
					</div>
					<div class="actions">
						<button onclick="window.open('PrintTicket', '_blank')"
							id="printBtn" class="btn">Print Ticket</button>
						<button id="copyPnrBtn" class="btn btn-outline">Copy PNR</button>
					</div>
				</div>

				<div class="card-body">
					<section class="ticket-meta">
						<div class="meta">
							<label>PNR</label>
							<div id="pnrValue">${ConfirmedTicket.pnrNumber}</div>
						</div>
						<div class="meta">
							<label>Transaction ID</label>
							<div>${ConfirmedTicket.transactionId}</div>
						</div>
						<div class="meta">
							<label>Train</label>
							<div>${ConfirmedTicket.trainName}
								(${ConfirmedTicket.trainId})</div>
						</div>
						<div class="meta">
							<label>Class</label>
							<div>${ConfirmedTicket.className}</div>
						</div>
					</section>

					<section class="route">
						<div>
							<strong>From</strong>
							<div>${ConfirmedTicket.sourceArr}</div>
						</div>
						<div class="arrow">→</div>
						<div>
							<strong>To</strong>
							<div>${ConfirmedTicket.destinationArr}</div>
						</div>
					</section>

					<section class="passengers">
						<h3>Passengers</h3>
						<table>
							<thead>
								<tr>
									<th>#</th>
									<th>Name</th>
									<th>Preference</th>
									<th>Age</th>
									<th>Gender</th>
									<th>Status</th>
									<th>Coach</th>
									<th>Seat</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="p"
									items="${ConfirmedTicket.associatedPassenger}" varStatus="loop">
									<tr>
										<td>${loop.index + 1}</td>
										<td>${p.name}</td>
										<td>${p.preference}</td>
										<td>${p.age}</td>
										<td>${p.gender}</td>

										<!-- STATUS COLUMN -->
										<td><c:choose>
												<c:when test="${p.ticketStatus.startsWith('CNF')}">
                                                    CNF (${p.preference})
                                                </c:when>
												<c:when test="${p.ticketStatus.startsWith('RAC')}">
                                                    RAC (${p.ticketStatus})
                                                </c:when>
												<c:when test="${p.ticketStatus.startsWith('WL')}">
                                                    WL (${p.ticketStatus})
                                                </c:when>
												<c:otherwise>--</c:otherwise>
											</c:choose></td>

										<!-- COACH COLUMN -->
										<td><c:choose>
												<c:when test="${p.ticketStatus.startsWith('CNF')}">
                                                    ${p.seatMetaData.coachNo}
                                                </c:when>
												<c:otherwise>--</c:otherwise>
											</c:choose></td>

										<!-- SEAT COLUMN -->
										<td><c:choose>
												<c:when test="${p.ticketStatus.startsWith('CNF')}">
                                                     ${p.ticketStatus}/${p.seatMetaData.seatNumber}
                                                </c:when>
												<c:when test="${p.ticketStatus.startsWith('RAC')}">
                                                    ${p.ticketStatus}/${p.seatMetaData.seatNumber}
                                                </c:when>
												<c:when test="${p.ticketStatus.startsWith('WL')}">
                                                    ${p.ticketStatus}/${p.seatMetaData.seatNumber}
                                                </c:when>
												<c:otherwise>--</c:otherwise>
											</c:choose></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</section>

					<div class="cta-row">
						<a href="MyBookings.jsp" class="btn btn-primary">View My
							Bookings</a> <a href="index.jsp" class="btn btn-ghost">Back to
							Home</a>
					</div>
				</div>
			</div>
		</c:if>
	</div>

	<script src="TicketBookingConfirmation.js?v=4"></script>
</body>
</html>

