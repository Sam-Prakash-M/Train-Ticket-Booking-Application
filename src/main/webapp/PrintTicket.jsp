<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="tkt" value="${not empty ticket ? ticket : ConfirmedTicket}" />

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>E-Ticket: ${tkt.pnrNumber}</title>
<link
	href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&family=Inter:wght@400;500;600&display=swap"
	rel="stylesheet">
	<link rel="icon" type="image/png" href="train_logo_all.png">

<style>
/* ================= VARIABLES ================= */
:root {
	--primary: #1a1a1a;
	--secondary: #555;
	--border: #e0e0e0;
	--bg: #f4f6f8;
	--accent: #2563eb;
}

/* ================= RESET ================= */
* {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
}

body {
	background-color: var(--bg);
	font-family: 'Inter', sans-serif;
	color: var(--primary);
	display: flex;
	justify-content: center;
	padding: 40px 0;
}

/* ================= TICKET CONTAINER ================= */
.ticket-container {
	width: 210mm; /* A4 Width */
	min-height: 290mm;
	background: white;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
	position: relative;
	overflow: hidden;
	padding-bottom: 100px;
}

/* ================= HEADER ================= */
.header {
	padding: 30px 40px;
	border-bottom: 2px dashed var(--border);
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.brand-section {
	display: flex;
	align-items: center;
	gap: 15px;
}

.logo {
	width: 50px;
	height: 50px;
	object-fit: contain;
}

.brand-name {
	font-family: 'Poppins', sans-serif;
	font-weight: 700;
	font-size: 22px;
}

.doc-type {
	font-size: 12px;
	color: var(--secondary);
	text-transform: uppercase;
	letter-spacing: 2px;
	margin-top: 2px;
}

.pnr-box {
	text-align: right;
}

.pnr-label {
	font-size: 11px;
	color: var(--secondary);
	font-weight: 600;
	letter-spacing: 1px;
}

.pnr-value {
	font-family: 'Poppins', sans-serif;
	font-size: 28px;
	font-weight: 700;
	letter-spacing: 1px;
}

/* ================= JOURNEY HERO ================= */
.journey-hero {
	padding: 30px 40px;
	background: #fafafa;
	border-bottom: 1px solid var(--border);
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.station-info {
	flex: 1;
}

.station-code {
	font-size: 24px;
	font-weight: 800;
	font-family: 'Poppins', sans-serif;
}

.station-name {
	font-size: 14px;
	color: var(--secondary);
}

.route-visual {
	flex: 2;
	text-align: center;
	position: relative;
	padding: 0 20px;
}

.train-name {
	font-weight: 700;
	font-size: 16px;
	margin-bottom: 5px;
	display: block;
}

.train-number {
	font-size: 13px;
	color: var(--secondary);
	background: #eee;
	padding: 2px 8px;
	border-radius: 4px;
}

.arrow-line {
	margin-top: 10px;
	height: 2px;
	background: var(--border);
	position: relative;
	width: 100%;
}

.arrow-line::after {
	content: '➜';
	position: absolute;
	right: 0;
	top: -8px;
	font-size: 14px;
	color: var(--secondary);
}

.right-align {
	text-align: right;
}

/* ================= META GRID ================= */
.meta-grid {
	display: grid;
	grid-template-columns: repeat(4, 1fr);
	gap: 20px;
	padding: 20px 40px;
	border-bottom: 1px solid var(--border);
}

.meta-item label {
	display: block;
	font-size: 11px;
	color: var(--secondary);
	text-transform: uppercase;
	font-weight: 600;
	margin-bottom: 4px;
}

.meta-item span {
	display: block;
	font-size: 15px;
	font-weight: 600;
}

/* ================= TABLE ================= */
.passenger-section {
	padding: 30px 40px;
}

.sec-title {
	font-size: 14px;
	font-weight: 700;
	text-transform: uppercase;
	margin-bottom: 15px;
	border-left: 4px solid var(--primary);
	padding-left: 10px;
}

.p-table {
	width: 100%;
	border-collapse: collapse;
	margin-bottom: 20px;
}

.p-table th {
	text-align: left;
	font-size: 12px;
	color: var(--secondary);
	text-transform: uppercase;
	padding: 10px 0;
	border-bottom: 2px solid var(--border);
}

.p-table td {
	padding: 12px 0;
	font-size: 14px;
	border-bottom: 1px solid #f0f0f0;
}

.p-table tr:last-child td {
	border-bottom: none;
}

.status-badge {
	font-weight: 700;
	font-size: 13px;
}

.seat-badge {
	font-family: monospace;
	font-size: 15px;
	font-weight: 700;
	background: #f0f0f0;
	padding: 4px 8px;
	border-radius: 4px;
}

/* ================= FOOTER ================= */
.footer-section {
	padding: 20px 40px;
	background: #fafafa;
	border-top: 1px dashed var(--border);
	display: flex;
	justify-content: space-between;
	align-items: center;
	width: 100%;
}

.terms {
	font-size: 11px;
	color: var(--secondary);
	width: 60%;
	line-height: 1.5;
}

.qr-box {
	text-align: right;
}

.qr-img {
	width: 100px;
	height: 100px;
	border: 4px solid white;
	box-shadow: 0 0 0 1px var(--border);
}

/* ================= PRINT ================= */
.print-fab {
	position: fixed;
	bottom: 30px;
	right: 30px;
	background: var(--accent);
	color: white;
	padding: 15px 30px;
	border-radius: 50px;
	font-weight: 600;
	cursor: pointer;
	border: none;
	z-index: 1000;
	box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

@media print {
	body {
		background: white;
		padding: 0;
		display: block;
	}
	.ticket-container {
		box-shadow: none;
		width: 100%;
		min-height: auto;
		border: none;
		padding-bottom: 0;
	}
	.print-fab {
		display: none;
	}
	.header, .journey-hero, .footer-section {
		-webkit-print-color-adjust: exact;
		print-color-adjust: exact;
	}
	@page {
		margin: 0;
		size: A4;
	}
	@media print {
    .footer-section {
        position: static !important;
    }
}
}
</style>
</head>
<body>

	<button class="print-fab" onclick="window.print()">Print
		Ticket</button>

	<div class="ticket-container">

		<header class="header">
			<div class="brand-section">
				<img src="train_logo.png" alt="Logo" class="logo"
					onerror="this.style.display='none'">
				<div>
					<div class="brand-name">Sam Railways</div>
					<div class="doc-type">Electronic Reservation Slip</div>
				</div>
			</div>
			<div class="pnr-box">
				<div class="pnr-label">PNR NUMBER</div>
				<div class="pnr-value">${tkt.pnrNumber}</div>
			</div>
		</header>

		<section class="journey-hero">
			<div class="station-info">
				<div class="station-code">${tkt.sourceArr}</div>
				<div class="station-name">Source</div>
			</div>
			<div class="route-visual">
				<span class="train-name">${tkt.trainName}</span> <span
					class="train-number">${tkt.trainId}</span>
				<div class="arrow-line"></div>
			</div>
			<div class="station-info right-align">
				<div class="station-code">${tkt.destinationArr}</div>
				<div class="station-name">Destination</div>
			</div>
		</section>

		<section class="meta-grid">
			<div class="meta-item">
				<label>Date of Journey</label> <span>${tkt.bookingDate}</span>
			</div>
			<div class="meta-item">
				<label>Class</label> <span>${tkt.className}</span>
			</div>
			<div class="meta-item">
				<label>Total Fare</label> <span>₹ ${tkt.totalFare}</span>
			</div>
			<div class="meta-item">
				<label>Status</label> <span>CONFIRMED</span>
			</div>
		</section>

		<section class="passenger-section">
			<div class="sec-title">Passenger Details</div>

			<table class="p-table">
				<thead>
					<tr>
						<th width="10%">#</th>
						<th width="40%">Name</th>
						<th width="15%">Age / Sex</th>
						<th width="20%">Status</th>
						<th width="15%">Seat</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty tkt.associatedPassenger}">
							<c:forEach var="p" items="${tkt.associatedPassenger}"
								varStatus="loop">
								<tr>
									<td>${loop.index + 1}</td>
									<td><strong>${p.name}</strong></td>
									<td>${p.age}/${p.gender}</td>

									<td><c:choose>
											<c:when test="${p.ticketStatus.startsWith('CNF')}">
												<span style="color: green; font-weight: 700">CNF</span>
											</c:when>
											<c:when test="${p.ticketStatus.startsWith('RAC')}">
												<span style="color: orange; font-weight: 700">RAC</span>
											</c:when>
											<c:when test="${p.ticketStatus.startsWith('WL')}">
												<span style="color: red; font-weight: 700">WL</span>
											</c:when>
											<c:otherwise>${p.ticketStatus}</c:otherwise>
										</c:choose></td>

									<td><c:choose>
											<c:when test="${not empty p.seatMetaData}">
												<span class="seat-badge">${p.seatMetaData.coachNo} -
													${p.seatMetaData.seatNumber}</span>
											</c:when>
											<c:otherwise>--</c:otherwise>
										</c:choose></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="5"
									style="text-align: center; padding: 20px; color: #999; font-style: italic;">No
									passenger details available for this PNR.</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</section>

		<footer class="footer-section">
			<div class="terms">
				<strong>Instructions:</strong><br> 1. Original ID proof
				required.<br> 2. Check departure time.<br> 3. Generated by
				Sam Railways.
			</div>
			<div class="qr-box">
				<img
					src="https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=${tkt.pnrNumber}"
					class="qr-img" alt="QR">
			</div>
		</footer>

	</div>

	<script>
		window.onload = function() {
			setTimeout(function() {
				window.print();
			}, 800);
		};
	</script>

</body>
</html>