document.addEventListener("DOMContentLoaded", () => {

	// 1. Tab Logic
	window.showTab = (tabId, btn) => {
		document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
		btn.classList.add("active");

		document.querySelectorAll(".tab-content").forEach(c => {
			c.style.display = 'none';
			c.classList.remove("active");
		});

		const target = document.getElementById(tabId);
		if (target) {
			target.style.display = 'block';
			setTimeout(() => target.classList.add("active"), 10);
		}
	};

	// 2. Expand/Collapse Card
	window.toggleDetails = (card) => {
		// Don't toggle if user is selecting text
		if (window.getSelection().toString().length > 0) return;
		card.classList.toggle("open");
	};

	// 3. PNR Status Logic
	window.getPNRStatus = async (pnr, btn) => {
		const originalHtml = btn.innerHTML;
		btn.innerHTML = `<i class="ri-loader-4-line ri-spin"></i> Checking...`;
		btn.disabled = true;

		// Simulating API call
		setTimeout(() => {
			btn.innerHTML = originalHtml;
			btn.disabled = false;
			alert("Status Updated for " + pnr); // Replace with real logic if needed
		}, 1000);
	};

	// 4. Modal Logic (Partial Cancellation)
	const modal = document.getElementById("cancelModal");
	const container = document.getElementById("passengerCheckboxes");
	const pnrInput = document.getElementById("cancelPnrInput");

	window.openCancelModal = (pnr) => {
		pnrInput.value = pnr;
		container.innerHTML = ""; // Clear old data

		const card = document.getElementById(`card-${pnr}`);
		if (!card) return;

		// Find all passengers in that card
		const passengers = card.querySelectorAll(".p-row");
		let validCount = 0;

		passengers.forEach(row => {
			const name = row.dataset.name;
			const status = row.dataset.status;

			// Only show passengers who are NOT already cancelled
			if (status && !status.includes("CAN")) {
				validCount++;
				const label = document.createElement("label");
				label.className = "p-checkbox-label";
				label.innerHTML = `
                    <input type="checkbox" name="selectedPassengers" value="${name}">
                    <span class="p-name">${name}</span>
                    <span class="status-pill ${status.includes('CNF') ? 'cnf' : 'wl'}">${status}</span>
                `;
				container.appendChild(label);
			}
		});

		if (validCount === 0) {
			alert("All passengers in this ticket are already cancelled.");
			return;
		}

		modal.classList.add("show");
		modal.classList.remove("hidden");
	};

	window.closeModal = () => {
		modal.classList.remove("show");
		setTimeout(() => modal.classList.add("hidden"), 300);
	};

	// 5. Rebook Logic
	window.rebookJourney = (source, dest) => {
		window.location.href = `RailwayApplication.jsp?fromStation=${source}&toStation=${dest}`;
	};

	// Theme Toggle
	const toggle = document.getElementById("themeToggle");
	if (toggle) {
		toggle.addEventListener("click", () => {
			const root = document.documentElement;
			const isDark = root.getAttribute("data-theme") === "dark";
			root.setAttribute("data-theme", isDark ? "light" : "dark");
			localStorage.setItem("sam_theme", isDark ? "light" : "dark");
		});
	}
});