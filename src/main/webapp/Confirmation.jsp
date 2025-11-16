<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Passenger Details - Confirm Booking</title>
    <link rel="stylesheet" href="Confirmation.css?v=1">
    <script defer src="Confirmation.js?v=1"></script>
</head>
<body>

<div class="container">

    <!-- Train Summary -->
    <div class="train-summary-card">
        <h2><%= request.getAttribute("trainId") %> - <%= request.getAttribute("classType") %></h2>

        <div class="train-row">
            <div>
                <p class="station"><%= request.getAttribute("source") %></p>
                <p class="time">Departure</p>
            </div>

            <div class="arrow">➜</div>

            <div>
                <p class="station"><%= request.getAttribute("destination") %></p>
                <p class="time">Arrival</p>
            </div>
        </div>

        <p class="quota">Quota: General</p>
    </div>


    <!-- Passenger Section -->
    <h3 class="section-title">Passenger Details</h3>

    <form action="PaymentServlet" method="post" id="passengerForm">

        <div id="passengerContainer">

            <!-- Default Passenger Row -->
            <div class="passenger-row">
                <input type="text" name="pname[]" placeholder="Passenger Name" required>
                <input type="number" name="page[]" min="1" max="120" placeholder="Age" required>
                <select name="pgender[]">
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                </select>

                <select name="pnationality[]">
                    <option value="India">India</option>
                    <option value="Other">Other</option>
                </select>

                <select name="berth[]">
                    <option value="No Preference">No Preference</option>
                    <option value="Lower">Lower</option>
                    <option value="Middle">Middle</option>
                    <option value="Upper">Upper</option>
                </select>

                <button type="button" class="remove-btn" onclick="removePassenger(this)">×</button>
            </div>

        </div>

        <button type="button" id="addPassengerBtn" class="add-btn">+ Add Passenger</button>


        <!-- Contact Details -->
        <h3 class="section-title">Contact Details</h3>

        <div class="contact-box">
            <div class="contact-row">
                <input type="text" value="+91" disabled>
                <input type="text" name="mobile" placeholder="Enter Mobile Number" required>
            </div>
            <input type="email" name="email" placeholder="Enter Email" required>
        </div>


        <!-- Preferences -->
        <h3 class="section-title">Other Preferences</h3>

        <div class="pref-box">
            <label><input type="checkbox" name="autoUpgrade"> Consider for Auto Upgradation</label>
        </div>


        <!-- Fare Summary -->
        <div class="fare-summary">
            <p>Ticket Fare: ₹ <%= request.getAttribute("fare") %></p>
            <h3>Total Fare: ₹ <%= request.getAttribute("fare") %></h3>
        </div>


        <!-- Action Buttons -->
        <div class="action-row">
            <button type="button" class="back-btn" onclick="history.back()">Back</button>
            <button type="submit" class="continue-btn">Continue</button>
        </div>

    </form>

</div>

</body>
</html>
