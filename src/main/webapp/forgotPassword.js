// ===================================
// MODERN FORGOT PASSWORD JAVASCRIPT
// ===================================

// DOM Elements
const usernameInput = document.getElementById("usernameInput");
const emailInput = document.getElementById("emailInput");
const checkUserBtn = document.getElementById("checkUserBtn");
const getOtpBtn = document.getElementById("getOtpBtn");
const verifyBtn = document.getElementById("verifyBtn");
const resetBtn = document.getElementById("resetBtn");
const otpInputs = document.querySelectorAll(".otp-box");
const newPassInput = document.getElementById("newPassword");
const confirmPassInput = document.getElementById("confirmPassword");
const errorBanner = document.getElementById("errorMessage");
const errorText = document.getElementById("errorText");
const headerText = document.getElementById("headerText");

// Progress tracking
let currentStep = 1;

// ===================================
// INITIALIZATION
// ===================================
document.addEventListener("DOMContentLoaded", () => {
    initializeTheme();
    initializeOTPInputs();
    initializePasswordToggles();
    initializePasswordStrength();
});

// ===================================
// THEME MANAGEMENT
// ===================================
function initializeTheme() {
    const toggleBtn = document.getElementById("themeToggle");
    const root = document.documentElement;
    
    if (toggleBtn) {
        toggleBtn.addEventListener("click", () => {
            const currentTheme = root.getAttribute('data-theme');
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            
            if (newTheme === 'dark') {
                root.setAttribute('data-theme', 'dark');
            } else {
                root.removeAttribute('data-theme');
            }
            
            localStorage.setItem('sam_theme', newTheme);
        });
    }
}

// ===================================
// OTP INPUT MANAGEMENT
// ===================================
function initializeOTPInputs() {
    otpInputs.forEach((input, index) => {
        // Only allow single digit
        input.addEventListener('input', (e) => {
            let value = e.target.value;
            
            // Remove non-numeric characters
            value = value.replace(/[^0-9]/g, '');
            
            // Keep only first character
            if (value.length > 1) {
                value = value.charAt(0);
            }
            
            e.target.value = value;
            
            // Auto-focus next input
            if (value.length === 1 && index < otpInputs.length - 1) {
                otpInputs[index + 1].focus();
            }
        });
        
        // Handle backspace navigation
        input.addEventListener('keydown', (e) => {
            if (e.key === "Backspace") {
                if (e.target.value === "" && index > 0) {
                    otpInputs[index - 1].focus();
                    otpInputs[index - 1].value = "";
                }
            }
        });
        
        // Handle paste event
        input.addEventListener('paste', (e) => {
            e.preventDefault();
            const pastedData = e.clipboardData.getData('text').trim();
            
            // Only process if it's 6 digits
            if (/^\d{6}$/.test(pastedData)) {
                pastedData.split('').forEach((char, i) => {
                    if (otpInputs[i]) {
                        otpInputs[i].value = char;
                    }
                });
                // Focus last input
                otpInputs[5].focus();
            }
        });

        // Prevent non-numeric input
        input.addEventListener('keypress', (e) => {
            if (!/[0-9]/.test(e.key)) {
                e.preventDefault();
            }
        });
    });
}

// ===================================
// PASSWORD VISIBILITY TOGGLE
// ===================================
function initializePasswordToggles() {
    const toggleButtons = document.querySelectorAll('.toggle-password');
    
    toggleButtons.forEach(button => {
        button.addEventListener('click', () => {
            const targetId = button.getAttribute('data-target');
            const input = document.getElementById(targetId);
            
            if (input) {
                const isPassword = input.type === 'password';
                input.type = isPassword ? 'text' : 'password';
                button.classList.toggle('active');
            }
        });
    });
}

// ===================================
// PASSWORD STRENGTH INDICATOR
// ===================================
function initializePasswordStrength() {
    if (newPassInput) {
        newPassInput.addEventListener('input', () => {
            const password = newPassInput.value;
            const strength = calculatePasswordStrength(password);
            updateStrengthIndicator(strength);
        });
    }
}

function calculatePasswordStrength(password) {
    let strength = 0;
    
    if (password.length >= 6) strength += 25;
    if (password.length >= 10) strength += 25;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength += 25;
    if (/\d/.test(password)) strength += 15;
    if (/[^a-zA-Z\d]/.test(password)) strength += 10;
    
    return Math.min(strength, 100);
}

function updateStrengthIndicator(strength) {
    const strengthFill = document.querySelector('.strength-fill');
    const strengthText = document.querySelector('.strength-text');
    
    if (strengthFill && strengthText) {
        strengthFill.style.width = `${strength}%`;
        
        if (strength < 30) {
            strengthText.textContent = 'Weak password';
            strengthFill.style.background = 'var(--danger)';
        } else if (strength < 60) {
            strengthText.textContent = 'Medium password';
            strengthFill.style.background = '#f59e0b';
        } else {
            strengthText.textContent = 'Strong password';
            strengthFill.style.background = 'var(--success)';
        }
    }
}

// ===================================
// PROGRESS STEP MANAGEMENT
// ===================================
function updateProgressStep(step) {
    const steps = document.querySelectorAll('.progress-step');
    steps.forEach((stepEl, index) => {
        if (index + 1 <= step) {
            stepEl.classList.add('active');
        } else {
            stepEl.classList.remove('active');
        }
    });
    currentStep = step;
}

function showStep(stepId) {
    const steps = document.querySelectorAll('.step-container');
    steps.forEach(step => step.classList.remove('active'));
    
    const targetStep = document.getElementById(stepId);
    if (targetStep) {
        targetStep.classList.add('active');
    }
}

// ===================================
// ACTION 1: CHECK USERNAME
// ===================================
checkUserBtn.addEventListener("click", () => {
    const username = usernameInput.value.trim();
    
    if (!username) {
        showError("Please enter your username.");
        return;
    }
    
    setLoading(checkUserBtn, true);
    hideError();
    
    const params = new URLSearchParams();
    params.append("action", "checkUsername");
    params.append("username", username);
    
    fetch("ForgotPasswordServlet", {
        method: "POST",
        body: params
    })
    .then(response => response.json())
    .then(data => {
        setLoading(checkUserBtn, false);
        
        if (data.status === "success") {
            emailInput.value = data.email;
            showStep("step-email");
            updateProgressStep(2);
            headerText.textContent = "Verify your registered email.";
        } else {
            showError(data.message || "Username not found.");
        }
    })
    .catch(err => {
        setLoading(checkUserBtn, false);
        showError("Server error. Please try again.");
        console.error("Error:", err);
    });
});

// ===================================
// ACTION 2: SEND EMAIL OTP
// ===================================
getOtpBtn.addEventListener("click", () => {
    const email = emailInput.value.trim();
    
    setLoading(getOtpBtn, true);
    hideError();
    
    const params = new URLSearchParams();
    params.append("action", "sendOtp");
    params.append("email", email);
    
    fetch("ForgotPasswordServlet", {
        method: "POST",
        body: params
    })
    .then(response => response.json())
    .then(data => {
        setLoading(getOtpBtn, false);
        
        if (data.status === "success") {
            showStep("step-otp");
            updateProgressStep(2);
            document.getElementById("displayEmail").textContent = email;
            headerText.textContent = "Enter the verification code.";
            startTimer();
            
            // Focus first OTP input
            setTimeout(() => {
                otpInputs[0].focus();
            }, 300);
        } else {
            showError(data.message || "Failed to send OTP.");
        }
    })
    .catch(err => {
        setLoading(getOtpBtn, false);
        showError("Server error. Please try again.");
        console.error("Error:", err);
    });
});

// ===================================
// ACTION 3: VERIFY OTP
// ===================================
verifyBtn.addEventListener("click", () => {
    const otp = Array.from(otpInputs).map(i => i.value).join('');
    const email = emailInput.value.trim();
    
    if (otp.length !== 6) {
        showError("Please enter the complete 6-digit code.");
        return;
    }
    
    if (!/^\d{6}$/.test(otp)) {
        showError("OTP must contain only numbers.");
        return;
    }
    
    setLoading(verifyBtn, true);
    hideError();
    
    const params = new URLSearchParams();
    params.append("action", "verifyOtp");
    params.append("email", email);
    params.append("otp", otp);
    
    fetch("ForgotPasswordServlet", {
        method: "POST",
        body: params
    })
    .then(response => response.json())
    .then(data => {
        setLoading(verifyBtn, false);
        
        if (data.status === "success") {
            showStep("step-reset");
            updateProgressStep(3);
            headerText.textContent = "Create a new strong password.";
            
            // Focus password input
            setTimeout(() => {
                newPassInput.focus();
            }, 300);
        } else {
            showError(data.message || "Invalid or expired OTP.");
            // Shake OTP inputs on error
            otpInputs.forEach(input => {
                input.style.animation = 'shake 0.4s ease';
                setTimeout(() => {
                    input.style.animation = '';
                }, 400);
            });
        }
    })
    .catch(err => {
        setLoading(verifyBtn, false);
        showError("Verification failed. Please try again.");
        console.error("Error:", err);
    });
});

// ===================================
// ACTION 4: RESET PASSWORD
// ===================================
resetBtn.addEventListener("click", () => {
    const email = emailInput.value.trim();
    const newPass = newPassInput.value;
    const confirmPass = confirmPassInput.value;
    
    // Validation
    if (newPass.length < 6) {
        showError("Password must be at least 6 characters long.");
        newPassInput.focus();
        return;
    }
    
    if (newPass !== confirmPass) {
        showError("Passwords do not match!");
        confirmPassInput.focus();
        return;
    }
    
    setLoading(resetBtn, true);
    hideError();
    
    const params = new URLSearchParams();
    params.append("action", "resetPassword");
    params.append("email", email);
    params.append("newPassword", newPass);
    
    fetch("ForgotPasswordServlet", {
        method: "POST",
        body: params
    })
    .then(response => response.json())
    .then(data => {
        setLoading(resetBtn, false);
        
        if (data.status === "success") {
            showStep("step-success");
            document.getElementById("cardHeader").style.display = 'none';
            updateProgressStep(3);
        } else {
            showError(data.message || "Failed to update password.");
        }
    })
    .catch(err => {
        setLoading(resetBtn, false);
        showError("Server error during password update.");
        console.error("Error:", err);
    });
});

// ===================================
// UTILITY FUNCTIONS
// ===================================

function setLoading(btn, isLoading) {
    const content = btn.querySelector(".btn-content");
    const loader = btn.querySelector(".btn-loader");
    
    if (isLoading) {
        if (content) content.style.opacity = '0';
        if (loader) loader.classList.remove("hidden");
        btn.disabled = true;
    } else {
        if (content) content.style.opacity = '1';
        if (loader) loader.classList.add("hidden");
        btn.disabled = false;
    }
}

function showError(msg) {
    errorText.textContent = msg;
    errorBanner.classList.remove("hidden");
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        hideError();
    }, 5000);
}

function hideError() {
    errorBanner.classList.add("hidden");
}

function startTimer() {
    let timeLeft = 60;
    const timerElem = document.getElementById("timer");
    
    const interval = setInterval(() => {
        if (timeLeft <= 0) {
            clearInterval(interval);
            timerElem.textContent = "0";
            // Could enable resend button here
        } else {
            timerElem.textContent = timeLeft;
            timeLeft--;
        }
    }, 1000);
}

// ===================================
// KEYBOARD SHORTCUTS
// ===================================
document.addEventListener('keydown', (e) => {
    // Enter key submits current step
    if (e.key === 'Enter' && !e.shiftKey) {
        const activeStep = document.querySelector('.step-container.active');
        if (!activeStep) return;
        
        if (activeStep.id === 'step-username') {
            e.preventDefault();
            checkUserBtn.click();
        } else if (activeStep.id === 'step-email') {
            e.preventDefault();
            getOtpBtn.click();
        } else if (activeStep.id === 'step-otp') {
            e.preventDefault();
            verifyBtn.click();
        } else if (activeStep.id === 'step-reset') {
            e.preventDefault();
            resetBtn.click();
        }
    }
});