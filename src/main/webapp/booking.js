document.addEventListener("DOMContentLoaded", () => {

    /* ---------------------------------------------
       BASIC SETUP
    --------------------------------------------- */
    const loader = document.getElementById("pageLoader");
    const modal = document.getElementById("scheduleModal");
    const closeBtn = modal.querySelector(".close-btn");
    const scheduleTableBody = modal.querySelector("tbody");
    const scheduleTrainName = document.getElementById("scheduleTrainName");

    const showLoader = s => loader.classList.toggle("hidden", !s);
    const showModal = s => modal.classList.toggle("hidden", !s);

    showLoader(true);
    setTimeout(() => showLoader(false), 400);


    /* ---------------------------------------------
       TRAIN SCHEDULE POPUP
    --------------------------------------------- */
    document.querySelectorAll(".schedule-btn").forEach(btn => {
        btn.addEventListener("click", () => {

            const card = btn.closest(".train-card");
            const routes = JSON.parse(card.dataset.routes);
            const title = card.querySelector("h2").textContent;

            scheduleTrainName.textContent = title;

            scheduleTableBody.innerHTML = routes.map(r => `
                <tr>
                    <td>${r.station}</td>
                    <td>${r.arrival}</td>
                    <td>${r.departure}</td>
                    <td>${r.distance_from_start}</td>
                </tr>
            `).join("");

            showModal(true);
        });
    });

    closeBtn.addEventListener("click", () => showModal(false));
    modal.addEventListener("click", e => { if (e.target === modal) showModal(false); });



    /* ---------------------------------------------
       RENDER CLASS DETAILS (Supports RAC/WL)
    --------------------------------------------- */
    function renderClassDetails(trainId, coachClass, distance, card) {

        const detailsDiv = document.getElementById(`details-${trainId}`);
        const availability = seatAvailabilityData[trainId][coachClass];
        const farePerKm = fareMap[trainId]?.[coachClass] || 0;
        const totalFare = (farePerKm * distance).toFixed(2);

        detailsDiv.classList.remove("hidden");

        // CASE 1: Normal coaches (array)
        if (Array.isArray(availability)) {

            let totalSeats = availability.reduce((sum, c) =>
                sum + (c.available_seats || 0), 0
            );

            detailsDiv.innerHTML = `
                <h3>${coachClass}</h3>
                <p><strong>Total Seats Available:</strong> ${totalSeats}</p>
                <p><strong>Fare:</strong> ₹${totalFare}</p>
                <p style="font-size: 0.9rem; color:#666;">
                    Distance: ${distance} km × ₹${farePerKm}/km
                </p>
            `;

            enableBookNow(card, coachClass, totalFare);
            return;
        }

        // CASE 2: RAC
        if (availability && availability.status === "RAC") {
            detailsDiv.innerHTML = `
                <h3>${coachClass} - RAC</h3>
                <p><strong>RAC Seats Available:</strong> ${availability.available_seats}</p>
                <p><strong>Fare:</strong> ₹${totalFare}</p>
            `;
            enableBookNow(card, coachClass, totalFare);
            return;
        }

        // CASE 3: WL
        if (availability && availability.status === "WL") {
            detailsDiv.innerHTML = `
                <h3>${coachClass} - Waiting List</h3>
                <p><strong>WL Positions Available:</strong> ${availability.available_seats}</p>
                <p><strong>Fare:</strong> ₹${totalFare}</p>
            `;
            enableBookNow(card, coachClass, totalFare);
            return;
        }

        // CASE 4: Not available
        detailsDiv.innerHTML = `
            <h3>${coachClass}</h3>
            <p>No tickets available</p>
        `;
        disableBookNow(card);
    }


    function enableBookNow(card, coachClass, fare) {
        const btn = card.querySelector(".book-now");
        btn.disabled = false;
        btn.dataset.selectedClass = coachClass;
        btn.dataset.totalFare = fare;
    }

    function disableBookNow(card) {
        const btn = card.querySelector(".book-now");
        btn.disabled = true;
        delete btn.dataset.selectedClass;
    }

    /* ---------------------------------------------
       COACH BUTTON CLICK
    --------------------------------------------- */
    document.querySelectorAll(".coach-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const trainId = btn.dataset.train;
            const coachClass = btn.dataset.class;
            const distance = parseFloat(btn.dataset.distance);
            const card = btn.closest(".train-card");

            renderClassDetails(trainId, coachClass, distance, card);
        });
    });



    /* ---------------------------------------------
       BOOK NOW → BookingServlet
    --------------------------------------------- */
    document.querySelectorAll(".book-now").forEach(btn => {
        btn.addEventListener("click", () => {

            const card = btn.closest(".train-card");

            const trainId = btn.dataset.train;
            const trainName = card.dataset.trainName;
            const selectedClass = btn.dataset.selectedClass;
            const totalFare = btn.dataset.totalFare;

            const [source, destination] =
                card.querySelector(".route").textContent.split("➜").map(s => s.trim());

            const routes = JSON.parse(card.dataset.routes);

            // FIXED: Case-insensitive matching
            const srcStop = routes.find(r =>
                r.station.toLowerCase() === source.toLowerCase()
            );
            const destStop = routes.find(r =>
                r.station.toLowerCase() === destination.toLowerCase()
            );

            const params = {
                trainId,
                trainName,
                classType: selectedClass,
                source,
                destination,
                fare: totalFare,
                sourceArrival: srcStop?.arrival || "-",
                sourceDeparture: srcStop?.departure || "-",
                destinationArrival: destStop?.arrival || "-",
                destinationDeparture: destStop?.departure || "-"
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



    /* ---------------------------------------------
       SWAP BUTTON
    --------------------------------------------- */
    const from = document.getElementById("searchFrom");
    const to = document.getElementById("searchTo");
    const swapBtn = document.getElementById("swapBtn");

    swapBtn.addEventListener("click", () => {
        let tmp = from.value;
        from.value = to.value;
        to.value = tmp;

        swapBtn.classList.add("swap-anim");
        setTimeout(() => swapBtn.classList.remove("swap-anim"), 300);
    });



    /* ---------------------------------------------
       DATE NAVIGATION
    --------------------------------------------- */
    const prevBtn = document.getElementById("prevDay");
    const nextBtn = document.getElementById("nextDay");
    const dateInput = document.getElementById("searchDate");
    const modifyForm = document.getElementById("modifySearchForm");

	if (prevBtn && nextBtn && dateInput && modifyForm) {

	     function updateButtons() {
	         const min = new Date(dateInput.min);
	         const max = new Date(dateInput.max);
	         const current = new Date(dateInput.value);

	         prevBtn.disabled = current <= min;
	         nextBtn.disabled = current >= max;

	         prevBtn.classList.toggle("disabled", prevBtn.disabled);
	         nextBtn.classList.toggle("disabled", nextBtn.disabled);
	     }

	     function changeDate(offset) {
	         const min = new Date(dateInput.min);
	         const max = new Date(dateInput.max);
	         let current = new Date(dateInput.value);

	         current.setDate(current.getDate() + offset);

	         if (current < min) current = min;
	         if (current > max) current = max;

	         dateInput.value = current.toISOString().split("T")[0];
	         updateButtons();
	         modifyForm.submit();
	     }

	     prevBtn.addEventListener("click", () => changeDate(-1));
	     nextBtn.addEventListener("click", () => changeDate(1));

	     updateButtons();
	 }
});
