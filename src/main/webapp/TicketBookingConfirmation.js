document.addEventListener("DOMContentLoaded", () => {

	// 1. Theme Logic
	const toggle = document.getElementById("themeToggle");
	if (toggle) {
		toggle.addEventListener("click", () => {
			const root = document.documentElement;
			const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
			root.setAttribute("data-theme", next);
			localStorage.setItem("sam_theme", next);
		});
	}

	// 2. Print Logic
	const printBtn = document.getElementById("printBtn");
	if (printBtn) {
		printBtn.addEventListener("click", () => {
			window.print();
		});
	}

	// 3. Copy PNR Logic
	const copyBtn = document.getElementById("copyPnr");
	if (copyBtn) {
		copyBtn.addEventListener("click", () => {
			const pnr = document.getElementById("pnrText").textContent;
			navigator.clipboard.writeText(pnr).then(() => {
				showToast("PNR Copied!");
			});
		});
	}

	// 4. Toast
	function showToast(msg) {
		const t = document.getElementById("toast");
		t.textContent = msg;
		t.classList.add("show");
		setTimeout(() => t.classList.remove("show"), 2000);
	}
});