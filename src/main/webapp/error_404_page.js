document.addEventListener("DOMContentLoaded", () => {

	// Theme Logic
	const toggle = document.getElementById("themeToggle");
	if (toggle) {
		toggle.addEventListener("click", () => {
			const root = document.documentElement;
			const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
			root.setAttribute("data-theme", next);
			localStorage.setItem("sam_theme", next);
			updateIcon(next);
		});

		const current = document.documentElement.getAttribute("data-theme") || "light";
		updateIcon(current);
	}

	function updateIcon(theme) {
		const i = toggle.querySelector("i");
		if (i) i.className = theme === 'dark' ? 'ri-sun-line' : 'ri-moon-line';
	}
});