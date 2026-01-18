document.addEventListener("DOMContentLoaded", () => {

	// 1. Theme Logic
	const toggle = document.getElementById("themeToggle");
	const root = document.documentElement;
	if (toggle) {
		toggle.addEventListener("click", () => {
			const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
			if (next === 'dark') root.setAttribute("data-theme", "dark");
			else root.removeAttribute("data-theme");
			localStorage.setItem("sam_theme", next);
		});
	}

	// 2. AJAX Fetch Logic
	const form = document.getElementById("trainForm");
	const container = document.getElementById("resultContainer");
	const input = document.getElementById("trainInput");
	const loader = document.getElementById("loader");
	const btnSpan = document.querySelector(".btn-search span");
	const arrow = document.querySelector(".btn-search i");

	form.addEventListener("submit", (e) => {
		e.preventDefault();
		const query = input.value.trim();
		if (!query) return;

		// UI Loading State
		btnSpan.textContent = "Searching...";
		arrow.classList.add("hidden");
		loader.classList.remove("hidden");
		container.classList.add("hidden");

		// Call Servlet
		fetch(`TrainData?trainInput=${query}`, {
			method: 'GET',
			headers: { 'X-Requested-With': 'XMLHttpRequest' }
		})
			.then(res => res.json())
			.then(json => {
				if (json.status === 'success') {
					container.innerHTML = renderTrainCard(json.data);
				} else {
					container.innerHTML = `
                    <div class="error-msg">
                        <i class="ri-error-warning-fill" style="font-size:2rem; margin-bottom:10px"></i>
                        <h3>Not Found</h3>
                        <p>${json.message}</p>
                    </div>
                `;
				}
				container.classList.remove("hidden");
			})
			.catch(err => {
				container.innerHTML = `<div class="error-msg">Server Error. Please try again.</div>`;
				container.classList.remove("hidden");
			})
			.finally(() => {
				btnSpan.textContent = "Search";
				arrow.classList.remove("hidden");
				loader.classList.add("hidden");
			});
	});

	// 3. HTML Generator for Train Card
	function renderTrainCard(data) {
		// A. Process Routes (Timeline)
		const sortedRoutes = data.routes.sort((a, b) => a.distanceFromStart - b.distanceFromStart);

		let timelineHTML = '<div class="timeline">';
		sortedRoutes.forEach((r, i) => {
			let type = '';
			if (i === 0) type = 'start';
			else if (i === sortedRoutes.length - 1) type = 'end';

			timelineHTML += `
                <div class="timeline-item ${type}">
                    <div class="timeline-dot"></div>
                    <span class="station-name">${r.stationName}</span>
                    <div class="time-info">
                        <span>Arr: ${r.arrivalTime}</span> • <span>Dep: ${r.departureTime}</span>
                        <span class="dist">${r.distanceFromStart} km</span>
                    </div>
                </div>
            `;
		});
		timelineHTML += '</div>';

		// B. Process Fares (Filter out 0 fares)
		const f = data.fareAmount;
		const fares = [
			{ name: "Sleeper (SL)", val: f.sleeper },
			{ name: "3rd AC (3A)", val: f.thirdAC },
			{ name: "2nd AC (2A)", val: f.secondAc },
			{ name: "1st AC (1A)", val: f.firstAc },
			{ name: "Chair Car", val: f.acChairCar },
			{ name: "2S Sitting", val: f.secondClassSitting }
		];

		let fareHTML = '<div class="fare-grid">';
		fares.forEach(fare => {
			if (fare.val > 0) {
				fareHTML += `
                    <div class="fare-card">
                        <span class="cls-name">${fare.name}</span>
                        <span class="price">₹${fare.val}</span>
                    </div>
                `;
			}
		});
		fareHTML += '</div>';

		// C. Available Days
		let daysHTML = data.availableDays.map(d => `<span class="day-pill">${d.substring(0, 3)}</span>`).join('');

		// D. Final HTML Construction
		return `
            <div class="train-card">
                <div class="card-header">
                    <div class="train-title">
                        <h2>${data.trainName}</h2>
                        <span class="train-id">${data.trainId}</span>
                    </div>
                    <div class="run-days">
                        ${daysHTML}
                    </div>
                </div>
                <div class="info-grid">
                    <div class="route-section">
                        <h4 style="margin-bottom:20px; font-family:'Space Grotesk'; text-transform:uppercase; color:var(--muted)">Route Timeline</h4>
                        ${timelineHTML}
                    </div>
                    <div class="fare-section">
                        <h4 style="margin-bottom:20px; font-family:'Space Grotesk'; text-transform:uppercase; color:var(--muted)">Base Fares Per km</h4>
                        ${fareHTML}
                    </div>
                </div>
            </div>
        `;
	}
});