document.addEventListener("DOMContentLoaded", function() {

	// 1. Initialize Flatpickr (Date Picker)
	// Using a configuration that matches the dark/light theme logic if desired
	const today = new Date();
	const maxDate = new Date();
	maxDate.setDate(today.getDate() + 90); // 90 days advance booking

	const fp = flatpickr("#journeyDate", {
		dateFormat: "Y-m-d",
		minDate: today,
		maxDate: maxDate,
		disableMobile: "true", // Forces the custom UI even on mobile
		onOpen: function(selectedDates, dateStr, instance) {
			// Add a class for styling overrides if needed
			instance.calendarContainer.classList.add("modern-calendar");
		}
	});

	// 2. Station Swap Logic
	const swapBtn = document.getElementById("swapStations");
	const fromInput = document.getElementById("fromStation");
	const toInput = document.getElementById("toStation");

	if (swapBtn && fromInput && toInput) {
		swapBtn.addEventListener("click", () => {
			// Animate rotation via CSS class toggle if desired, 
			// or rely on the :hover state, but here we swap values
			const temp = fromInput.value;
			fromInput.value = toInput.value;
			toInput.value = temp;

			// Simple spin animation trigger
			swapBtn.style.transform = "rotate(180deg)";
			setTimeout(() => { swapBtn.style.transform = ""; }, 300);
		});
	}

	// 3. Theme Toggle (Dark/Light)
	const themeToggle = document.getElementById('themeToggle');
	const root = document.documentElement;
	const icon = themeToggle ? themeToggle.querySelector('i') : null;

	function applyTheme(theme) {
		if (theme === 'dark') {
			root.setAttribute('data-theme', 'dark');
			if (icon) icon.className = 'ri-sun-line';
		} else {
			root.removeAttribute('data-theme');
			if (icon) icon.className = 'ri-moon-clear-line';
		}
		localStorage.setItem('sam_theme', theme);
	}

	// Load saved theme
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	applyTheme(savedTheme);

	if (themeToggle) {
		themeToggle.addEventListener('click', () => {
			const current = root.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
			const next = current === 'dark' ? 'light' : 'dark';
			applyTheme(next);
		});
	}

	// 4. Form Submission & Auth Check
	const form = document.getElementById('searchForm');
	const authNotice = document.getElementById('authNotice');
	const goLogin = document.getElementById('goLogin');
	const toast = document.getElementById('toast');
	const toastMsg = document.getElementById('toastMsg');

	function showToast(message) {
		if (!toast) return;
		toastMsg.textContent = message;
		toast.classList.remove('hidden');
		setTimeout(() => {
			toast.classList.add('hidden');
		}, 4000);
	}

	if (form) {
		form.addEventListener('submit', function(e) {
			e.preventDefault();

			// Get configuration injected from JSP
			const appConfig = window.__APP || {};
			const isLoggedIn = appConfig.loggedIn;

			if (!isLoggedIn) {
				// Show inline notice
				if (authNotice) authNotice.classList.remove('hidden');

				// Show toast
				showToast("Please log in to search for trains.");

				// Highlight login button in notice
				if (goLogin) goLogin.focus();

				// Scroll to notice if needed
				authNotice.scrollIntoView({ behavior: 'smooth', block: 'center' });
				return;
			}

			// Proceed if logged in
			const action = appConfig.searchAction || "searchtrains";
			const formData = new FormData(form);
			const params = new URLSearchParams(formData).toString();

			// Add a small loading state to button
			const btn = document.getElementById('searchBtn');
			const originalText = btn.innerHTML;
			btn.innerHTML = '<i class="ri-loader-4-line ri-spin"></i> Searching...';
			btn.disabled = true;

			// Simulate slight network delay for UX then redirect
			setTimeout(() => {
				window.location.href = `${action}?${params}`;
			}, 500);
		});
	}

	// Login Redirect from Notice
	if (goLogin) {
		goLogin.addEventListener('click', () => {
			window.location.href = window.__APP ? window.__APP.loginUrl : 'login.jsp';
		});
	}

	// Update Year
	const yearSpan = document.getElementById('year');
	if (yearSpan) yearSpan.textContent = new Date().getFullYear();

});