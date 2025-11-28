const MAX_PASSENGERS = 6;

function getBerths(classCode) {
    switch (classCode) {
        case "SL":
        case "3A":
            return ["No Preference", "Lower", "Middle", "Upper", "Side Lower", "Side Upper"];
        case "2A":
            return ["No Preference", "Lower", "Upper", "Side Lower", "Side Upper"];
        case "1A":
            return ["No Preference", "Cabin", "Coupe"];
        case "CC":
            return ["No Preference", "Window", "Aisle", "Middle"];
        case "EC":
            return ["No Preference", "Window", "Aisle"];
        default:
            return ["No Preference"];
    }
}

function fillBerthDropdown(selectBox) {
    const berths = getBerths(TRAIN_CLASS_CODE);
    selectBox.innerHTML = berths.map(b => `<option>${b}</option>`).join("");
}

// Load berth for default row
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".berth-select").forEach(fillBerthDropdown);
    updateFare();
});

// Add Passenger
document.getElementById("addPassengerBtn").addEventListener("click", () => {

    let count = document.querySelectorAll(".passenger-row").length;

    if (count >= MAX_PASSENGERS) {
		showToast("Maximum 6 passengers allowed", "#e63946");
        return;
    }

    let row = document.createElement("div");
    row.className = "passenger-row";

    row.innerHTML = `
        <input type="text" name="pname[]" placeholder="Passenger Name" required>
        <input type="number" name="page[]" min="1" max="120" placeholder="Age" required>

        <select name="pgender[]">
            <option>Male</option>
            <option>Female</option>
            <option>Other</option>
        </select>

        <select name="pnationality[]">
            <option>India</option>
            <option>Other</option>
        </select>

        <select name="berth[]" class="berth-select"></select>

        <button type="button" class="remove-btn" onclick="removePassenger(this)">×</button>
    `;

    document.getElementById("passengerContainer").appendChild(row);

    fillBerthDropdown(row.querySelector(".berth-select"));

    updateFare();
});

// Remove Passenger
function removePassenger(btn) {
    btn.parentElement.remove();
    updateFare();
}

// Update Fare calculation
function updateFare() {
    let count = document.querySelectorAll(".passenger-row").length;
    let total = baseFare * count;

    document.querySelector(".fare-summary h3").innerHTML = `Total Fare: ₹ ${total}`;
    document.querySelector(".fare-summary p").innerHTML = `Ticket Fare: ₹ ${baseFare} x ${count}`;
}

function showToast(message, color = "#005eff") {
    const toast = document.getElementById("toast");
    toast.innerText = message;
    toast.style.background = color;
    toast.classList.add("show");

    setTimeout(() => {
        toast.classList.remove("show");
    }, 2600);
}
