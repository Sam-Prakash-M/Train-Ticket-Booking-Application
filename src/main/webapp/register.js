document.addEventListener("DOMContentLoaded", () => {

	// 1. Theme Logic
	const toggle = document.getElementById("themeToggle");
	if (toggle) {
		toggle.addEventListener("click", () => {
			const root = document.documentElement;
			const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
			root.setAttribute("data-theme", next);
			localStorage.setItem("sam_theme", next);
			// Re-render logo/icons if needed
		});
	}

	// 2. Hide Loader
	setTimeout(() => {
		const loader = document.getElementById("pageLoader");
		if (loader) loader.style.display = 'none';
	}, 800);

	// ------------------------------------------
	//  PHONE INPUT LOGIC
	// ------------------------------------------
	const phoneInput = document.querySelector("#mobile");
	const errorMsg = document.querySelector("#error-msg");
	const validMsg = document.querySelector("#valid-msg");
	const hiddenInput = document.querySelector("#fullMobileNumber");

	if (phoneInput) {
		// Initialize Plugin
		const iti = window.intlTelInput(phoneInput, {
			utilsScript: "https://cdn.jsdelivr.net/npm/intl-tel-input@18.2.1/build/js/utils.js",
			initialCountry: "auto",
			geoIpLookup: function(callback) {
				fetch("https://ipapi.co/json")
					.then(function(res) { return res.json(); })
					.then(function(data) { callback(data.country_code); })
					.catch(function() { callback("in"); }); // Default to India on error
			},
			separateDialCode: true,
			preferredCountries: ["in", "us", "gb"]
		});

		const reset = () => {
			phoneInput.classList.remove("error");
			errorMsg.innerHTML = "";
			errorMsg.classList.add("hide");
			validMsg.classList.add("hide");
		};

		// Validate on change
		phoneInput.addEventListener('blur', () => {
			reset();
			if (phoneInput.value.trim()) {
				if (iti.isValidNumber()) {
					validMsg.classList.remove("hide");
					// Save full number (e.g., +919876543210) to hidden input
					hiddenInput.value = iti.getNumber();
				} else {
					phoneInput.classList.add("error");
					const errorCode = iti.getValidationError();
					// Basic error mapping
					const errorMap = ["Invalid number", "Invalid country code", "Too short", "Too long", "Invalid number"];
					errorMsg.innerHTML = errorMap[errorCode] || "Invalid number";
					errorMsg.classList.remove("hide");
					hiddenInput.value = ""; // Clear invalid data
				}
			}
		});

		// Reset on keyup
		phoneInput.addEventListener('change', reset);
		phoneInput.addEventListener('keyup', reset);

		// Ensure full number is set on form submit
		document.getElementById("regForm").addEventListener("submit", function(e) {
			if (!iti.isValidNumber()) {
				e.preventDefault();
				phoneInput.focus();
				errorMsg.innerHTML = "Valid phone number required";
				errorMsg.classList.remove("hide");
			} else {
				hiddenInput.value = iti.getNumber();
			}
		});
	}

	// ------------------------------------------
	//  PASSWORD TOGGLE
	// ------------------------------------------
	window.togglePassword = () => {
		const input = document.getElementById("password");
		const icon = document.getElementById("eyeIcon");
		if (input.type === "password") {
			input.type = "text";
			icon.className = "ri-eye-line";
		} else {
			input.type = "password";
			icon.className = "ri-eye-off-line";
		}
	};
});