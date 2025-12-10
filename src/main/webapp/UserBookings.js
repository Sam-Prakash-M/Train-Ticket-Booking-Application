document.addEventListener("DOMContentLoaded", () => {
    const loader = document.getElementById("pageLoader");
    const main = document.getElementById("mainContent");

    setTimeout(() => {
        loader.style.display = "none";
        main.classList.remove("hidden");
    }, 1000);
});

function showTab(tabId, btn) {

    // Remove active class from all tabs
    document.querySelectorAll(".tab").forEach(tab => {
        tab.classList.remove("active");
    });

    // Hide all tab contents
    document.querySelectorAll(".tab-content").forEach(content => {
        content.classList.remove("active");
    });

    // Activate clicked tab
    btn.classList.add("active");

    // Show correct content
    document.getElementById(tabId).classList.add("active");
}

function toggleDetails(card) {
    card.classList.toggle("open");
}