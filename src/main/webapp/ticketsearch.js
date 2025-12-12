document.addEventListener("DOMContentLoaded", () => {

	// ----------------------------------------
	// 1. Loader Logic
	// ----------------------------------------
	const loader = document.getElementById("pageLoader");
	setTimeout(() => {
		if (loader) {
			loader.style.opacity = "0";
			loader.style.visibility = "hidden";
			// Remove from DOM after transition to free memory
			setTimeout(() => loader.remove(), 500);
		}
	}, 1200); // 1.2 seconds artificial load time

	// ----------------------------------------
	// 2. Tab Switching (Segmented Control)
	// ----------------------------------------
	const tabs = document.querySelectorAll(".tab-btn");
	const sections = document.querySelectorAll(".tab-content");

	tabs.forEach(tab => {
		tab.addEventListener("click", () => {
			// Remove active class from all tabs
			tabs.forEach(t => t.classList.remove("active"));
			// Add to clicked
			tab.classList.add("active");

			// Hide all sections
			sections.forEach(s => s.classList.remove("active"));

			// Show target section
			const targetId = tab.dataset.target;
			document.getElementById(targetId).classList.add("active");
		});
	});

	// ----------------------------------------
	// 3. Theme Persistence & Toggle
	// ----------------------------------------
	const toggleBtn = document.getElementById("themeToggle");
	const root = document.documentElement;
	const toggleIcon = toggleBtn ? toggleBtn.querySelector("i") : null;

	// Function to apply visual icon state
	const updateIcon = (theme) => {
		if (!toggleIcon) return;
		if (theme === 'dark') toggleIcon.className = "ri-sun-line"; // Show sun to switch to light
		else toggleIcon.className = "ri-moon-clear-line"; // Show moon to switch to dark
	};

	// Check current state on load
	const currentTheme = root.getAttribute('data-theme') || 'light';
	updateIcon(currentTheme);

	if (toggleBtn) {
		toggleBtn.addEventListener("click", () => {
			const current = root.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
			const next = current === 'dark' ? 'light' : 'dark';

			// Apply to DOM
			if (next === 'dark') root.setAttribute('data-theme', 'dark');
			else root.removeAttribute('data-theme');

			// Save to LocalStorage
			localStorage.setItem('sam_theme', next);

			// Update Icon
			updateIcon(next);
		});
	}

	// ----------------------------------------
	// 4. Station Swap Animation
	// ----------------------------------------
	const swapBtn = document.getElementById("swapStations");
	if (swapBtn) {
		swapBtn.addEventListener("click", () => {
			const from = document.getElementById("source");
			const to = document.getElementById("destination");

			// Visual feedback
			swapBtn.style.transform = "rotate(180deg)";

			// Swap values
			const temp = from.value;
			from.value = to.value;
			to.value = temp;

			// Reset rotation reset after animation
			setTimeout(() => { swapBtn.style.transform = ""; }, 300);
		});
	}

	// ----------------------------------------
	// 5. Flatpickr Init
	// ----------------------------------------
	const dateInputs = document.querySelectorAll("input[id*='Date']");
	dateInputs.forEach(input => {
		flatpickr(input, {
			dateFormat: "Y-m-d",
			minDate: input.dataset.min,
			maxDate: input.dataset.max,
			disableMobile: "true" // Force custom UI
		});
	});
});