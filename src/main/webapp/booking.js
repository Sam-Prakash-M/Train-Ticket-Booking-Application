document.addEventListener("DOMContentLoaded", () => {
    
    // ------------------------------------
    // 1. Theme Toggle & Persistence
    // ------------------------------------
    const themeToggle = document.getElementById('themeToggle');
    const root = document.documentElement;
    const themeIcon = themeToggle ? themeToggle.querySelector('i') : null;

    const updateIcon = (theme) => {
        if (!themeIcon) return;
        themeIcon.className = theme === 'dark' ? 'ri-sun-line' : 'ri-moon-clear-line';
    };

    // Set initial icon based on what script in <head> set
    updateIcon(root.getAttribute('data-theme') || 'light');

    if (themeToggle) {
        themeToggle.addEventListener('click', () => {
            const current = root.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
            const next = current === 'dark' ? 'light' : 'dark';
            
            if (next === 'dark') root.setAttribute('data-theme', 'dark');
            else root.removeAttribute('data-theme');
            
            localStorage.setItem('sam_theme', next);
            updateIcon(next);
        });
    }

    // ------------------------------------
    // 2. Loader Logic
    // ------------------------------------
    const loader = document.getElementById("pageLoader");
    const showLoader = (show) => loader.classList.toggle("hidden", !show);
    
    // Hide loader after slight delay for effect
    setTimeout(() => showLoader(false), 800);

    // ------------------------------------
    // 3. Modal Logic (Schedule)
    // ------------------------------------
    const modal = document.getElementById("scheduleModal");
    const modalBody = document.getElementById("scheduleTableBody");
    const modalTitle = document.getElementById("scheduleTrainName");
    const closeModal = modal ? modal.querySelector(".close-btn") : null;

    document.querySelectorAll(".schedule-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const card = btn.closest(".train-card");
            const routes = JSON.parse(card.dataset.routes);
            
            modalTitle.textContent = card.dataset.trainName + " Schedule";
            modalBody.innerHTML = routes.map(r => `
                <tr>
                    <td>${r.station}</td>
                    <td>${r.arrival}</td>
                    <td>${r.departure}</td>
                    <td>${r.distance_from_start}</td>
                </tr>
            `).join("");
            
            modal.classList.remove("hidden");
        });
    });

    if (closeModal) closeModal.addEventListener("click", () => modal.classList.add("hidden"));
    if (modal) modal.addEventListener("click", (e) => { if(e.target === modal) modal.classList.add("hidden"); });

    // ------------------------------------
    // 4. Seat Availability & Details
    // ------------------------------------
    const renderClassDetails = (trainId, coachClass, distance, card) => {
        const detailsPanel = document.getElementById(`details-${trainId}`);
        const contentDiv = detailsPanel.querySelector(".details-content");
        
        // Hide all other panels first (accordion style - optional)
        document.querySelectorAll(".details-panel").forEach(p => {
            if (p !== detailsPanel) p.classList.add("hidden");
        });

        detailsPanel.classList.remove("hidden");
        
        const availability = seatAvailabilityData[trainId]?.[coachClass];
        const farePerKm = fareMap[trainId]?.[coachClass] || 0;
        const totalFare = (farePerKm * distance).toFixed(2);

        let html = "";
        let canBook = false;

        if (!availability) {
            html = `<h3>${coachClass}</h3><p>Data not available.</p>`;
        } else if (Array.isArray(availability)) {
            // Normal Available
            const seats = availability.reduce((sum, c) => sum + (c.available_seats || 0), 0);
            html = `
                <h3>${coachClass}</h3>
                <p><strong>Available Seats:</strong> <span style="color:var(--primary); font-weight:700;">${seats}</span></p>
                <p><strong>Total Fare:</strong> ₹${totalFare}</p>
            `;
            canBook = seats > 0;
        } else if (availability.status === "RAC") {
            html = `
                <h3>${coachClass} (RAC)</h3>
                <p><strong>Seats:</strong> ${availability.available_seats}</p>
                <p><strong>Fare:</strong> ₹${totalFare}</p>
            `;
            canBook = true;
        } else if (availability.status === "WL") {
            html = `
                <h3 style="color:#f59e0b">${coachClass} (Waiting List)</h3>
                <p><strong>WL Position:</strong> ${availability.available_seats}</p>
                <p><strong>Fare:</strong> ₹${totalFare}</p>
            `;
            canBook = true;
        } else {
            html = `<h3>${coachClass}</h3><p>Booking Closed</p>`;
        }

        contentDiv.innerHTML = html;
        
        // Setup Book Button
        const bookBtn = detailsPanel.querySelector(".book-now");
        bookBtn.disabled = !canBook;
        bookBtn.dataset.selectedClass = coachClass;
        bookBtn.dataset.totalFare = totalFare;
    };

    document.querySelectorAll(".coach-btn").forEach(btn => {
        btn.addEventListener("click", function() {
            // Remove active style from siblings
            this.closest(".coach-grid").querySelectorAll(".coach-btn").forEach(b => b.style.borderColor = "");
            this.style.borderColor = "var(--primary)";
            
            const { train, class: cClass, distance } = this.dataset;
            renderClassDetails(train, cClass, distance, this.closest(".train-card"));
        });
    });

    // ------------------------------------
    // 5. Booking Redirect
    // ------------------------------------
    document.querySelectorAll(".book-now").forEach(btn => {
        btn.addEventListener("click", () => {
            const card = btn.closest(".train-card");
            const routes = JSON.parse(card.dataset.routes);
            
            // Get data
            const trainId = btn.dataset.train;
            const trainName = card.dataset.trainName;
            const selectedClass = btn.dataset.selectedClass;
            const totalFare = btn.dataset.totalFare;
            const travelDate = document.getElementById("searchDate").value;

            // Extract Source/Dest from input fields (more reliable than card text)
            const source = document.getElementById("searchFrom").value;
            const destination = document.getElementById("searchTo").value;

            // Find times
            const srcStop = routes.find(r => r.station.toLowerCase() === source.toLowerCase()) || {};
            const destStop = routes.find(r => r.station.toLowerCase() === destination.toLowerCase()) || {};

            // Construct form
            const params = {
                trainId, trainName, classType: selectedClass, source, destination, fare: totalFare, travelDate,
                sourceArrival: srcStop.arrival || "-",
                sourceDeparture: srcStop.departure || "-",
                destinationArrival: destStop.arrival || "-",
                destinationDeparture: destStop.departure || "-"
            };

            const form = document.createElement("form");
            form.method = "POST";
            form.action = "BookingServlet";
            
            Object.entries(params).forEach(([k, v]) => {
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = k;
                input.value = v;
                form.appendChild(input);
            });
            
            document.body.appendChild(form);
            form.submit();
        });
    });

    // ------------------------------------
    // 6. Utils: Swap & Date Nav
    // ------------------------------------
    const swapBtn = document.getElementById("swapBtn");
    if (swapBtn) {
        swapBtn.addEventListener("click", () => {
            const from = document.getElementById("searchFrom");
            const to = document.getElementById("searchTo");
            [from.value, to.value] = [to.value, from.value];
            
            swapBtn.style.transform = "rotate(180deg)";
            setTimeout(() => swapBtn.style.transform = "", 300);
        });
    }

    const prevBtn = document.getElementById("prevDay");
    const nextBtn = document.getElementById("nextDay");
    const dateInput = document.getElementById("searchDate");
    const form = document.getElementById("modifySearchForm");

    const changeDate = (days) => {
        const current = new Date(dateInput.value);
        current.setDate(current.getDate() + days);
        dateInput.value = current.toISOString().split('T')[0];
        form.submit();
    };

    if (prevBtn) {
        prevBtn.addEventListener("click", () => {
            if (new Date(dateInput.value) > new Date(dateInput.min)) changeDate(-1);
        });
    }
    if (nextBtn) {
        nextBtn.addEventListener("click", () => {
            if (new Date(dateInput.value) < new Date(dateInput.max)) changeDate(1);
        });
    }
});