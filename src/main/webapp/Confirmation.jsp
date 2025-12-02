<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Passenger Details - Confirm Booking</title>
<link rel="stylesheet" href="Confirmation.css?v=3">
<script>
        // ClassType → extract code like SL, 2A, 3A, 1A, CC, EC
        const classText = "<%=request.getAttribute("classType")%>";

        function extractClassCode(str) {
            const match = str.match(/\((.*?)\)/);
            return match ? match[1] : str;
        }

        const TRAIN_CLASS_CODE = extractClassCode(classText); 
        const baseFare = parseFloat("<%=request.getAttribute("fare")%>");
</script>
<script defer src="Confirmation.js?v=3"></script>
</head>
<body>

	<div class="container">

		<!-- Train Summary -->
		<div class="train-summary-card">
			<h2>
			   <%=request.getAttribute("travelDate") %>
				<%=request.getAttribute("trainName")%>
				(<%=request.getAttribute("trainId")%>) - Class Type (
				<%=request.getAttribute("classType")%>
				)
			</h2>
			

			<div class="train-row">
				<div>
					<p class="station"><%=request.getAttribute("source")%></p>
					<p class="time">
						Dep:
						<%=request.getAttribute("sourceDeparture")%>
					</p>
					<p class="time small">
						Arr:
						<%=request.getAttribute("sourceArrival")%>
					</p>
				</div>

				<div class="arrow">➜</div>

				<div>
					<p class="station"><%=request.getAttribute("destination")%></p>
					<p class="time">
						Arr:
						<%=request.getAttribute("destinationArrival")%>
					</p>
					<p class="time small">
						Dep:
						<%=request.getAttribute("destinationDeparture")%>
					</p>
				</div>
			</div>


			<p class="quota">Quota: General</p>
		</div>

		<!-- Passenger Section -->
		<h3 class="section-title">Passenger Details</h3>

		<form action="PaymentServlet" method="post" id="passengerForm">

			<div id="passengerContainer">

				<!-- Default Passenger Row (Berth Loaded by JS) -->
				<div class="passenger-row">
					<input type="text" name="pname[]" placeholder="Passenger Name"
						required> <input type="number" name="page[]" min="1"
						max="120" placeholder="Age" required> <select
						name="pgender[]">
						<option value="Male">Male</option>
						<option value="Female">Female</option>
						<option value="Other">Other</option>
					</select> <select name="pnationality[]">
						<option value="India">India</option>
						<option value="Other">Other</option>
					</select> <select name="berth[]" class="berth-select">
						<!-- JS dynamically inserts correct berths -->
					</select>

					<button type="button" class="remove-btn"
						onclick="removePassenger(this)">×</button>
				</div>

			</div>

			<button type="button" id="addPassengerBtn" class="add-btn">+
				Add Passenger</button>


			<!-- Contact Details -->
			<h3 class="section-title">Contact Details</h3>

			<div class="contact-box">

				<div class="contact-label">Mobile Number</div>
				<div class="contact-row">
					<select class="country-code" disabled>
						<option>+91</option>
					</select> <input type="text" name="mobile" placeholder="Enter Mobile Number"
						required>
				</div>

				<div class="contact-label">Email Address</div>
				<input type="email" name="email" placeholder="Enter Email Address"
					class="email-input" required>

			</div>



			<!-- Preferences -->
			<h3 class="section-title">Other Preferences</h3>

			<div class="pref-box">
				<label><input type="checkbox" name="autoUpgrade">
					Consider for Auto Upgradation</label>
			</div>


			<div class="fare-summary">
				<p>
					Ticket Fare: ₹
					<%=request.getAttribute("fare")%>
				</p>

				<input type="hidden" name="fare"
					value="<%=request.getAttribute("fare")%>">

				<h3>
					Total Fare: ₹
					<%=request.getAttribute("fare")%></h3>
			</div>



			<!-- Action Buttons -->
			<div class="action-row">
				<button type="button" class="back-btn" onclick="history.back()">Back</button>
				<button type="submit" class="continue-btn">Continue</button>
			</div>

			<input type="hidden" name="trainName"
				value="<%=request.getAttribute("trainName")%>"> <input
				type="hidden" name="trainId"
				value="<%=request.getAttribute("trainId")%>"> <input
				type="hidden" name="source"
				value="<%=request.getAttribute("source")%>"> <input
				type="hidden" name="destination"
				value="<%=request.getAttribute("destination")%>"> <input
				type="hidden" name="sourceDeparture"
				value="<%=request.getAttribute("sourceDeparture")%>"> <input
				type="hidden" name="sourceArrival"
				value="<%=request.getAttribute("sourceArrival")%>"> <input
				type="hidden" name="destinationArrival"
				value="<%=request.getAttribute("destinationArrival")%>"> <input
				type="hidden" name="destinationDeparture"
				value="<%=request.getAttribute("destinationDeparture")%>">
			<input type="hidden" name="classType"
				value="<%=request.getAttribute("classType")%>">
			<input type="hidden" name="travelDate"
					value="<%=request.getAttribute("travelDate")%>">

		</form>

	</div>
	<div id="toast" class="toast"></div>

</body>
</html>
