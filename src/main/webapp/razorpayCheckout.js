function openRazorpayCheckout(keyId, amountPaise, orderId) {

	const options = {
		"key": keyId,
		"amount": amountPaise,
		"currency": "INR",
		"name": "Train Ticket Booking",
		"description": "Ticket Payment",
		"order_id": orderId,

		"handler": function(response) {

			// Create hidden form
			const form = document.createElement("form");
			form.method = "POST";
			form.action = "PaymentSuccess";

			form.innerHTML = `
			        <input type="hidden" name="payment_id" value="${response.razorpay_payment_id}">
			        <input type="hidden" name="order_id" value="${response.razorpay_order_id}">
			        <input type="hidden" name="signature" value="${response.razorpay_signature}">
			    `;
			
			document.body.appendChild(form);
			form.submit();
		}
		,

		"theme": {
			"color": "#00eaff"
		}
	};

	const rzp = new Razorpay(options);

	// Auto-open checkout
	rzp.open();

	// Prevent double triggers
	rzp.on('payment.failed', function(response) {
		alert("Payment Failed: " + response.error.description);
		console.error(response);
	});
}
