document.addEventListener("DOMContentLoaded", () => {

	// 1. Loader Removal
	setTimeout(() => {
		const loader = document.getElementById("appLoader");
		if (loader) {
			loader.style.opacity = '0';
			setTimeout(() => loader.remove(), 600);
		}
	}, 1500);

	// 2. Navbar Scroll Effect
	const nav = document.getElementById("mainNav");
	window.addEventListener("scroll", () => {
		if (window.scrollY > 50) nav.classList.add("scrolled");
		else nav.classList.remove("scrolled");
	});

	// 3. Theme Logic
	const toggle = document.getElementById("themeToggle");
	const root = document.documentElement;

	if (toggle) {
		toggle.addEventListener("click", () => {
			const current = root.getAttribute("data-theme") === "dark" ? "dark" : "light";
			const next = current === "dark" ? "light" : "dark";

			root.setAttribute("data-theme", next);
			localStorage.setItem("sam_theme", next);

			const i = toggle.querySelector("i");
			i.className = next === 'dark' ? 'ri-sun-line' : 'ri-moon-clear-line';
		});
	}

	// 4. Date Picker
	const dateInput = document.getElementById("travelDate");
	if (dateInput && window.flatpickr) {
		const today = new Date();
		const maxDate = new Date();
		maxDate.setDate(today.getDate() + 90);

		flatpickr(dateInput, {
			dateFormat: "Y-m-d",
			minDate: "today",
			maxDate: maxDate,
			disableMobile: "true"
		});
	}

	// 5. Swap Logic
	const swapBtn = document.getElementById("swapBtn");
	if (swapBtn) {
		swapBtn.addEventListener("click", () => {
			const from = document.getElementById("fromStation");
			const to = document.getElementById("toStation");
			const temp = from.value;
			from.value = to.value;
			to.value = temp;

			swapBtn.style.transform = "rotate(180deg)";
			setTimeout(() => swapBtn.style.transform = "", 300);
		});
	}
});