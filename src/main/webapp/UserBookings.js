document.addEventListener("DOMContentLoaded", () => {

	// 1. Theme Logic
	const toggle = document.getElementById("themeToggle");
	if (toggle) {
		toggle.addEventListener("click", () => {
			const root = document.documentElement;
			const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
			root.setAttribute("data-theme", next);
			localStorage.setItem("sam_theme", next);

			const i = toggle.querySelector("i");
			if (i) i.className = next === 'dark' ? 'ri-sun-line' : 'ri-moon-line';
		});
	}

	// 2. Tab Logic
	window.showTab = (tabId, btn) => {
		document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
		btn.classList.add("active");
		document.querySelectorAll(".tab-content").forEach(c => c.classList.remove("active"));
		document.getElementById(tabId).classList.add("active");
	};

	// 3. Card Expand Logic
	window.toggleDetails = (card) => {
		card.classList.toggle("open");
	};

	// 4. API: Get PNR Status
	window.getPNRStatus = async (pnr, btn) => {
		// Prevent card collapse
		// event.stopPropagation() is handled in HTML onclick

		// UI Feedback: Loading
		const originalText = btn.innerHTML;
		btn.innerHTML = `<i class="ri-loader-4-line ri-spin"></i> Checking...`;
		btn.disabled = true;

		try {
			// Replace 'API/Status' with your actual servlet/REST endpoint
			// Example Response: 
			// { "passengers": [ {"name": "Sam", "status": "CNF", "coach": "S1", "seat": "12"}, ... ] }

			const response = await fetch(`PNRServlet?pnr=${pnr}&type=json`);

			if (!response.ok) throw new Error("Network error");

			const data = await response.json();

			// Update UI based on data
			updatePassengerTable(pnr, data);

			showToast("Status Updated Successfully!");

		} catch (error) {
			console.error("Error fetching PNR status:", error);
			showToast("Failed to fetch status", true);
		} finally {
			// Reset Button
			btn.innerHTML = originalText;
			btn.disabled = false;
		}
	};

	function updatePassengerTable(pnr, data) {
		// Find the specific table for this PNR
		const table = document.getElementById(`table-${pnr}`);
		if (!table) return;

		// Assuming data.passengers is an array of objects
		// You might need to adjust logic based on your actual JSON structure
		// Here assuming simple order match or name match
		const rows = table.querySelectorAll("tbody tr");

		if (data.passengers && data.passengers.length > 0) {
			rows.forEach((row, index) => {
				// If API returns data in same order as booking
				const pData = data.passengers[index];

				if (pData) {
					const statusCell = row.cells[4]; // 5th column is status
					const coachCell = row.cells[3]; // 4th column is Coach/Seat

					// Update Status Badge
					let badgeClass = "wl";
					if (pData.currentStatus.includes("CNF")) badgeClass = "cnf";
					else if (pData.currentStatus.includes("RAC")) badgeClass = "rac";

					statusCell.innerHTML = `<span class="status-badge ${badgeClass}">${pData.currentStatus}</span>`;

					// Update Coach/Seat if available/changed
					if (pData.coach && pData.seatNumber) {
						coachCell.innerText = `${pData.coach} - ${pData.seatNumber}`;
					}
				}
			});
		}
	}

	// 5. Toast Notification
	function showToast(msg, isError = false) {
		const t = document.getElementById("toast");
		t.textContent = msg;
		t.className = isError ? "toast show error" : "toast show";
		setTimeout(() => t.className = "toast", 3000);
	}
	
	// 5. Cancel Modal Logic
	    const modal = document.getElementById("cancelModal");
	    const pnrDisplay = document.getElementById("cancelPnrDisplay");
	    const pnrInput = document.getElementById("cancelPnrInput");

	    window.confirmCancel = (pnr) => {
	        pnrDisplay.textContent = pnr;
	        pnrInput.value = pnr;
	        modal.classList.remove("hidden");
	    };

	    window.closeModal = () => {
	        modal.classList.add("hidden");
	    };

	    window.addEventListener("click", (e) => {
	        if (e.target === modal) closeModal();
	    });

});