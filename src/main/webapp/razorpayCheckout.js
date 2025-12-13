// 1. Theme Logic
const toggle = document.getElementById("themeToggle");
if (toggle) {
	toggle.addEventListener("click", () => {
		const root = document.documentElement;
		const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
		root.setAttribute("data-theme", next);
		localStorage.setItem("sam_theme", next);

		// Update Icon
		const i = toggle.querySelector("i");
		i.className = next === 'dark' ? 'ri-sun-line' : 'ri-moon-line';
	});
}

// 2. Razorpay Logic
function openRazorpayCheckout(keyId, amountPaise, orderId) {

	// Check if Razorpay loaded
	if (typeof Razorpay === 'undefined') {
		alert("Payment gateway failed to load. Please check internet connection.");
		document.getElementById("retryBtn").classList.remove("hidden");
		return;
	}

	const options = {
		"key": keyId,
		"amount": amountPaise,
		"currency": "INR",
		"name": "Sam Railways",
		"description": "Ticket Booking Payment",
		"order_id": orderId,
		"image": "https://cdn-icons-png.flaticon.com/512/821/821354.png", // Train Icon
		"theme": {
			"color": "#2563eb"
		},
		"handler": function(response) {
			// Show processing state
			document.querySelector(".process-card h2").innerText = "Payment Successful!";
			document.querySelector(".process-card p").innerText = "Finalizing booking...";

			// Submit form
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
		},
		"modal": {
			"ondismiss": function() {
				// Handle if user closes modal manually
				document.querySelector(".process-card p").innerText = "Payment cancelled by user.";
				document.getElementById("retryBtn").classList.remove("hidden");
			}
		}
	};

	const rzp = new Razorpay(options);

	rzp.on('payment.failed', function(response) {
		document.querySelector(".process-card h2").innerText = "Payment Failed";
		document.querySelector(".process-card p").innerText = response.error.description;
		document.getElementById("retryBtn").classList.remove("hidden");
	});

	rzp.open();
}