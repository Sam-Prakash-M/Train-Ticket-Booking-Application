document.addEventListener("DOMContentLoaded", () => {
    // Theme Logic
    const toggle = document.getElementById("themeToggle");
    if(toggle) {
        toggle.addEventListener("click", () => {
            const root = document.documentElement;
            const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
            root.setAttribute("data-theme", next);
            localStorage.setItem("sam_theme", next);
        });
    }
});

// Initialize Cashfree SDK
const cashfree = Cashfree({
    mode: "sandbox" // Change to "production" for live
});

function startPayment() {
    const initialState = document.getElementById("initialState");
    const processingState = document.getElementById("processingState");
    const statusText = document.querySelector(".status-text");

    // Switch UI to Processing
    initialState.classList.add("hidden");
    processingState.classList.remove("hidden");

    // Trigger Cashfree Checkout
    let checkoutOptions = {
        paymentSessionId: window.cfSessionId,
        redirectTarget: "_self" // Redirects whole page to bank
    };

    cashfree.checkout(checkoutOptions).then(function(result) {
        if(result.error){
            // Handle Error: Revert UI
            initialState.classList.remove("hidden");
            processingState.classList.add("hidden");
            alert("Payment Error: " + result.error.message);
        }
        if(result.redirect){
            console.log("Redirecting to bank...");
            statusText.innerText = "Redirecting to Bank...";
        }
    });
}