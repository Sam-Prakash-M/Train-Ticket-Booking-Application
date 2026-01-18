document.addEventListener("DOMContentLoaded", () => {

	// 1. Theme Logic (Fixed Toggle)
	const toggle = document.getElementById("themeToggle");
	if (toggle) {
		toggle.addEventListener("click", () => {
			const root = document.documentElement;
			const current = root.getAttribute("data-theme");
			const next = current === "dark" ? "light" : "dark";
			root.setAttribute("data-theme", next);
			localStorage.setItem("sam_theme", next);
		});
	}

	// 2. Phone Input Logic
	const phoneInput = document.querySelector("#mobile");
	const hiddenInput = document.querySelector("#fullMobileNumber");
	const errorMsg = document.querySelector("#error-msg");
	const validMsg = document.querySelector("#valid-msg");
	let iti;

	if (phoneInput) {
		iti = window.intlTelInput(phoneInput, {
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

		// Initialize value if exists
		if (phoneInput.value.trim() !== "") {
			hiddenInput.value = phoneInput.value;
		}

		const reset = () => {
			phoneInput.classList.remove("error");
			errorMsg.innerHTML = "";
			errorMsg.classList.add("hide");
			validMsg.classList.add("hide");
		};

		phoneInput.addEventListener('blur', () => {
			if (phoneInput.disabled) return; // Don't validate in view mode
			reset();
			if (phoneInput.value.trim()) {
				if (iti.isValidNumber()) {
					validMsg.classList.remove("hide");
					hiddenInput.value = iti.getNumber();
				} else {
					phoneInput.classList.add("error");
					errorMsg.classList.remove("hide");
					errorMsg.innerHTML = "Invalid";
				}
			}
		});

		// Form Submit Intercept
		document.getElementById("profileForm").addEventListener("submit", function(e) {
			if (!iti.isValidNumber()) {
				e.preventDefault();
				phoneInput.focus();
				errorMsg.classList.remove("hide");
				errorMsg.innerHTML = "Required";
			} else {
				hiddenInput.value = iti.getNumber();
			}
		});
	}

	// 3. EDIT MODE LOGIC
	window.enableEditMode = () => {
		document.getElementById("inpName").disabled = false;
		document.getElementById("inpEmail").disabled = false;
		document.getElementById("mobile").disabled = false;

		// Show Actions
		const actions = document.getElementById("editActions");
		actions.classList.add("active");

		// Hide Edit Pencil
		document.getElementById("editTrigger").style.display = "none";

		// Remove 'view-mode' styling class
		document.querySelector(".profile-form").classList.remove("view-mode");
	};

	window.cancelEditMode = () => {
		// Reload page to reset data or manually revert
		location.reload();
	};

	// 4. Modal Logic
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

	// 5. Toast
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