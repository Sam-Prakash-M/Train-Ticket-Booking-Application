<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Train Ticket (ERS)</title>

<style>
/* ===== Global Print Styling ===== */
body {
	font-family: "Segoe UI", Arial, sans-serif;
	-webkit-print-color-adjust: exact;
	print-color-adjust: exact;
	background: #f8f9fc;
	margin: 0;
	padding: 25px;
}

.page {
	width: 210mm;
	min-height: 297mm;
	background: #ffffff;
	padding: 22mm;
	margin: auto;
	border-radius: 10px;
	border: 1px solid #d1d9e6;
}

/* ===== Header ===== */
.header-container {
	text-align: center;
	margin-bottom: 15px;
}

.header-title {
	font-size: 22px;
	font-weight: 700;
	color: #1a2b49;
}

.subheader {
	font-size: 14px;
	color: #5f6c85;
	margin-bottom: 20px;
}

.logo-row {
	display: flex;
	justify-content: center;
	gap: 160px;
	margin-bottom: 10px;
}

.logo {
	width: 90px;
	opacity: 0.95;
}

/* ===== Section Cards ===== */
.section-card {
	margin-top: 25px;
	border-radius: 12px;
	border: 1px solid #cfd7e6;
	padding: 12px 15px 15px 15px;
	background: #fdfdfd;
}

.section-title {
	font-weight: 600;
	padding: 6px 0;
	font-size: 15px;
	color: #1d3557;
	border-bottom: 1px solid #d6dce8;
	margin-bottom: 10px;
}

/* ===== Modern Tables ===== */
table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 8px;
}

th {
	background: #eef3fc;
	border: 1px solid #d3d8e6;
	padding: 8px;
	font-size: 13px;
	font-weight: 600;
	color: #2a3350;
}

td {
	border: 1px solid #e4e6eb;
	padding: 7px 8px;
	font-size: 13px;
}

/* Passenger Table Row Highlight */
tbody tr:nth-child(odd) {
	background: #fafbff;
}

/* ===== QR Code ===== */
.qr {
	width: 160px;
	margin-top: 18px;
	float: right;
	border: 1px solid #d0d4df;
	padding: 6px;
	border-radius: 6px;
}

/* Print Background */
@media print {
	body {
		background: white !important;
	}
}
</style>

</head>

<body onload="window.print()">

	<div class="page">

		<!-- LOGOS -->
		<div class="logo-row">
			<img src="train.png" class="logo"> <img src="irctc.png"
				class="logo">
		</div>

		<div class="header-container">
			<div class="header-title">Electronic Reservation Slip (ERS)</div>
			<div class="subheader">Normal User</div>
		</div>

		<!-- JOURNEY DETAILS -->
		<div class="section-card">
			<div class="section-title">Journey Details</div>

			<table>
				<tr>
					<th>PNR</th>
					<td>${ticket.pnrNumber}</td>

					<th>Train</th>
					<td>${ticket.trainName}(${ticket.trainId})</td>
				</tr>

				<tr>
					<th>From</th>
					<td>${ticket.sourceArr}</td>

					<th>To</th>
					<td>${ticket.destinationArr}</td>
				</tr>

				<tr>
					<th>Class</th>
					<td>${ticket.className}</td>

					<th>Booking Date</th>
					<td>${ticket.bookingDate}</td>
				</tr>
			</table>
		</div>

		<!-- PASSENGERS -->
		<div class="section-card">
			<div class="section-title">Passenger Details</div>

			<table>
				<thead>
					<tr>
						<th>#</th>
						<th>Name</th>
						<th>Age</th>
						<th>Gender</th>
						<th>Status</th>
						<th>Coach</th>
						<th>Seat</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach items="${ticket.associatedPassenger}" var="p"
						varStatus="loop">
						<tr>
							<td>${loop.index + 1}</td>
							<td>${p.name}</td>
							<td>${p.age}</td>
							<td>${p.gender}</td>

							<!-- Status -->
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
									<c:otherwise> -- </c:otherwise>
								</c:choose></td>

							<!-- Coach -->
							<td><c:choose>
									<c:when test="${p.ticketStatus.startsWith('CNF')}">
                                ${p.seatMetaData.coachNo()}
                            </c:when>
									<c:otherwise> -- </c:otherwise>
								</c:choose></td>

							<!-- Seat -->
							<td><c:choose>
									<c:when test="${p.ticketStatus.startsWith('CNF')}">
                                ${p.seatMetaData.seatNumber()}
                            </c:when>

									<c:when test="${p.ticketStatus.startsWith('RAC')}">
                                ${p.ticketStatus}/${p.seatMetaData.seatNumber()}
                            </c:when>

									<c:when test="${p.ticketStatus.startsWith('WL')}">
                                ${p.ticketStatus}/${p.seatMetaData.seatNumber()}
                            </c:when>

									<c:otherwise> -- </c:otherwise>
								</c:choose></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<!-- QR -->
		<img src="QR?pnr=${ticket.pnrNumber}" class="qr">

	</div>

</body>
</html>
