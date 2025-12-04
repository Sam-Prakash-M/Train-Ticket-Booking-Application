<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<title>Train Ticket (ERS)</title>

<style>
body {
	font-family: Arial, sans-serif;
	-webkit-print-color-adjust: exact;
	print-color-adjust: exact;
	background: white;
	margin: 0;
	padding: 20px;
}

.page {
	width: 210mm;
	min-height: 297mm;
	padding: 20mm;
	border: 1px solid #aaa;
	margin: auto;
}

.header {
	text-align: center;
	font-size: 18px;
	font-weight: bold;
}

.subheader {
	text-align: center;
	margin-bottom: 15px;
	font-size: 14px;
}

.logo-row {
	display: flex;
	justify-content: space-between;
}

.logo {
	width: 120px;
}

.section-title {
	background: #e6f0ff;
	padding: 6px;
	font-weight: bold;
	border: 1px solid #aac3ff;
	margin-top: 15px;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 6px;
}

td, th {
	border: 1px solid #555;
	padding: 6px;
	font-size: 13px;
}

.qr {
	float: right;
	width: 150px;
	margin-top: 10px;
}

@media print {
	body {
		background: white;
	}
}
</style>

</head>
<body onload="window.print()">

	<div class="page">

		<div class="logo-row">
			<img src="train.png" class="logo"> <img src="irctc.png"
				class="logo">
		</div>

		<div class="header">Electronic Reservation Slip (ERS)</div>
		<div class="subheader">Normal User</div>

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
								<c:otherwise>
                    --
                </c:otherwise>
							</c:choose></td>

						<!-- COACH COLUMN -->
						<td><c:choose>
								<c:when test="${p.ticketStatus.startsWith('CNF')}">
                    ${p.seatMetaData.coachNo()}
                </c:when>
								<c:otherwise>
                    --
                </c:otherwise>
							</c:choose></td>

						<!-- SEAT COLUMN -->
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

								<c:otherwise>
                    --
                </c:otherwise>
							</c:choose></td>

					</tr>
				</c:forEach>
			</tbody>

		</table>

		<img src="QR?pnr=${ticket.pnrNumber}" class="qr">

	</div>

</body>
</html>
