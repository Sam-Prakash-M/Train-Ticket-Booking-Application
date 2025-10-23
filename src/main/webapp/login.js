// Hide loader after startup
window.addEventListener("load", () => {
  setTimeout(() => {
    document.getElementById("loader").classList.add("hidden");
  }, 2000); // loader visible for 2 seconds
});

// Theme toggle
function toggleTheme() {
  const body = document.body;
  const icon = document.querySelector(".theme-toggle .icon");

  if (body.classList.contains("dark-mode")) {
    body.classList.replace("dark-mode", "light-mode");
    icon.textContent = "☀️";
  } else {
    body.classList.replace("light-mode", "dark-mode");
    icon.textContent = "🌙";
  }
}
/**
 * 
 */