document.addEventListener("DOMContentLoaded", () => {

	// 1. Loader Logic
	const loader = document.getElementById("pageLoader");
	setTimeout(() => {
		if (loader) {
			loader.style.opacity = "0";
			loader.style.visibility = "hidden";
			setTimeout(() => loader.remove(), 500);
		}
	}, 800);

	// 2. Tab Switching
	window.switchTab = (targetId) => {
		document.querySelectorAll(".tab-btn").forEach(t => t.classList.remove("active"));
		document.querySelectorAll(".tab-content").forEach(s => s.classList.remove("active"));

		const btn = document.querySelector(`.tab-btn[data-target='${targetId}']`);
		if (btn) btn.classList.add("active");
		document.getElementById(targetId).classList.add("active");
	};

	document.querySelectorAll(".tab-btn").forEach(tab => {
		tab.addEventListener("click", () => switchTab(tab.dataset.target));
	});

	// 3. Theme Toggle
	const toggleBtn = document.getElementById("themeToggle");
	const root = document.documentElement;
	if (toggleBtn) {
		toggleBtn.addEventListener("click", () => {
			const next = root.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
			if (next === 'dark') root.setAttribute('data-theme', 'dark');
			else root.removeAttribute('data-theme');
			localStorage.setItem('sam_theme', next);
		});
	}

	// 4. PNR AJAX HANDLER
	const pnrForm = document.getElementById("pnrForm");
	if (pnrForm) {
		pnrForm.addEventListener("submit", function(e) {
			e.preventDefault();

			const pnrInput = document.getElementById("pnrInput").value;
			const container = document.getElementById("pnrResultContainer");
			const btnText = document.getElementById("pnrBtnText");
			const loader = document.getElementById("pnrLoader");

			if (!pnrInput) return;

			btnText.textContent = "Checking...";
			loader.classList.remove("hidden");
			container.classList.add("hidden");

			fetch(`PnrStatus?pnr=${pnrInput}`, {
				method: 'GET',
				headers: { 'X-Requested-With': 'XMLHttpRequest' }
			})
				.then(response => response.json())
				.then(json => {
					if (json.status === 'success') {
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
		let statusHTML = pnrStatus === 'FLUSHED'
			? `<div class="status-banner banner-flushed"><i class="ri-history-line"></i> Journey Completed / Chart Flushed</div>`
			: `<div class="status-banner banner-live"><div style="width:8px; height:8px; background:#10b981; border-radius:50%; box-shadow:0 0 8px #10b981; animation:pulse 2s infinite"></div> Live Status</div>`;

		let paxRows = '';
		if (ticket.associatedPassenger) {
			ticket.associatedPassenger.forEach((p, i) => {
				let badgeClass = 'st-wl';
				if (p.ticketStatus.includes('CNF')) badgeClass = 'st-cnf';
				else if (p.ticketStatus.includes('CAN')) badgeClass = 'st-can';

				let seat = p.seatMetaData ? `${p.seatMetaData.coachNo}/${p.seatMetaData.seatNumber}` : '-';

				paxRows += `
                    <tr>
                        <td>${i + 1}</td>
                        <td style="font-weight:600">${p.name}</td>
                        <td><span class="st-badge ${badgeClass}">${p.ticketStatus}</span></td>
                        <td>${seat}</td>
                    </tr>
                `;
			});
		}

		return `
            <div class="ticket-card">
                <div class="ticket-header">
                    <div>
                        <span style="font-size:0.9rem; opacity:0.8; letter-spacing:1px;">PNR: ${ticket.pnrNumber}</span>
                        <div class="train-name">${ticket.trainName} <span style="opacity:0.7">(${ticket.trainId})</span></div>
                    </div>
                </div>
                ${statusHTML}
                <div class="ticket-body">
                    <div class="route-row">
                        <span class="station">${ticket.sourceArr}</span>
                        <i class="ri-arrow-right-line" style="color:var(--muted)"></i>
                        <span class="station">${ticket.destinationArr}</span>
                    </div>
                    <div style="font-size:0.9rem; color:var(--muted); margin-bottom:20px; display:flex; gap:15px;">
                        <span><i class="ri-calendar-line"></i> ${ticket.bookingDate}</span>
                        <span><i class="ri-armchair-line"></i> ${ticket.className}</span>
                    </div>
                    <table class="pax-table">
                        <thead><tr><th>#</th><th>Name</th><th>Status</th><th>Seat</th></tr></thead>
                        <tbody>${paxRows}</tbody>
                    </table>
                </div>
            </div>
        `;
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
});