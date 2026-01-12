<%@ page import="org.json.*,java.util.*,jakarta.servlet.*"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page session="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Available Trains | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />

<link rel="stylesheet" href="booking.css?v=2025_2">
<link rel="icon" type="image/png" href="train_logo_all.png">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="booking.js?v=2025_2"></script>
</head>
<body>

	<%
	HttpSession currentSession = request.getSession(false);
	if (currentSession == null || currentSession.getAttribute("user_name") == null) {
		request.setAttribute("message", "Please log in to continue.");
		RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
		rd.forward(request, response);
		return;
	}
	String userName = (String) currentSession.getAttribute("user_name");

	// Logic: Data Retrieval (Kept existing logic)
	JSONArray matchedTrains = (JSONArray) request.getAttribute("MatchedTrainList");
	Map<String, String> trainData = (Map<String, String>) request.getAttribute("TrainData");
	JSONObject seatAvailability = (JSONObject) request.getAttribute("TrainSeatAvailability");
	String source = (String) request.getAttribute("SourceStation");
	String destination = (String) request.getAttribute("DestinationStation");
	java.time.LocalDate today = java.time.LocalDate.now();
	java.time.LocalDate maxDate = today.plusDays(60);
	String todayFormatted = today.toString();
	String maxDateFormatted = maxDate.toString();
	String travelDate = request.getParameter("travelDate");
	%>

	<div id="pageLoader" class="loader-overlay">
		<div class="loader-content">
			<div class="train-icon">
				<i class="ri-train-line"></i>
			</div>
			<div class="loader-bar"></div>
			<p>Fetching Schedules...</p>
		</div>
	</div>

	<div class="ambient-light"></div>

	<div class="app-container">

		<nav class="navbar glass">
			<div class="nav-brand">
				<div class="logo-icon">
					<i class="ri-train-fill"></i>
				</div>
				<div class="brand-info">
					<span class="brand-name">Sam Railways</span>
				</div>
			</div>

			<div class="nav-links">
				<a href="RailwayApplication.jsp"><i class="ri-home-5-line"></i>
					Home</a> <a href="#" class="active"><i class="ri-train-line"></i>
					Trains</a> <a href="pnrstatus.jsp"><i class="ri-qr-code-line"></i>
					PNR Status</a> <a href="charts.jsp"><i
					class="ri-bar-chart-horizontal-line"></i> Charts</a>
			</div>

			<div class="nav-actions">
				<button id="themeToggle" class="btn-icon" aria-label="Toggle Theme">
					<i class="ri-moon-clear-line"></i>
				</button>

				<div class="nav-item dropdown">
					<button class="dropdown-trigger">
						<i class="ri-user-3-line"></i> <span class="account-text">My
							Account</span> <i class="ri-arrow-down-s-line arrow-icon"></i>
					</button>

					<div class="dropdown-menu glass">
						<div class="menu-header">
							<div class="user-avatar"><%=userName.toString().toUpperCase().charAt(0)%></div>
							<div class="user-details">
								<span class="u-name"><%=userName%></span> <span class="u-status">Logged
									In <i class="ri-checkbox-circle-fill"></i>
								</span>
							</div>
						</div>

						<div class="menu-divider"></div>

						<a href="ProfileUpdate" class="menu-item"> <i
							class="ri-profile-line"></i> My Profile
						</a> <a href="TransactionList" class="menu-item"> <i
							class="ri-exchange-dollar-line"></i> My Transactions
						</a> <a href="MyBookings" class="menu-item"> <i
							class="ri-history-line"></i> Booked Ticket History
						</a> <a href="refunds.jsp" class="menu-item"> <i
							class="ri-refund-2-line"></i> Ticket Refund History
						</a>

						<div class="menu-divider"></div>

						<a href="logout" class="menu-item danger"> <i
							class="ri-logout-box-r-line"></i> Logout
						</a>
					</div>
				</div>
			</div>
		</nav>

		<main class="main-content">

			<div class="modify-container glass">
				<form id="modifySearchForm" action="SearchServlet" method="get"
					class="modify-form">
					<div class="route-group">
						<div class="input-mini">
							<label>From</label> <input type="text" name="fromStation"
								id="searchFrom" value="<%=source%>">
						</div>
						<button type="button" id="swapBtn" class="swap-mini">
							<i class="ri-arrow-left-right-line"></i>
						</button>
						<div class="input-mini">
							<label>To</label> <input type="text" name="toStation"
								id="searchTo" value="<%=destination%>">
						</div>
					</div>
					<div class="input-mini">
						<label>Date</label> <input type="date" name="travelDate"
							id="searchDate" value="<%=travelDate%>" min="<%=todayFormatted%>"
							max="<%=maxDateFormatted%>">
					</div>
					<div class="input-mini">
						<label>Class</label> <select name="trainClass" id="searchClass">
							<option>All Classes</option>
							<option>AC 3 Tier (3A)</option>
							<option>AC 2 Tier (2A)</option>
							<option>Sleeper (SL)</option>
							<option>Chair Car (CC)</option>
							<option>Second Sitting (2S)</option>
						</select>
					</div>
					<div class="input-mini">
						<label>Quota</label> <select name="quota">
							<option>General</option>
							<option>Ladies</option>
							<option>Tatkal</option>
							<option>Senior Citizen</option>
						</select>
					</div>
					<button type="submit" class="btn btn-primary btn-search">Modify</button>
				</form>
			</div>

			<div class="date-nav-wrapper">
				<div class="date-nav">
					<button id="prevDay" class="nav-btn">
						<i class="ri-arrow-left-s-line"></i> Prev Day
					</button>
					<button id="nextDay" class="nav-btn">
						Next Day <i class="ri-arrow-right-s-line"></i>
					</button>
				</div>
			</div>

			<h1 class="page-title">Available Trains</h1>

			<div class="train-list">
				<%
				if (matchedTrains != null && matchedTrains.length() > 0) {
					for (int i = 0; i < matchedTrains.length(); i++) {
						JSONObject train = matchedTrains.getJSONObject(i);
						String trainId = train.getString("train_id");
						String trainName = trainData.get(trainId);
						JSONObject farePerKm = train.getJSONObject("fare_per_km");
						JSONArray routes = train.getJSONArray("routes");

						double srcDist = 0, destDist = 0;
						String depTime = "--:--", arrTime = "--:--";
						for (int j = 0; j < routes.length(); j++) {
					JSONObject stop = routes.getJSONObject(j);
					if (stop.getString("station").equalsIgnoreCase(source)) {
						srcDist = stop.getDouble("distance_from_start");
						depTime = stop.getString("departure");
					}
					if (stop.getString("station").equalsIgnoreCase(destination)) {
						destDist = stop.getDouble("distance_from_start");
						arrTime = stop.getString("arrival");
					}
						}
						double totalDistance = Math.abs(destDist - srcDist);

						JSONArray availableDays = train.getJSONArray("available_days");
						List<String> availableList = new ArrayList<>();
						for (int k = 0; k < availableDays.length(); k++)
					availableList.add(availableDays.getString(k));
				%>
				<section class="train-card" data-train-id="<%=trainId%>"
					data-train-name="<%=trainName%>"
					data-routes='<%=routes.toString().replace("'", "\\'")%>'>
					<div class="card-top">
						<div class="train-info">
							<div class="train-header-row">
								<h2><%=trainName%></h2>
								<span class="train-number">#<%=trainId%></span>
							</div>
							<div class="train-times">
								<span class="time"><%=depTime%></span> <span class="arrow"><i
									class="ri-arrow-right-line"></i></span> <span class="time"><%=arrTime%></span>
								<span class="duration"><%=(int) totalDistance%> km</span>
							</div>
						</div>
						<button class="btn-outline schedule-btn" data-train="<%=trainId%>">
							<i class="ri-map-2-line"></i> Schedule
						</button>
					</div>

					<div class="days-row">
						<span>Runs On:</span>
						<div class="day-pills">
							<%
							String[] shortDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
							String[] fullDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
							for (int d = 0; d < 7; d++) {
								boolean available = availableList.contains(fullDays[d]);
							%>
							<span class="day-badge <%=available ? "active" : ""%>"><%=shortDays[d].charAt(0)%></span>
							<%
							}
							%>
						</div>
					</div>

					<div class="coach-grid">
						<%
						Iterator<String> it = farePerKm.keys();
						while (it.hasNext()) {
							String coach = it.next();
							if (!seatAvailability.has(trainId) || !seatAvailability.getJSONObject(trainId).has(coach))
								continue;
						%>
						<button class="coach-btn" data-train="<%=trainId%>"
							data-class="<%=coach%>" data-distance="<%=totalDistance%>">
							<span class="coach-name"><%=coach%></span> <span class="tap-text">Check</span>
						</button>
						<%
						}
						%>
					</div>

					<div class="details-panel hidden" id="details-<%=trainId%>">
						<div class="details-content"></div>
						<div class="action-row">
							<button class="btn btn-primary book-now"
								data-train="<%=trainId%>" disabled>Book Now</button>
						</div>
					</div>
				</section>
				<%
				}
				} else {
				%>
				<div class="no-results glass">
					<i class="ri-train-line"></i>
					<h3>No trains found</h3>
					<p>Try changing dates or stations.</p>
				</div>
				<%
				}
				%>
			</div>
		</main>
	</div>

	<div id="scheduleModal" class="modal-overlay hidden">
		<div class="modal-card">
			<div class="modal-header">
				<h2 id="scheduleTrainName">Schedule</h2>
				<button class="close-btn">
					<i class="ri-close-line"></i>
				</button>
			</div>
			<div class="modal-body">
				<table class="schedule-table">
					<thead>
						<tr>
							<th>Station</th>
							<th>Arr</th>
							<th>Dep</th>
							<th>Km</th>
						</tr>
					</thead>
					<tbody id="scheduleTableBody"></tbody>
				</table>
			</div>
		</div>
	</div>

	<script>
		const seatAvailabilityData = JSON
				.parse(
	<%=JSONObject.quote(seatAvailability.toString())%>
		);
		const fareMap = JSON
				.parse(
	<%JSONObject safeFare = new JSONObject();
if (matchedTrains != null) {
	for (int i = 0; i < matchedTrains.length(); i++) {
		JSONObject t = matchedTrains.getJSONObject(i);
		safeFare.put(t.getString("train_id"), t.getJSONObject("fare_per_km"));
	}
}
out.print(JSONObject.quote(safeFare.toString()));%>
		);
	</script>
</body>
</html>