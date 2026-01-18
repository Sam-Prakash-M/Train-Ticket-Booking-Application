// DOM Elements
const emailInput = document.getElementById("emailInput");
const getOtpBtn = document.getElementById("getOtpBtn");
const verifyBtn = document.getElementById("verifyBtn");
const otpInputs = document.querySelectorAll(".otp-box");
const errorBanner = document.getElementById("errorMessage");
const errorText = document.getElementById("errorText");

document.addEventListener("DOMContentLoaded", () => {
    // Auto-Focus OTP Logic
    otpInputs.forEach((input, index) => {
        input.addEventListener('input', (e) => {
            if(e.target.value.length === 1 && index < otpInputs.length - 1) {
                otpInputs[index + 1].focus();
            }
        });
        input.addEventListener('keydown', (e) => {
            if(e.key === "Backspace" && e.target.value === "" && index > 0) {
                otpInputs[index - 1].focus();
            }
        });
    });
});

// --- ACTION 1: SEND EMAIL OTP ---
getOtpBtn.addEventListener("click", () => {
    const email = emailInput.value.trim();
    if(!validateEmail(email)) {
        showError("Please enter a valid email address.");
        return;
    }

    setLoading(getOtpBtn, true);
    hideError();

    // CALL YOUR JAVA BACKEND
    const params = new URLSearchParams();
    params.append("action", "sendOtp");
    params.append("email", email);

    fetch("ForgotUsernameServlet", {
        method: "POST",
        body: params
    })
    .then(response => response.json())
    .then(data => {
        setLoading(getOtpBtn, false);
        if(data.status === "success") {
            // UI Transition
            document.getElementById("step-email").classList.add("hidden");
            document.getElementById("step-otp").classList.remove("hidden");
            document.getElementById("displayEmail").innerText = email;
            startTimer();
        } else {
            showError(data.message || "Failed to send OTP.");
        }
    })
    .catch(err => {
        setLoading(getOtpBtn, false);
        console.error(err);
        showError("Server error. Please check your connection.");
    });
});

// --- ACTION 2: VERIFY OTP ---
verifyBtn.addEventListener("click", () => {
    const otp = Array.from(otpInputs).map(i => i.value).join('');
    const email = emailInput.value.trim();

    if(otp.length !== 6) {
        showError("Please enter the 6-digit code.");
        return;
    }

    setLoading(verifyBtn, true);
    hideError();

    // CALL YOUR JAVA BACKEND
    const params = new URLSearchParams();
    params.append("action", "verifyOtp");
    params.append("email", email);
    params.append("otp", otp);

    fetch("ForgotUsernameServlet", {
        method: "POST",
        body: params
    })
    .then(response => response.json())
    .then(data => {
        setLoading(verifyBtn, false);
        if(data.status === "success") {
            // UI Transition
            document.getElementById("retrievedUsername").innerText = data.username;
            document.getElementById("step-otp").classList.add("hidden");
            document.getElementById("step-success").classList.remove("hidden");
            document.querySelector(".card-header").classList.add("hidden");
        } else {
            showError(data.message || "Invalid OTP.");
        }
    })
    .catch(err => {
        setLoading(verifyBtn, false);
        showError("Server verification failed.");
    });
});

// --- UTILITIES ---
document.getElementById("editEmailBtn").addEventListener("click", () => {
    document.getElementById("step-otp").classList.add("hidden");
    document.getElementById("step-email").classList.remove("hidden");
});

function setLoading(btn, isLoading) {
    const text = btn.querySelector(".btn-text");
    const spinner = btn.querySelector(".spinner");
    if(isLoading) {
        text.classList.add("hidden");
        spinner.classList.remove("hidden");
        btn.disabled = true;
    } else {
        text.classList.remove("hidden");
        spinner.classList.add("hidden");
        btn.disabled = false;
    }
}

function showError(msg) {
    errorText.innerText = msg;
    errorBanner.classList.remove("hidden");
}

function hideError() {
    errorBanner.classList.add("hidden");
}

function validateEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function startTimer() {
    let timeLeft = 60;
    const timerElem = document.getElementById("timer");
    const interval = setInterval(() => {
        if(timeLeft <= 0) {
            clearInterval(interval);
            timerElem.innerText = "0";
        } else {
            timerElem.innerText = timeLeft;
            timeLeft--;
        }
    }, 1000);
}

window.copyUsername = () => {
    const text = document.getElementById("retrievedUsername").innerText;
    navigator.clipboard.writeText(text);
    alert("Username copied!");
};