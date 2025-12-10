<%@ page import="org.json.*,java.util.*,jakarta.servlet.*"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page session="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Available Trains</title>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<link rel="stylesheet" href="booking.css?v=4">
<script defer src="booking.js?v=6"></script>
</head>
<body>
	<%
	HttpSession currentSession = request.getSession(false); // Do NOT create session
	System.out.println("Current Session " + currentSession);

	if (currentSession == null) {
		// User directly accessed the page (no session created)
		request.setAttribute("message", "Please log in to continue.");
		RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
		rd.forward(request, response);
		return;
	}

	String userName = (String) currentSession.getAttribute("user_name");

	System.out.println("Current UserName : " + userName);
	if (userName == null) {
		// Session existed earlier but expired
		request.setAttribute("message", "Your session expired. Please log in again.");
		RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
		rd.forward(request, response);
		return;
	}
	%>


	<%
	JSONArray matchedTrains = (JSONArray) request.getAttribute("MatchedTrainList");
	Map<String, String> trainData = (Map<String, String>) request.getAttribute("TrainData");
	JSONObject seatAvailability = (JSONObject) request.getAttribute("TrainSeatAvailability");
	String source = (String) request.getAttribute("SourceStation");
	String destination = (String) request.getAttribute("DestinationStation");
	java.time.LocalDate today = java.time.LocalDate.now();
	java.time.LocalDate maxDate = today.plusDays(60);
	String todayFormatted = today.toString();
	String maxDateFormatted = maxDate.toString();
	%>

	<div class="container">
		<div class="search-header">
			<form id="modifySearchForm" action="SearchServlet" method="get"
				class="modify-form">

				<div class="route-row">
					<div class="field-group">
						<label>From</label> <input type="text" name="fromStation"
							id="searchFrom" value="<%=source%>">
					</div>

					<div class="swap-icon-horizontal" id="swapBtn">
						<i class="fa-solid fa-right-left"></i>
					</div>

					<div class="field-group">
						<label>To</label> <input type="text" name="toStation"
							id="searchTo" value="<%=destination%>">
					</div>
				</div>


				<div class="field-group">
					<label>Date</label> <input type="date" name="travelDate"
						id="searchDate" value="<%=request.getParameter("travelDate")%>"
						min="<%=todayFormatted%>" max="<%=maxDateFormatted%>">

				</div>

				<div class="field-group">
					<label>Class</label> <select name="trainClass" id="searchClass">
						<option>All Classes</option>
						<option>AC 3 Tier (3A)</option>
						<option>AC 2 Tier (2A)</option>
						<option>Sleeper (SL)</option>
						<option>Chair Car (CC)</option>
						<option>Second Sitting (2S)</option>
					</select>
				</div>

				<div class="field-group">
					<label>Quota</label> <select name="quota">
						<option>General</option>
						<option>Ladies</option>
						<option>Tatkal</option>
						<option>Premium Tatkal</option>
						<option>Senior Citizen</option>
						<option>Person with Disability</option>
					</select>
				</div>

				<button type="submit" class="modify-btn">Modify Search</button>

			</form>

			<div class="date-nav">
				<button id="prevDay" class="nav-btn">⬅ Previous Day</button>
				<button id="nextDay" class="nav-btn">Next Day ➡</button>
			</div>
		</div>

	</div>


	<!-- Loader -->
	<div id="pageLoader" class="loader-overlay hidden">
		<div class="spinner"></div>
		<div class="loader-text">Loading trains...</div>
	</div>

	<main class="container">
		<h1 class="page-title">🚆 Available Trains</h1>


		<%
		if (matchedTrains != null && matchedTrains.length() > 0) {
			for (int i = 0; i < matchedTrains.length(); i++) {
				JSONObject train = matchedTrains.getJSONObject(i);
				String trainId = train.getString("train_id");
				String trainName = trainData.get(trainId);
				JSONObject farePerKm = train.getJSONObject("fare_per_km");

				JSONArray routes = train.getJSONArray("routes");
				double srcDist = 0, destDist = 0;
				for (int j = 0; j < routes.length(); j++) {
			JSONObject stop = routes.getJSONObject(j);
			if (stop.getString("station").equalsIgnoreCase(source))
				srcDist = stop.getDouble("distance_from_start");
			if (stop.getString("station").equalsIgnoreCase(destination))
				destDist = stop.getDouble("distance_from_start");
				}
				double totalDistance = Math.abs(destDist - srcDist);

				JSONArray availableDays = train.getJSONArray("available_days");
				List<String> availableList = new ArrayList<>();
				for (int k = 0; k < availableDays.length(); k++) {
			availableList.add(availableDays.getString(k));
				}
		%>
		<section class="train-card fade-in" data-train-id="<%=trainId%>"
			data-train-name="<%=trainName%>"
			data-routes='<%=routes.toString().replace("'", "\\'")%>'>

			<div class="train-header">
				<div>
					<h2><%=trainName%>
						(<%=trainId%>)
					</h2>
					<p class="route"><%=source%>
						➜
						<%=destination%></p>
				</div>
				<button class="schedule-btn" data-train="<%=trainId%>">Train
					Schedule</button>
			</div>

			<!-- Week Days -->
			<div class="day-strip">
				<%
				String[] shortDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
				String[] fullDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
				for (int d = 0; d < 7; d++) {
					boolean available = availableList.contains(fullDays[d]);
				%>
				<span class="day-pill <%=available ? "" : "disabled"%>"><%=shortDays[d]%></span>
				<%
				}
				%>
			</div>

			<!-- Coach Buttons -->
			<div class="coach-buttons">
				<%
				Iterator<String> it = farePerKm.keys();
				while (it.hasNext()) {
					String coach = it.next();
					 // Hide button if class not in availability
				    if (!seatAvailability.has(trainId) ||
				        !seatAvailability.getJSONObject(trainId).has(coach)) {
				        continue;  // Skip this coach
				    }
				%>
				<button class="coach-btn" data-train="<%=trainId%>"
					data-class="<%=coach%>" data-distance="<%=totalDistance%>"><%=coach%></button>
				<%
				}
				%>
			</div>

			<div class="coach-details hidden" id="details-<%=trainId%>">
				<p>Select a coach class to view availability and fare details.</p>
			</div>

			<div class="book-row">
				<button class="btn ghost other-dates" data-train="<%=trainId%>">Other
					Dates</button>
				<button class="btn book-now" data-train="<%=trainId%>" disabled>Book
					Now</button>
			</div>
		</section>

		<%
		}
		} else {
		%>
		<p class="no-trains">No trains found for your search.</p>
		<%
		}
		%>
	</main>

	<!-- Modal -->
	<div id="scheduleModal" class="modal hidden">
		<div class="modal-content">
			<span class="close-btn">×</span>
			<h2 id="scheduleTrainName"></h2>
			<table id="scheduleTable">
				<thead>
					<tr>
						<th>Station Name</th>
						<th>Arrival</th>
						<th>Departure</th>
						<th>Distance (km)</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>

	<script>
		const seatAvailabilityData = JSON
				.parse(
	<%=JSONObject.quote(seatAvailability.toString())%>
		);
		// ---- Safe fareMap ----
		const fareMap = JSON
				.parse(
	<%JSONObject safeFare = new JSONObject();
if (matchedTrains != null) {
	for (int i = 0; i < matchedTrains.length(); i++) {
		JSONObject train = matchedTrains.getJSONObject(i);
		safeFare.put(train.getString("train_id"), train.getJSONObject("fare_per_km"));
	}
}
out.print(JSONObject.quote(safeFare.toString()));%>
		);
	</script>
</body>
</html>
