<%@ page contentType="text/html;charset=UTF-8" %>
<%
    double fare = (Double) request.getAttribute("fare");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Select Payment Method</title>
    <link rel="stylesheet" href="payment.css?v=2">
</head>
<body>

<div class="page-wrapper">
    <div class="payment-card">

        <div class="fade-in">
            <h2 class="title">Complete Your Payment</h2>
            <p class="amount">₹ <%= fare %></p>
            <p class="sub-text">Choose your preferred payment gateway</p>
        </div>

        <div class="gateway-container fade-in">

            <button class="gateway-btn razorpay" onclick="payWithRazorpay('<%= fare %>')">
                <img src="https://upload.wikimedia.org/wikipedia/commons/8/89/Razorpay_logo.svg">
                Razorpay
            </button>

            <button class="gateway-btn cashfree" onclick="payWithCashfree('<%= fare %>')">
               <img src="https://cdn.brandfetch.io/idLecjUPYL/theme/dark/logo.svg?c=1bxid64Mup7aczewSAYMX&t=1700772642959">
                Cashfree
            </button>

            <button class="gateway-btn paypal" onclick="payWithPaypal('<%= fare %>')">
                <img src="https://upload.wikimedia.org/wikipedia/commons/b/b5/PayPal.svg">
                PayPal
            </button>

        </div>

        <div class="footer fade-in">
            <p class="safe">🔒 Secured by SSL • PCI-DSS Compliant</p>
        </div>

    </div>
</div>

<script src="payment.js?v=2"></script>

</body>
</html>
