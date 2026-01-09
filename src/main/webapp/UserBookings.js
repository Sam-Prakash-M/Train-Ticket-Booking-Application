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
		setTimeout(() => {
			btn.innerHTML = originalHtml;
			btn.disabled = false;
			showToast("PNR Status Updated", false);
		}, 1000);
	};

	// ---------------------------------------------
	//  CANCEL MODAL LOGIC (Updated)
	// ---------------------------------------------
	const modal = document.getElementById("cancelModal");
	const container = document.getElementById("passengerCheckboxes");
	const pnrInput = document.getElementById("cancelPnrInput");
	const cancelForm = document.getElementById("cancelForm");

	window.openCancelModal = (pnr) => {
		pnrInput.value = pnr;
		container.innerHTML = "";

		const card = document.getElementById(`card-${pnr}`);
		if (!card) return;

		const passengers = card.querySelectorAll(".p-row");
		let validCount = 0;

		passengers.forEach(row => {
			const name = row.dataset.name;
			const age = row.dataset.age;
			const gender = row.dataset.gender;
			const status = row.dataset.status; // e.g. "CNF", "WL", "CAN"
			const seat = row.dataset.seat;
			const classType = row.dataset.classtype;

			// Only allow cancelling if NOT already cancelled
			if (status && !status.includes("CAN") && !status.includes("Can")) {
				validCount++;

				const passengerObj = {
					name: name, age: age, gender: gender, status: status,
					seatNumber: seat, classType: classType, pnr: pnr
				};

				// JSON string for value
				const jsonString = JSON.stringify(passengerObj).replace(/'/g, "&apos;");

				const label = document.createElement("label");
				label.className = "p-checkbox-label";
				label.innerHTML = `
		                    <input type="checkbox" name="selectedPassengers" value='${jsonString}'>
		                    <div style="flex:1">
		                        <span class="p-name">${name}</span>
		                        <small style="display:block; color:var(--muted); font-size:0.75rem">
		                            ${age}/${gender} • Seat: ${seat}
		                        </small>
		                    </div>
		                    <span class="status-pill ${status.includes('CNF') ? 'cnf' : 'wl'}">${status}</span>
		                `;
				container.appendChild(label);
			}
		});

		// REPLACED ALERT WITH TOAST
		if (validCount === 0) {
			showToast("All passengers are already cancelled.", true);
			return;
		}

		modal.classList.add("show");
		modal.classList.remove("hidden");
	};

	window.closeModal = () => {
		modal.classList.remove("show");
		setTimeout(() => modal.classList.add("hidden"), 300);
	};


	// ---------------------------------------------
	//  AJAX FORM SUBMISSION (No Reload)
	// ---------------------------------------------
	if (cancelForm) {
		cancelForm.addEventListener("submit", async (e) => {
			e.preventDefault();

			const checkedBoxes = container.querySelectorAll('input[type="checkbox"]:checked');
			if (checkedBoxes.length === 0) {
				showToast("Please select at least one passenger.", true);
				return;
			}

			const confirmBtn = cancelForm.querySelector('.btn-modal.confirm');
			const originalText = confirmBtn.innerText;
			confirmBtn.innerText = "Processing...";
			confirmBtn.disabled = true;

			const formData = new FormData(cancelForm);
			// FIX: Convert FormData to URLSearchParams to send as x-www-form-urlencoded
			// This ensures request.getParameter() works in the Servlet without @MultipartConfig
			const urlEncodedData = new URLSearchParams(formData);

			try {
				const response = await fetch("CancelTicketServlet", {
					method: "POST",
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
					},
					body: urlEncodedData
				});

				if (response.ok) {
					showToast("Ticket(s) Cancelled Successfully!", false);
					closeModal();
					// 1. Immediate Visual Update (Greys out the row)
					                updateUIOnCancel(pnrInput.value, checkedBoxes);
					                
					                // 2. Reload after short delay to sync with backend
					                setTimeout(() => {
					                    location.reload();
					                }, 1500);
				} else {
					// NEW: Read the error text sent from Servlet
					const errorText = await response.text();
					console.error("Server Error Details:", errorText); // Check Console

					// Extract meaningful message from HTML error page if possible, or show generic
					let cleanMsg = "Cancellation Failed.";
					if (errorText.includes("Server Error:")) {
						cleanMsg = errorText.split("Server Error:")[1].split("\n")[0];
					}

					showToast(cleanMsg, true);
				}

			} catch (error) {
				console.error("Network Error:", error);
				showToast("Network Error. Try again.", true);
			} finally {
				confirmBtn.innerText = originalText;
				confirmBtn.disabled = false;
			}
		});
	}

	// Function to update UI locally after success
	function updateUIOnCancel(pnr, checkedInputs) {
	        const card = document.getElementById(`card-${pnr}`);
	        if(!card) return;

	        checkedInputs.forEach(input => {
	            const data = JSON.parse(input.value);
	            const nameToFind = data.name;

	            const rows = card.querySelectorAll(".p-row");
	            rows.forEach(row => {
	                if(row.dataset.name === nameToFind) {
	                    const pill = row.querySelector(".status-pill");
	                    if(pill) {
	                        pill.className = "status-pill can"; 
	                        pill.innerText = "CANCELLED";
	                    }
	                    row.dataset.status = "CANCELLED";
	                    // Visual feedback
	                    row.style.opacity = "0.5";
	                    row.style.pointerEvents = "none";
	                }
	            });
	        });
	    }



	function showToast(msg, isError = false) {
		const t = document.getElementById("toast");

		// Add Icon based on type
		const icon = isError ? '<i class="ri-error-warning-fill"></i>' : '<i class="ri-checkbox-circle-fill"></i>';

		t.innerHTML = `${icon} <span>${msg}</span>`;
		t.className = isError ? "toast show error" : "toast show success";

		setTimeout(() => {
			t.classList.remove("show");
		}, 3500);
	}

	// 5. Rebook Logic
	window.rebookJourney = (source, dest) => {
		window.location.href = `RailwayApplication.jsp?fromStation=${source}&toStation=${dest}`;
	};
	// Close modal on outside click
	window.addEventListener("click", (e) => {
		if (e.target === modal) closeModal();
	});

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