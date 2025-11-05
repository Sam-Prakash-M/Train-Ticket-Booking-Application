document.addEventListener("DOMContentLoaded", () => {
  const loader = document.getElementById("loader");
  const container = document.getElementById("trainContainer");
  const prevBtn = document.getElementById("prevDate");
  const nextBtn = document.getElementById("nextDate");
  const dateSpan = document.getElementById("currentDate");

  const showLoader = () => { loader.style.display = "block"; container.style.display = "none"; };
  const hideLoader = () => { loader.style.display = "none"; container.style.display = "flex"; };

  prevBtn.addEventListener("click", () => navigateDate(-1));
  nextBtn.addEventListener("click", () => navigateDate(1));

  function navigateDate(offset) {
    const current = new Date(dateSpan.textContent);
    if (isNaN(current)) return;
    current.setDate(current.getDate() + offset);
    const formatted = current.toISOString().split('T')[0];

    showLoader();
    setTimeout(() => {
      window.location.href = `booking.jsp?date=${formatted}`;
    }, 500);
  }

  hideLoader();
});
