document.addEventListener("DOMContentLoaded", () => {
    
    // Theme Logic
    const toggle = document.getElementById("themeToggle");
    if (toggle) {
        toggle.addEventListener("click", () => {
            const root = document.documentElement;
            const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
            root.setAttribute("data-theme", next);
            localStorage.setItem("sam_theme", next);
        });
    }

    // Auto-focus Input
    const input = document.querySelector('input[name="pnrInput"]');
    if(input && !input.value) {
        input.focus();
    }
});