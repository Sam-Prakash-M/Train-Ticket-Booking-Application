document.addEventListener("DOMContentLoaded", () => {
  const loader = document.getElementById("pageLoader");
  const modal = document.getElementById("scheduleModal");
  const closeBtn = modal.querySelector(".close-btn");
  const scheduleTableBody = modal.querySelector("tbody");
  const scheduleTrainName = document.getElementById("scheduleTrainName");

  const showLoader = (show) => loader.classList.toggle("hidden", !show);
  const showModal = (show) => modal.classList.toggle("hidden", !show);

  // ✅ Initial loading animation
  showLoader(true);
  setTimeout(() => showLoader(false), 400);

  // ✅ Train Schedule Modal
  document.querySelectorAll(".schedule-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const card = btn.closest(".train-card");
      const routes = JSON.parse(card.dataset.routes);
      const title = card.querySelector("h2").textContent;
      scheduleTrainName.textContent = title;

      scheduleTableBody.innerHTML = routes.map(r =>
        `<tr>
          <td>${r.station}</td>
          <td>${r.arrival}</td>
          <td>${r.departure}</td>
          <td>${r.distance_from_start}</td>
        </tr>`
      ).join("");
      showModal(true);
    });
  });

  closeBtn.addEventListener("click", () => showModal(false));
  modal.addEventListener("click", e => { if (e.target === modal) showModal(false); });

  // ✅ Handle seat class selection & enable Book Now
  document.querySelectorAll(".coach-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const trainId = btn.dataset.train;
      const coachClass = btn.dataset.class;
      const distance = parseFloat(btn.dataset.distance);
      const detailsDiv = document.getElementById(`details-${trainId}`);
      const card = btn.closest(".train-card");
      const bookBtn = card.querySelector(".book-now");
      const source = card.querySelector(".route").textContent.split("➜")[0].trim();
      const destination = card.querySelector(".route").textContent.split("➜")[1].trim();

      const trainData = seatAvailabilityData[trainId];
      if (!trainData) return;

      const matchingCoaches = Object.values(trainData).filter(c => c.class === coachClass);
      const totalSeats = matchingCoaches.reduce((sum, c) => sum + (c.available_seats || 0), 0);
      const farePerKm = fareMap[trainId]?.[coachClass];
      const totalFare = (farePerKm * distance).toFixed(2);

      detailsDiv.innerHTML = `
        <p><strong>Class:</strong> ${coachClass}</p>
        <p><strong>Total Coaches:</strong> ${matchingCoaches.length}</p>
        <p><strong>Total Available Seats:</strong> ${totalSeats}</p>
        <p><strong>Fare:</strong> ₹${totalFare}</p>
        <p style="color:#777;font-size:0.9rem;">(Distance: ${distance} km × ₹${farePerKm}/km)</p>
      `;
      detailsDiv.classList.remove("hidden");

      // ✅ Enable Book Now
      bookBtn.disabled = false;
      // ✅ Store data in button for redirect
      bookBtn.dataset.selectedClass = coachClass;
      bookBtn.dataset.totalFare = totalFare;
      bookBtn.dataset.source = source;
      bookBtn.dataset.destination = destination;
    });
  });

  // ✅ Redirect to BookingServlet on click
  document.querySelectorAll(".book-now").forEach(btn => {
    btn.addEventListener("click", () => {
      const trainId = btn.dataset.train;
      const selectedClass = btn.dataset.selectedClass;
      const totalFare = btn.dataset.totalFare;
      const source = btn.dataset.source;
      const destination = btn.dataset.destination;

      if (!selectedClass) {
        alert("Please select a class before booking!");
        return;
      }

      showLoader(true);

      setTimeout(() => {
        showLoader(false);

        // ✅ Create a hidden form to send POST request
        const form = document.createElement("form");
        form.method = "POST";
        form.action = "BookingServlet";

        const params = {
          trainId,
          classType: selectedClass,
          source,
          destination,
          fare: totalFare
        };

        // Dynamically append hidden input fields
        for (const key in params) {
          const input = document.createElement("input");
          input.type = "hidden";
          input.name = key;
          input.value = params[key];
          form.appendChild(input);
        }

        document.body.appendChild(form);
        form.submit(); // 🚀 Send via POST
      }, 600);
    });
  });

});
