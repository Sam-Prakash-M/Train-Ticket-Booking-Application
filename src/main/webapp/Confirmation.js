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
	setTimeout(() => document.getElementById("pageLoader").style.display = "none", 600);

	// 3. Populate Berths
	const currentClass = window.TRAIN_CLASS_CODE || "SL";
	const unitFare = window.BASE_FARE || 0;
	const GST_RATE = 0.05; // 5% Tax

	const getBerths = (code) => {
		if (code === "SL" || code === "3A") return ["No Preference", "Lower", "Middle", "Upper", "Side Lower", "Side Upper"];
		if (code === "2A") return ["No Preference", "Lower", "Upper", "Side Lower", "Side Upper"];
		if (code === "1A") return ["No Preference", "Cabin", "Coupe"];
		return ["No Preference", "Window", "Aisle"];
	};

	const fillBerths = (select) => {
		select.innerHTML = getBerths(currentClass).map(o => `<option>${o}</option>`).join("");
	};

	document.querySelectorAll(".berth-select").forEach(fillBerths);

	// 4. Update Fare Logic (Includes Tax)
	const updateFare = () => {
		const count = document.querySelectorAll(".passenger-row").length;

		const totalBase = unitFare * count;
		const tax = totalBase * GST_RATE;
		const grandTotal = totalBase + tax;

		// Update UI Text
		document.getElementById("baseFareDisplay").textContent = `₹${totalBase.toFixed(2)}`;
		document.getElementById("pCountDisplay").textContent = `x ${count}`;
		document.getElementById("taxDisplay").textContent = `₹${tax.toFixed(2)}`;
		document.getElementById("totalFareDisplay").textContent = `₹${grandTotal.toFixed(2)}`;

		// Update Hidden Input for Form Submission
		document.getElementById("inputTotalFare").value = grandTotal.toFixed(2);
	};

	// Run once on load
	updateFare();

	// 5. Add Passenger
	const container = document.getElementById("passengerContainer");
	const MAX_P = 6;

	document.getElementById("addPassengerBtn").addEventListener("click", () => {
		const count = container.querySelectorAll(".passenger-row").length;
		if (count >= MAX_P) {
			showToast("Maximum 6 passengers allowed", true);
			return;
		}

		const firstRow = container.querySelector(".passenger-row");
		const newRow = firstRow.cloneNode(true);

		newRow.querySelectorAll("input").forEach(i => i.value = "");
		newRow.querySelector(".p-count").textContent = `Passenger ${count + 1}`;
		newRow.querySelector(".remove-btn").classList.remove("hidden");

		container.appendChild(newRow);
		updateFare();
	});

	// 6. Remove Logic
	window.removePassenger = (btn) => {
		btn.closest(".passenger-row").remove();
		container.querySelectorAll(".passenger-row").forEach((row, i) => {
			row.querySelector(".p-count").textContent = `Passenger ${i + 1}`;
		});
		updateFare();
	};

	window.showToast = (msg, isError = false) => {
		const t = document.getElementById("toast");
		t.textContent = msg;
		t.className = isError ? "toast show error" : "toast show";
		setTimeout(() => t.className = "toast", 3000);
	};
});