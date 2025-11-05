<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Train Booking Availability</title>
  <link rel="stylesheet" href="booking.css?v=5">
  <script defer src="booking.js?v=5"></script>
</head>
<body>

<div class="header">
  <h2>Train Availability</h2>
  <div class="date-navigation">
    <button id="prevDate">⬅ Previous</button>
    <span id="currentDate"><%= request.getAttribute("travelDate") %></span>
    <button id="nextDate">Next ➡</button>
  </div>
</div>

<div id="loader" class="loader"></div>

<div id="trainContainer" class="train-container">
  <%
    org.json.JSONObject availability = (org.json.JSONObject) request.getAttribute("TrainSeatAvailability");
    if (availability != null && availability.length() > 0) {
        java.util.Iterator<String> trainKeys = availability.keys();
        while (trainKeys.hasNext()) {
            String trainID = trainKeys.next();
            org.json.JSONObject trainObj = availability.getJSONObject(trainID);
  %>
      <div class="train-card">
        <h3><%= trainID %></h3>
        <div class="train-details">
          <% 
            java.util.Iterator<String> coachKeys = trainObj.keys();
            while (coachKeys.hasNext()) {
                String coach = coachKeys.next();
                org.json.JSONObject coachObj = trainObj.getJSONObject(coach);
          %>
              <div class="coach-info">
                <span class="coach-no"><%= coach %></span>
                <span class="coach-class"><%= coachObj.getString("class") %></span>
                <span class="available">Available: <%= coachObj.getInt("available_seats") %></span>
                <span class="fare">Fare: ₹<%= coachObj.optInt("fare", 0) %></span>
              </div>
          <% } %>
        </div>
        <button class="book-btn">Book Now</button>
      </div>
  <% 
        }
    } else { 
  %>
    <p class="no-trains">No trains available for this date.</p>
  <% } %>
</div>

</body>
</html>
