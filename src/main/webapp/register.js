document.addEventListener("DOMContentLoaded", () => {
    
    // 1. Theme Logic
    const toggle = document.getElementById("themeToggle");
    const root = document.documentElement;

    if(toggle) {
        toggle.addEventListener("click", () => {
            const current = root.getAttribute("data-theme") === "dark" ? "dark" : "light";
            const next = current === "dark" ? "light" : "dark";
            
            root.setAttribute("data-theme", next);
            localStorage.setItem("sam_theme", next);
            updateIcon(next);
        });
        
        // Init icon
        const saved = localStorage.getItem("sam_theme") || "light";
        updateIcon(saved);
    }

    function updateIcon(theme) {
        const i = toggle.querySelector("i");
        if(i) i.className = theme === 'dark' ? 'ri-sun-line' : 'ri-moon-line';
    }

    // 2. Loader
    setTimeout(() => {
        const l = document.getElementById("pageLoader");
        if(l) {
            l.style.opacity = '0';
            setTimeout(() => l.remove(), 500);
        }
    }, 1000);

    // 3. Password Toggle
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

    // 4. Form Validation
    window.validateForm = () => {
        const username = document.getElementById("username").value;
        const pass = document.getElementById("password").value;

        if(username.includes(" ")) {
            alert("Username cannot contain spaces.");
            return false;
        }
        if(pass.length < 6) {
            alert("Password must be at least 6 characters.");
            return false;
        }
        return true;
    };

});