document.addEventListener("DOMContentLoaded", () => {
  const WEEK = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
  const WEEK_SHORT = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];

  const loader = document.getElementById("pageLoader");
  const showLoader = (on) => loader.classList.toggle("hidden", !on);

  // Initial loader while page paints
  showLoader(true);
  setTimeout(() => showLoader(false), 350);

  // ====== Build Day Strip for each train card ======
  document.querySelectorAll(".train-card").forEach(card => {
    const trainId = card.dataset.trainId;
    const daysContainer = card.querySelector(".day-strip");
    const available = new Set(JSON.parse(card.dataset.availableDays || "[]"));
    const bookBtn = card.querySelector(".book-now");

    let selectedDay = null;
    const todayFull = WEEK[new Date().getDay()];

    WEEK.forEach((full, idx) => {
      const pill = document.createElement("button");
      pill.type = "button";
      pill.className = "day-pill";
      pill.textContent = WEEK_SHORT[idx];

      const isAvailable = available.has(full);
      if (!isAvailable) pill.classList.add("disabled");

      // Default-select today's day if available; otherwise first available later
      if (selectedDay === null && isAvailable && full === todayFull) {
        pill.classList.add("selected");
        selectedDay = full;
      }

      pill.addEventListener("click", () => {
        if (pill.classList.contains("disabled")) return;
        daysContainer.querySelectorAll(".day-pill").forEach(p => p.classList.remove("selected"));
        pill.classList.add("selected");
        selectedDay = full;
        bookBtn.disabled = false;
      });

      daysContainer.appendChild(pill);
    });

    // If we didn't select anything (today unavailable), select first available
    if (!selectedDay) {
      const firstAvail = WEEK.find(d => available.has(d));
      if (firstAvail) {
        const idx = WEEK.indexOf(firstAvail);
        daysContainer.children[idx].classList.add("selected");
        selectedDay = firstAvail;
        bookBtn.disabled = false;
      } else {
        // No days available at all
        bookBtn.disabled = true;
      }
    }

    // Book Now click → show loader and (for now) alert (wire to your servlet later)
    bookBtn.addEventListener("click", () => {
      if (!selectedDay) return;
      showLoader(true);
      setTimeout(() => {
        showLoader(false);
        alert(`Proceeding to book ${trainId} on ${selectedDay}`);
        // TODO: window.location.href = `BookServlet?trainId=${encodeURIComponent(trainId)}&day=${encodeURIComponent(selectedDay)}`;
      }, 600);
    });

    // Other Dates (demo: quick loader)
    const otherDatesBtn = card.querySelector(".other-dates");
    otherDatesBtn?.addEventListener("click", () => {
      showLoader(true);
      setTimeout(() => showLoader(false), 400);
    });
  });

  // ====== Your existing coach selection logic (kept, with small tweaks) ======
  const coachButtons = document.querySelectorAll(".coach-btn");

  coachButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      const trainId = btn.dataset.train;
      const coachClass = btn.dataset.class;
      const distance = parseFloat(btn.dataset.distance);
      const detailsDiv = document.getElementById(`details-${trainId}`);

      const trainData = seatAvailabilityData[trainId];
      if (!trainData) return;

      // Sum all seats for the chosen class across coaches
      const matchingCoaches = Object.values(trainData).filter(c => c.class === coachClass);
      let totalSeats = 0;
      matchingCoaches.forEach(c => totalSeats += (c.available_seats || 0));

      const farePerKm = fareMap[trainId]?.[coachClass];
      if (matchingCoaches.length > 0 && typeof farePerKm === "number") {
        const totalFare = (farePerKm * distance).toFixed(2);
        detailsDiv.innerHTML = `
          <p><strong>Class:</strong> ${coachClass}</p>
          <p><strong>Total Coaches:</strong> ${matchingCoaches.length}</p>
          <p><strong>Total Available Seats:</strong> ${totalSeats}</p>
          <p><strong>Fare:</strong> ₹${totalFare}</p>
          <p style="font-size:0.9rem;color:#777;">(Distance: ${distance} km × ₹${farePerKm}/km)</p>
        `;
      } else {
        detailsDiv.innerHTML = `<p style="color:#999;">No data found for ${coachClass}</p>`;
      }

      detailsDiv.classList.remove("hidden");
      detailsDiv.classList.add("show");
      detailsDiv.scrollIntoView({ behavior: "smooth", block: "center" });
    });
  });
});
