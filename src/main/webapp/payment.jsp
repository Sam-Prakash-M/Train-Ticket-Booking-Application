<%@ page contentType="text/html;charset=UTF-8"%>

<%
double totalFare = (Double) request.getAttribute("totalFare");
System.out.println("Total Fare : " + totalFare);
double gst = (Double) request.getAttribute("gst");
double serviceCharge = (Double) request.getAttribute("serviceCharge");
double total = (Double) request.getAttribute("totalPayable");

String[] names = (String[]) request.getAttribute("names");
String[] ages = (String[]) request.getAttribute("ages");
String[] genders = (String[]) request.getAttribute("genders");
String[] berths = (String[]) request.getAttribute("berths");
%>

<!DOCTYPE html>
<html>
<head>
<title>Payment Summary</title>
<link rel="stylesheet" href="payment.css?v=5">
</head>
<body>



	<div class="bg-gradient"></div>

	<div class="page-wrapper">
		<!-- Train Details Inside Payment Card -->
<div class="train-box animate-fade-up">

    <div class="train-top">
        <img src="train.png" class="train-logo">
        <div>
            <h3 class="train-title">
                <%= session.getAttribute("trainName") %> 
                (<%= session.getAttribute("trainId") %>)
            </h3>
            <p class="train-route-text">
                <%= session.getAttribute("source") %> → <%= session.getAttribute("destination") %>
            </p>
        </div>
    </div>

    <div class="train-route-box">
        <div class="station">
            <p class="station-name"><%= session.getAttribute("source") %></p>
            <p class="station-time">Dep: <%= session.getAttribute("sourceDeparture") %></p>
        </div>

        <div class="route-arrow-center">
            <span>➜</span>
        </div>

        <div class="station">
            <p class="station-name"><%= session.getAttribute("destination") %></p>
            <p class="station-time">Arr: <%= session.getAttribute("destinationArrival") %></p>
        </div>
    </div>

    <p class="train-class">
        Class: <%= session.getAttribute("classType") %>
    </p>

</div>


		<div class="payment-card animate-slide">

			<h2 class="header-title">Payment Summary</h2>
			<!-- Animated Price -->
			<div class="price-card glass animate-fade">
				<span class="price-title">Total Amount</span> <span
					class="price-value">₹ <%=String.format("%.2f", total)%></span>
			</div>

			<!-- Passenger Section -->
			<div class="section glass animate-fade-up">
				<h3>
					<i class="icon-user"></i> Passenger Details
				</h3>
				<ul class="passenger-list">
					<%
					for (int i = 0; i < names.length; i++) {
					%>
					<li><span class="bullet"></span> <strong><%=names[i]%></strong>
						<small>(Age: <%=ages[i]%>, <%=genders[i]%>, Berth: <%=berths[i]%>)
					</small></li>
					<%
					}
					%>
				</ul>
			</div>

			<!-- Fare Section -->
			<div class="section glass animate-fade-up delay-1">
				<h3>
					<i class="icon-bill"></i> Fare Breakdown
				</h3>

				<div class="row">
					<span>Base Fare</span> <span>₹ <%=totalFare%></span>
				</div>

				<div class="row">
					<span>GST (5%)</span> <span>₹ <%=String.format("%.2f", gst)%></span>
				</div>

				<div class="row">
					<span>Service Charge</span> <span>₹ <%=serviceCharge%></span>
				</div>

				<div class="divider"></div>

				<div class="row total">
					<span>Total</span> <span>₹ <%=String.format("%.2f", total)%></span>
				</div>
			</div>

			<!-- Payment Buttons -->
			<h3 class="pay-title animate-fade-up delay-2">Select Payment
				Method</h3>

			<div class="gateways animate-fade-up delay-3">

				<button class="pay-btn razor"
					onclick="payWithRazorpay('<%=total%>')">
					<img
						src="https://upload.wikimedia.org/wikipedia/commons/8/89/Razorpay_logo.svg">
					<span>Razorpay</span>
				</button>

				<button class="pay-btn cashfree"
					onclick="payWithCashfree('<%=total%>')">
					<img
						src="https://images.seeklogo.com/logo-png/47/1/cashfree-payments-logo-png_seeklogo-479373.png">
					<span>Cashfree</span>
				</button>

				<button class="pay-btn paypal" onclick="payWithPaypal('<%=total%>')">
					<img
						src="https://upload.wikimedia.org/wikipedia/commons/b/b5/PayPal.svg">
					<span>PayPal</span>
				</button>

			</div>

			<p class="secure-tag animate-fade">🔒 100% Secure Payment</p>

		</div>
	</div>

	<script src="payment.js?v=5"></script>

</body>
</html>
