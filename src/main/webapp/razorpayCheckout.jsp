<%@ page contentType="text/html; charset=UTF-8" %>
<%
    String orderId = (String) request.getAttribute("order_id");
    String amount  = String.valueOf(request.getAttribute("amount"));
    String keyId   = (String) request.getAttribute("key");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Processing Payment...</title>
    <link rel="stylesheet" href="razorpayCheckout.css?v=1">
</head>
<body>

<div class="loading-container">
    <div class="spinner"></div>
    <p>Redirecting to secure Razorpay payment...</p>
</div>

<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
<script src="razorpayCheckout.js?v=1"></script>

<script>
    openRazorpayCheckout("<%= keyId %>", "<%= amount %>", "<%= orderId %>");
</script>

</body>
</html>
