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

	// 4. Modal Logic (JSON Data Structure)
	    const modal = document.getElementById("cancelModal");
	    const container = document.getElementById("passengerCheckboxes");
	    const pnrInput = document.getElementById("cancelPnrInput");

	    window.openCancelModal = (pnr) => {
	        pnrInput.value = pnr;
	        container.innerHTML = ""; 

	        const card = document.getElementById(`card-${pnr}`);
	        if(!card) return;

	        const passengers = card.querySelectorAll(".p-row");
	        let validCount = 0;

	        passengers.forEach(row => {
	            const name = row.dataset.name;
	            const age = row.dataset.age;
	            const gender = row.dataset.gender;
	            const status = row.dataset.status;
	            const seat = row.dataset.seat;
				// NEW: Retrieve Class Type (dataset converts 'data-classType' to 'classtype')
				            const classType = row.dataset.classtype;

	            if (status && !status.includes("CAN")) {
	                validCount++;
	                
	                // 1. Construct JSON Object
	                const passengerObj = {
	                    name: name,
	                    age: age,
	                    gender: gender,
	                    status: status,
	                    seatNumber: seat,
	                    pnr: pnr, // Useful context for backend
					    classType: classType // <--- ADDED HERE
	                };

	                // 2. Stringify to JSON
	                // We escape single quotes just in case names contain them
	                const jsonString = JSON.stringify(passengerObj).replace(/'/g, "&apos;");

	                const label = document.createElement("label");
	                label.className = "p-checkbox-label";
	                
	                // 3. Set value='JSON_STRING'
	                label.innerHTML = `
	                    <input type="checkbox" name="selectedPassengers" value='${jsonString}'>
	                    <div style="flex:1">
	                        <span class="p-name">${name}</span>
	                        <small style="display:block; color:var(--muted); font-size:0.75rem">
	                            ${age}/${gender} • Seat: ${seat}
	                        </small>
	                    </div>
	                    <span class="status-pill ${status.includes('CNF')?'cnf':'wl'}">${status}</span>
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