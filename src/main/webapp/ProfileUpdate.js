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

	// 2. Phone Input Logic
	const phoneInput = document.querySelector("#mobile");
	const hiddenInput = document.querySelector("#fullMobileNumber");
	const errorMsg = document.querySelector("#error-msg");
	const validMsg = document.querySelector("#valid-msg");

	if (phoneInput) {
		// Init with existing value handling
		const iti = window.intlTelInput(phoneInput, {
			utilsScript: "https://cdn.jsdelivr.net/npm/intl-tel-input@18.2.1/build/js/utils.js",
			initialCountry: "auto",
			geoIpLookup: function(callback) {
				fetch("https://ipapi.co/json")
					.then(function(res) { return res.json(); })
					.then(function(data) { callback(data.country_code); })
					.catch(function() { callback("in"); });
			},
			separateDialCode: true
		});

		// Pre-fill hidden input on load if value exists
		if (phoneInput.value.trim() !== "") {
			hiddenInput.value = phoneInput.value;
			// NOTE: intl-tel-input might need setNumber to format correctly
			// iti.setNumber(phoneInput.value); 
		}

		const reset = () => {
			phoneInput.classList.remove("error");
			errorMsg.innerHTML = "";
			errorMsg.classList.add("hide");
			validMsg.classList.add("hide");
		};

		phoneInput.addEventListener('blur', () => {
			reset();
			if (phoneInput.value.trim()) {
				if (iti.isValidNumber()) {
					validMsg.classList.remove("hide");
					hiddenInput.value = iti.getNumber();
				} else {
					phoneInput.classList.add("error");
					errorMsg.classList.remove("hide");
					errorMsg.innerHTML = "Invalid Number";
				}
			}
		});

		// Form Submit Intercept
		document.getElementById("profileForm").addEventListener("submit", function(e) {
			if (!iti.isValidNumber()) {
				e.preventDefault();
				phoneInput.focus();
				errorMsg.classList.remove("hide");
				errorMsg.innerHTML = "Valid Phone Required";
			} else {
				hiddenInput.value = iti.getNumber();
			}
		});
	}

	// 3. Modal Logic (Password)
	window.openPasswordModal = () => {
		document.getElementById("passwordModal").classList.add("show");
		document.getElementById("passwordModal").classList.remove("hidden");
	};

	window.closePasswordModal = () => {
		document.getElementById("passwordModal").classList.remove("show");
		setTimeout(() => {
			document.getElementById("passwordModal").classList.add("hidden");
		}, 300);
	};

	// 4. Global Toast Function
	window.showToast = (msg, isError) => {
		const t = document.getElementById("toast");
		t.innerHTML = isError
			? `<i class="ri-error-warning-fill" style="color:var(--danger)"></i> ${msg}`
			: `<i class="ri-checkbox-circle-fill" style="color:var(--success)"></i> ${msg}`;
		t.className = isError ? "toast show error" : "toast show success";
		setTimeout(() => {
			t.classList.remove("show");
		}, 3500);
	};
});