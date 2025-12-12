document.addEventListener("DOMContentLoaded", () => {

	// 1. Theme Logic
	const toggle = document.getElementById("themeToggle");
	if (toggle) {
		toggle.addEventListener("click", () => {
			const root = document.documentElement;
			const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
			root.setAttribute("data-theme", next);
			localStorage.setItem("sam_theme", next);
		});
	}

	// 2. Loader
	setTimeout(() => {
		const loader = document.getElementById("pageLoader");
		if (loader) loader.style.display = "none";
	}, 800);

	// 3. Payment Logic
	window.postToServlet = (url, amount) => {
		// Show loader again
		const loader = document.getElementById("pageLoader");
		if (loader) {
			loader.querySelector("p").textContent = "Redirecting to Gateway...";
			loader.style.display = "flex";
		}

		const form = document.createElement("form");
		form.method = "POST";
		form.action = url;

		const input = document.createElement("input");
		input.type = "hidden";
		input.name = "amount";
		input.value = amount;

		form.appendChild(input);
		document.body.appendChild(form);
		form.submit();
	};

	window.payWithRazorpay = (amount) => {
		postToServlet("RazorPayPayment", amount);
	};

	window.payWithCashfree = (amount) => {
		postToServlet("CashfreePaymentServlet", amount);
	};

	window.payWithPaypal = (amount) => {
		postToServlet("PaypalServlet", amount);
	};

});