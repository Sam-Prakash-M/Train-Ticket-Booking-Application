document.addEventListener("DOMContentLoaded", () => {

	// Theme Logic
	const toggle = document.getElementById("themeToggle");
	const root = document.documentElement;

	if (toggle) {
		toggle.addEventListener("click", () => {
			const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
			root.setAttribute("data-theme", next);
			localStorage.setItem("sam_theme", next);
			updateIcon(next);
		});

		const current = root.getAttribute("data-theme") || "light";
		updateIcon(current);
	}

	function updateIcon(theme) {
		const i = toggle.querySelector("i");
		if (i) i.className = theme === 'dark' ? 'ri-sun-line' : 'ri-moon-line';
	}

	// Toggle Details Logic
	window.toggleDetails = () => {
		const stack = document.getElementById("stackTrace");
		const icon = document.getElementById("toggleIcon");

		if (stack.classList.contains("hidden")) {
			stack.classList.remove("hidden");
			icon.style.transform = "rotate(180deg)";
		} else {
			stack.classList.add("hidden");
			icon.style.transform = "rotate(0deg)";
		}
	};
});