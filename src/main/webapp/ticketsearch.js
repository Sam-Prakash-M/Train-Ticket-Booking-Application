document.addEventListener("DOMContentLoaded", () => {

	// 1. Loader Logic (Same as before)
	const loader = document.getElementById("pageLoader");
	setTimeout(() => {
		if (loader) {
			loader.style.opacity = "0";
			loader.style.visibility = "hidden";
			setTimeout(() => loader.remove(), 500);
		}
	}, 1200);

	// 2. Tab Switching
	const tabs = document.querySelectorAll(".tab-btn");
	const sections = document.querySelectorAll(".tab-content");

	tabs.forEach(tab => {
		tab.addEventListener("click", () => {
			tabs.forEach(t => t.classList.remove("active"));
			tab.classList.add("active");
			sections.forEach(s => s.classList.remove("active"));
			const targetId = tab.dataset.target;
			document.getElementById(targetId).classList.add("active");
		});
	});

	// 3. Theme Toggle (Robust)
	const toggleBtn = document.getElementById("themeToggle");
	const root = document.documentElement;

	const updateIcon = (theme) => {
		if (!toggleBtn) return;
		const icon = toggleBtn.querySelector("i");
		if (icon) {
			icon.className = theme === 'dark' ? "ri-sun-line" : "ri-moon-clear-line";
		}
	};

	// Init
	const currentTheme = localStorage.getItem('sam_theme') || 'light';
	updateIcon(currentTheme);

	if (toggleBtn) {
		toggleBtn.addEventListener("click", () => {
			const current = root.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
			const next = current === 'dark' ? 'light' : 'dark';

			if (next === 'dark') root.setAttribute('data-theme', 'dark');
			else root.removeAttribute('data-theme');

			localStorage.setItem('sam_theme', next);
			updateIcon(next);
		});
	}

	// 4. Swap Stations
	const swapBtn = document.getElementById("swapStations");
	if (swapBtn) {
		swapBtn.addEventListener("click", () => {
			const from = document.getElementById("source");
			const to = document.getElementById("destination");

			swapBtn.style.transform = "rotate(180deg)";

			const temp = from.value;
			from.value = to.value;
			to.value = temp;

			setTimeout(() => { swapBtn.style.transform = ""; }, 300);
		});
	}

	// 5. Date Picker
	const dateInputs = document.querySelectorAll("input[id*='Date']");
	dateInputs.forEach(input => {
		flatpickr(input, {
			dateFormat: "Y-m-d",
			minDate: input.dataset.min,
			maxDate: input.dataset.max,
			disableMobile: "true"
		});
	});
});