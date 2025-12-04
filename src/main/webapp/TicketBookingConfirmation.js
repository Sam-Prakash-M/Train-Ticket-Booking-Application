(function() {
	function $(q) { return document.querySelector(q); }

	// Snack notification
	function showSnack(msg) {
		const s = document.createElement('div');
		s.className = 'snack';
		s.innerText = msg;
		document.body.appendChild(s);

		setTimeout(() => {
			s.style.opacity = '0';
			s.addEventListener('transitionend', () => s.remove());
		}, 1800);
	}

	// Add ripple effect to buttons
	document.addEventListener("click", function(e) {
		if (e.target.classList.contains("btn")) {
			const circle = document.createElement("span");
			circle.classList.add("ripple");
			e.target.appendChild(circle);

			setTimeout(() => circle.remove(), 600);
		}
	});

	document.addEventListener('DOMContentLoaded', () => {
		const copyBtn = $('#copyPnrBtn');
	
		if (copyBtn) {
			copyBtn.addEventListener('click', () => {
				const pnr = $('#pnrValue')?.textContent?.trim();
				if (!pnr) return showSnack("PNR not found");

				navigator.clipboard.writeText(pnr)
					.then(() => showSnack("PNR copied!"))
					.catch(() => showSnack("Copy failed"));
			});
		}

		
	});
})();
