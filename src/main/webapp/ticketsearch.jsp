<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Train Booking Portal</title>

<!-- FontAwesome for icons -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

<!-- Flatpickr for modern calendar -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/themes/material_blue.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>

<link rel="stylesheet" href="ticketsearch.css?v=5">
<script defer src="ticketsearch.js?v=5"></script>
</head>
<body>

	<!-- Loader -->
	<div class="loader-wrapper" id="pageLoader">
		<div class="train-track">
			<div class="train-body">🚆</div>
		</div>
		<p class="loader-text">Preparing Tracks...</p>
	</div>

	<!-- Theme toggle -->
	<div class="theme-toggle" id="themeToggle" title="Toggle Dark/Light">🌙</div>

	<!-- App container -->
	<div class="app-wrap">
		<div class="tab-container">
			<button class="tab active">Book Ticket</button>
			<button class="tab">PNR Status</button>
			<button class="tab">Charts/Vacancy</button>
		</div>

		<div class="card">
			<%
			java.time.LocalDate today = java.time.LocalDate.now();
			java.time.LocalDate maxDate = today.plusDays(60);
			String todayFormatted = today.toString();
			String maxDateFormatted = maxDate.toString();
			%>

			<!-- Book Ticket -->
			<section class="search-container active">
				<h1>Book Your Train Ticket</h1>
				<form action="SearchServlet" method="get" class="search-form">
					<div class="form-row route-row">
						<div class="form-group">
							<label for="source">From</label>
							<input type="text" id="source" name="fromStation" placeholder="Enter source station">
						</div>

						<div class="swap-icon" id="swapStations">
							<i class="fa-solid fa-right-left"></i>
						</div>

						<div class="form-group">
							<label for="destination">To</label>
							<input type="text" id="destination" name="toStation" placeholder="Enter destination station">
						</div>
					</div>

					<div class="form-group">
						<label>Date</label>
						<input id="travelDate" type="text" name="travelDate"
							data-min="<%=todayFormatted%>" data-max="<%=maxDateFormatted%>">
					</div>

					<div class="form-group">
						<label>Class</label>
						<select name="trainClass" required>
							<option>All Classes</option>
							<option>AC 3 Tier (3A)</option>
							<option>AC 2 Tier (2A)</option>
							<option>Sleeper (SL)</option>
							<option>Chair Car (CC)</option>
							<option>Second Sitting (2S)</option>
						</select>
					</div>

					<div class="form-group">
						<label>Quota</label>
						<select name="quota" required>
							<option>General</option>
							<option>Ladies</option>
							<option>Senior Citizen</option>
							<option>Person with Disability</option>
							<option>Tatkal</option>
							<option>Premium Tatkal</option>
						</select>
					</div>

					<button type="submit" class="btn">Search Trains</button>
				</form>
			</section>

			<!-- PNR -->
			<section class="search-container">
				<h1>Check PNR Status</h1>
				<form action="PNRServlet" method="get" class="search-form">
					<div class="form-group">
						<label>PNR Number</label>
						<input type="text" name="pnr" maxlength="10" placeholder="Enter 10-digit PNR" required>
					</div>
					<button type="submit" class="btn">Check Status</button>
				</form>
			</section>

			<!-- Chart -->
			<section class="search-container">
				<h1>Chart / Vacancy</h1>
				<form action="ChartServlet" method="get" class="search-form">
					<div class="form-group">
						<label>Train Number</label>
						<input type="text" name="trainNo" placeholder="Enter Train Number" required>
					</div>
					<div class="form-group">
						<label>Date</label>
						<input id="chartDate" type="text" name="chartDate"
							data-min="<%=todayFormatted%>" data-max="<%=maxDateFormatted%>">
					</div>
					<button type="submit" class="btn">Check Chart</button>
				</form>
			</section>

		</div>
	</div>
</body>
</html>
