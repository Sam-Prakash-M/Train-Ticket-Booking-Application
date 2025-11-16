function removePassenger(btn) {
    const row = btn.parentElement;
    const container = document.getElementById("passengerContainer");

    if (document.querySelectorAll(".passenger-row").length > 1) {
        container.removeChild(row);
    } else {
        alert("At least one passenger is required!");
    }
}

document.getElementById("addPassengerBtn").addEventListener("click", () => {

    let newRow = document.createElement("div");
    newRow.classList.add("passenger-row");

    newRow.innerHTML = `
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
        <select name="berth[]">
            <option>No Preference</option>
            <option>Lower</option>
            <option>Middle</option>
            <option>Upper</option>
        </select>
        <button type="button" class="remove-btn" onclick="removePassenger(this)">×</button>
    `;

    document.getElementById("passengerContainer").appendChild(newRow);
});
