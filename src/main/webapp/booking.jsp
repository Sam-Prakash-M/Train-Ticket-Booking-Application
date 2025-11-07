<%@ page import="org.json.*,java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Available Trains</title>
  <link rel="stylesheet" href="booking.css?v=5">
  <script defer src="booking.js?v=5"></script>
</head>
<body>

  <!-- Loader -->
  <div id="pageLoader" class="loader-overlay hidden">
    <div class="spinner"></div>
    <div class="loader-text">Loading trains...</div>
  </div>

  <main class="container">
    <h1 class="page-title">🚆 Available Trains</h1>

    <%
      JSONArray matchedTrains = (JSONArray) request.getAttribute("MatchedTrainList");
      Map<String,String> trainData = (Map<String,String>) request.getAttribute("TrainData");
      JSONObject seatAvailability = (JSONObject) request.getAttribute("TrainSeatAvailability");
      String source = (String) request.getAttribute("SourceStation");
      String destination = (String) request.getAttribute("DestinationStation");
    %>

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
             data-routes='<%=routes.toString().replace("'", "\\'")%>'>
      <div class="train-header">
        <div>
          <h2><%=trainName%> (<%=trainId%>)</h2>
          <p class="route"><%=source%> ➜ <%=destination%></p>
        </div>
        <button class="schedule-btn" data-train="<%=trainId%>">Train Schedule</button>
      </div>

      <!-- Week Days -->
      <div class="day-strip">
        <%
          String[] shortDays = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
          String[] fullDays = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
          for (int d = 0; d < 7; d++) {
            boolean available = availableList.contains(fullDays[d]);
        %>
          <span class="day-pill <%= available ? "" : "disabled" %>"><%= shortDays[d] %></span>
        <% } %>
      </div>

      <!-- Coach Buttons -->
      <div class="coach-buttons">
        <% Iterator<String> it = farePerKm.keys();
           while (it.hasNext()) {
             String coach = it.next(); %>
             <button class="coach-btn"
                     data-train="<%=trainId%>"
                     data-class="<%=coach%>"
                     data-distance="<%=totalDistance%>"><%=coach%></button>
        <% } %>
      </div>

      <div class="coach-details hidden" id="details-<%=trainId%>">
        <p>Select a coach class to view availability and fare details.</p>
      </div>

      <div class="book-row">
        <button class="btn ghost other-dates" data-train="<%=trainId%>">Other Dates</button>
        <button class="btn book-now" data-train="<%=trainId%>" disabled>Book Now</button>
      </div>
    </section>

    <% } } else { %>
      <p class="no-trains">No trains found for your search.</p>
    <% } %>
  </main>

  <!-- Modal -->
  <div id="scheduleModal" class="modal hidden">
    <div class="modal-content">
      <span class="close-btn">×</span>
      <h2 id="scheduleTrainName"></h2>
      <table id="scheduleTable">
        <thead>
          <tr><th>Station Name</th><th>Arrival</th><th>Departure</th><th>Distance (km)</th></tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </div>

  <script>
    const seatAvailabilityData = <%= seatAvailability.toString() %>;
    const fareMap = {};
    <% if (matchedTrains != null) {
         for (int i = 0; i < matchedTrains.length(); i++) {
           JSONObject train = matchedTrains.getJSONObject(i);
           String id = train.getString("train_id");
           JSONObject fare = train.getJSONObject("fare_per_km");
    %>
        fareMap["<%=id%>"] = <%=fare.toString()%>;
    <%   }
       } %>
  </script>
</body>
</html>
