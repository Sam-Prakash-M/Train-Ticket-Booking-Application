<%@ page import="org.json.*,java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Train Booking Results</title>
  <link rel="stylesheet" href="booking.css">
  <script defer src="booking.js"></script>
</head>
<body>
  <!-- Loader Overlay -->
  <div id="pageLoader" class="loader-overlay hidden">
    <div class="spinner"></div>
    <div class="loader-text">Loading trains…</div>
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

          // Calculate distance between source and destination
          JSONArray routes = train.getJSONArray("routes");
          double srcDist = 0, destDist = 0;
          for (int j = 0; j < routes.length(); j++) {
            JSONObject stop = routes.getJSONObject(j);
            if (stop.getString("station").equalsIgnoreCase(source)) {
              srcDist = stop.getDouble("distance_from_start");
            }
            if (stop.getString("station").equalsIgnoreCase(destination)) {
              destDist = stop.getDouble("distance_from_start");
            }
          }
          double totalDistance = Math.abs(destDist - srcDist);

          // Available days JSON for this train (used by JS to render day strip)
          String availableDaysJson = train.getJSONArray("available_days").toString();
    %>

    <section class="train-card fade-in"
             data-train-id="<%=trainId%>"
             data-available-days='<%=availableDaysJson%>'>
      <div class="train-header">
        <div>
          <h2><%=trainName%> (<%=trainId%>)</h2>
          <p class="route"><%=source%> ➜ <%=destination%></p>
        </div>
        <div class="distance">Distance: <%= (int) totalDistance %> km</div>
      </div>

      <!-- Day Strip (built by JS from data-available-days) -->
      <div class="day-strip" id="days-<%=trainId%>"></div>

      <!-- Coach/Class buttons (your original) -->
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
        <p>Select a coach class to view availability and fare.</p>
      </div>

      <!-- Book Now + Other Dates -->
      <div class="book-row">
        <button class="btn ghost other-dates" data-train="<%=trainId%>">Other Dates</button>
        <button class="btn book-now" data-train="<%=trainId%>" disabled>Book Now</button>
      </div>
    </section>

    <% } } else { %>
      <p class="no-trains">No trains found for your search.</p>
    <% } %>
  </main>

  <!-- Server → Client data -->
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
