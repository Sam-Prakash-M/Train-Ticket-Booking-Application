let currentPage = 1;

document.addEventListener("DOMContentLoaded", () => {
	// Theme Toggle
	const toggle = document.getElementById("themeToggle");
	if (toggle) {
		toggle.addEventListener("click", () => {
			const root = document.documentElement;
			const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
			root.setAttribute("data-theme", next);
			localStorage.setItem("sam_theme", next);
		});
	}

	// Load Initial Data
	loadTransactions(1);
});

function refreshData() {
	loadTransactions(1);
}

function changePage(direction) {
	const newPage = currentPage + direction;
	if (newPage < 1) return;
	loadTransactions(newPage);
}

function loadTransactions(page) {
	currentPage = page;
	const tbody = document.getElementById("txnTableBody");
	const loading = document.getElementById("loadingState");
	const empty = document.getElementById("emptyState");
	const tableBox = document.querySelector(".table-responsive");
	const pagination = document.getElementById("paginationBox");
	const prevBtn = document.getElementById("prevBtn");
	const nextBtn = document.getElementById("nextBtn");

	// 1. Show Loading, Hide Table
	loading.classList.remove("hidden");
	empty.classList.add("hidden");
	tableBox.classList.add("hidden");
	pagination.classList.add("hidden");
	tbody.innerHTML = "";

	// 2. Fetch Data
	fetch(`TransactionList?page=${page}`, { method: 'POST' })
		.then(response => response.json())
		.then(data => {
			loading.classList.add("hidden");

			// Handle Empty
			if (!data || data.length === 0) {
				if (page === 1) {
					empty.classList.remove("hidden");
				} else {
					currentPage--;
					// Restore table if we just tried to go to an empty page
					tableBox.classList.remove("hidden");
					pagination.classList.remove("hidden");
					// Re-fetch previous page to show data
					loadTransactions(currentPage);
					alert("No more records.");
				}
				return;
			}

			// 3. Render Table
			tableBox.classList.remove("hidden");
			pagination.classList.remove("hidden");

			data.forEach(txn => {
				const row = `
                <tr>
                    <td><span style="font-family:monospace; color:var(--primary); font-weight:600">#${txn.transactionId.substring(0, 8)}</span></td>
                    <td>${txn.transactionDate}</td>
                    <td>${formatPurpose(txn.transactionPurpose)}</td>
                    <td>${txn.paymentGateWay}</td>
                    <td>${getStatusBadge(txn.TransactionStatus)}</td>
                    <td class="text-right amount">₹${txn.totalAmount.toFixed(2)}</td>
                </tr>
            `;
				tbody.innerHTML += row;
			});

			// 4. Update Controls
			document.getElementById("pageIndicator").innerText = `Page ${currentPage}`;
			prevBtn.disabled = (currentPage === 1);
			nextBtn.disabled = (data.length < 10);
		})
		.catch(err => {
			console.error(err);
			loading.innerHTML = `<p style="color:red">Failed to load data. Try refreshing.</p>`;
		});
}

function getStatusBadge(status) {
	let cls = 'status-pending';
	if (status === 'SUCCESS') cls = 'status-success';
	if (status === 'FAILURE') cls = 'status-failure';
	return `<span class="status-pill ${cls}">${status}</span>`;
}

function formatPurpose(purpose) {
	if (!purpose) return "N/A";
	return purpose.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
}