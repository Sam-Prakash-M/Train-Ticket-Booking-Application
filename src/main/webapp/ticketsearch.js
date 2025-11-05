document.addEventListener("DOMContentLoaded", () => {
  const loader = document.getElementById("pageLoader");
  setTimeout(() => {
    loader.style.opacity = "0";
    setTimeout(() => loader.remove(), 800);
  }, 2000);

  const tabs = document.querySelectorAll(".tab");
  const sections = document.querySelectorAll(".search-container");

  tabs.forEach((tab, index) => {
    tab.addEventListener("click", () => {
      tabs.forEach(t => t.classList.remove("active"));
      tab.classList.add("active");
      sections.forEach(s => s.classList.remove("active"));
      sections[index].classList.add("active");
    });
  });

  // Swap "From" and "To"
  const swap = document.getElementById("swapStations");
  swap.addEventListener("click", () => {
    const from = document.getElementById("source");
    const to = document.getElementById("destination");
    [from.value, to.value] = [to.value, from.value];
  });

  // Flatpickr init
  document.querySelectorAll("input[id*='Date']").forEach(input => {
    flatpickr(input, {
      dateFormat: "Y-m-d",
      minDate: input.dataset.min,
      maxDate: input.dataset.max,
    });
  });

  // Dark / Light Mode Toggle
  const toggle = document.getElementById("themeToggle");
  toggle.addEventListener("click", () => {
    document.body.classList.toggle("dark");
    toggle.textContent = document.body.classList.contains("dark") ? "☀️" : "🌙";
  });
  
  document.querySelector('.swap-icon').addEventListener('click', () => {
    const fromInput = document.getElementById('from');
    const toInput = document.getElementById('to');

    fromInput.classList.add('swap-anim');
    toInput.classList.add('swap-anim');

    const temp = fromInput.value;
    fromInput.value = toInput.value;
    toInput.value = temp;

    setTimeout(() => {
      fromInput.classList.remove('swap-anim');
      toInput.classList.remove('swap-anim');
    }, 400);
  });
});



