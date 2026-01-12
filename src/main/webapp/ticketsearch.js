document.addEventListener("DOMContentLoaded", () => {

	// 1. Loader Logic (Same as before)
	const loader = document.getElementById("pageLoader");
	setTimeout(() => {
		if (loader) {
			loader.style.opacity = "0";
			loader.style.visibility = "hidden";
			setTimeout(() => loader.remove(), 500);
		}
	}, 1200);

	// 2. Tab Switching
	const tabs = document.querySelectorAll(".tab-btn");
	const sections = document.querySelectorAll(".tab-content");

	tabs.forEach(tab => {
		tab.addEventListener("click", () => {
			tabs.forEach(t => t.classList.remove("active"));
			tab.classList.add("active");
			sections.forEach(s => s.classList.remove("active"));
			const targetId = tab.dataset.target;
			document.getElementById(targetId).classList.add("active");
		});
	});

	// 3. Theme Toggle (Robust)
	const toggleBtn = document.getElementById("themeToggle");
	const root = document.documentElement;

	const updateIcon = (theme) => {
		if (!toggleBtn) return;
		const icon = toggleBtn.querySelector("i");
		if (icon) {
			icon.className = theme === 'dark' ? "ri-sun-line" : "ri-moon-clear-line";
		}
	};

	// Init
	const currentTheme = localStorage.getItem('sam_theme') || 'light';
	updateIcon(currentTheme);

	if (toggleBtn) {
		toggleBtn.addEventListener("click", () => {
			const current = root.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
			const next = current === 'dark' ? 'light' : 'dark';

			if (next === 'dark') root.setAttribute('data-theme', 'dark');
			else root.removeAttribute('data-theme');

			localStorage.setItem('sam_theme', next);
			updateIcon(next);
		});
	}

	// 4. Swap Stations
	const swapBtn = document.getElementById("swapStations");
	if (swapBtn) {
		swapBtn.addEventListener("click", () => {
			const from = document.getElementById("source");
			const to = document.getElementById("destination");

			swapBtn.style.transform = "rotate(180deg)";

			const temp = from.value;
			from.value = to.value;
			to.value = temp;

			setTimeout(() => { swapBtn.style.transform = ""; }, 300);
		});
	}

	// 5. Date Picker
	const dateInputs = document.querySelectorAll("input[id*='Date']");
	dateInputs.forEach(input => {
		flatpickr(input, {
			dateFormat: "Y-m-d",
			minDate: input.dataset.min,
			maxDate: input.dataset.max,
			disableMobile: "true"
		});
	});

	// PNR AJAX HANDLER
	const pnrForm = document.getElementById("pnrForm");

	if (pnrForm) {
		pnrForm.addEventListener("submit", function(e) {
			e.preventDefault();

			const pnrInput = document.getElementById("pnrInput").value;
			const container = document.getElementById("pnrResultContainer");
			const btnText = document.getElementById("pnrBtnText");
			const loader = document.getElementById("pnrLoader");

			if (!pnrInput) return;

			// UI Loading State
			btnText.textContent = "Checking...";
			loader.classList.remove("hidden");
			container.classList.add("hidden");

			// Fetch Data
			fetch(`PnrStatus?pnr=${pnrInput}`, {
				method: 'GET',
				headers: { 'X-Requested-With': 'XMLHttpRequest' }
			})
				.then(response => response.json())
				.then(json => {
					if (json.status === 'success') {
						// Pass the ticket data AND the computed status string
						container.innerHTML = renderTicketCard(json.data, json.pnrStatus);
					} else {
						container.innerHTML = `
	                    <div class="error-box">
	                        <i class="ri-error-warning-fill" style="font-size:2rem; margin-bottom:10px"></i>
	                        <p>${json.message}</p>
	                    </div>`;
					}
					container.classList.remove("hidden");
				})
				.catch(err => {
					container.innerHTML = `<div class="error-box">Server Error. Please try again.</div>`;
					container.classList.remove("hidden");
				})
				.finally(() => {
					btnText.textContent = "Check Status";
					loader.classList.add("hidden");
				});
		});
	}

	function renderTicketCard(ticket, pnrStatus) {
		// 1. Determine Status Banner Logic
		let statusBannerHTML = '';
		if (pnrStatus === 'FLUSHED') {
			statusBannerHTML = `
	            <div class="status-banner banner-flushed">
	                <i class="ri-history-line"></i> Journey Completed / Chart Flushed
	            </div>`;
		} else {
			statusBannerHTML = `
	            <div class="status-banner banner-live">
	                <i class="ri-broadcast-line"></i> Live Status
	            </div>`;
		}

		// 2. Build Passenger Rows
		let paxRows = '';
		if (ticket.associatedPassenger) {
			ticket.associatedPassenger.forEach((p, index) => {
				let statusClass = 'st-wl';
				let displayText = p.ticketStatus;

				if (p.ticketStatus.includes('CNF')) statusClass = 'st-cnf';
				else if (p.ticketStatus.includes('CAN')) statusClass = 'st-can';

				// Handle Seat Info safely
				let seatInfo = '-';
				if (p.seatMetaData) {
					seatInfo = `${p.seatMetaData.coachNo} / ${p.seatMetaData.seatNumber}`;
				}

				paxRows += `
	                <tr>
	                    <td>${index + 1}</td>
	                    <td>${p.name}</td>
	                    <td><span class="st-badge ${statusClass}">${displayText}</span></td>
	                    <td style="font-weight:600">${seatInfo}</td>
	                </tr>
	            `;
			});
		}

		return `
	        <div class="ticket-card">
	            <div class="ticket-header">
	                <div>
	                    <span class="train-name">${ticket.trainName}</span>
	                    <span class="train-num">${ticket.trainId}</span>
	                </div>
	                <div class="pnr-badge">PNR: ${ticket.pnrNumber}</div>
	            </div>
	            
	            ${statusBannerHTML}

	            <div class="ticket-body">
	                <div class="route-row">
	                    <span class="station">${ticket.sourceArr}</span>
	                    <i class="ri-arrow-right-line arrow"></i>
	                    <span class="station">${ticket.destinationArr}</span>
	                </div>
	                <div style="margin-bottom:15px; font-size:0.9rem; color:var(--text-muted)">
	                    <span><i class="ri-calendar-line"></i> ${ticket.bookingDate}</span> &nbsp;|&nbsp;
	                    <span><i class="ri-armchair-line"></i> ${ticket.className}</span>
	                </div>
	                <table class="pax-table">
	                    <thead>
	                        <tr><th>#</th><th>Passenger</th><th>Status</th><th>Seat</th></tr>
	                    </thead>
	                    <tbody>${paxRows}</tbody>
	                </table>
	            </div>
	            <div class="ticket-footer">
	                <span>Total Fare</span>
	                <span class="fare-val">₹${ticket.totalFare.toFixed(2)}</span>
	            </div>
	        </div>
	    `;
	}
});