<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
String userName = (String) session.getAttribute("user_name");
if (userName == null) {
	response.sendRedirect("login.jsp");
	return;
}
String userInitial = String.valueOf(userName.charAt(0)).toUpperCase();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>My Transactions | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="icon" type="image/png"
	href="https://cdn-icons-png.flaticon.com/512/1036/1036137.png">
<link rel="stylesheet" href="TransactionView.css?v=2026_FINAL">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="TransactionView.js?v=2026_FINAL"></script>
</head>
<body>

	<div class="ambient-light-neon"></div>

	<div class="app-container">

		<nav class="navbar glass">
			<div class="nav-brand">
				<div class="logo-box">
					<i class="ri-train-line"></i>
				</div>
				<span class="brand-text">Sam Railways</span>
			</div>
			<div class="nav-menu">
				<a href="RailwayApplication.jsp"><i class="ri-home-5-line"></i>
					Home</a> <a href="MyBookings"><i class="ri-ticket-2-line"></i>
					Bookings</a>
			</div>
			<div class="nav-profile">
				<button id="themeToggle" class="theme-btn">
					<i class="ri-sun-line light-icon"></i> <i
						class="ri-moon-line dark-icon"></i>
				</button>

				<div class="user-dropdown">
					<button class="user-btn">
						<span class="u-avatar"><%=userInitial%></span> <span
							class="u-name">My Account</span>
					</button>
				</div>
			</div>
		</nav>

		<main class="main-content">
			<div class="table-card glass animate-up">

				<div class="card-header">
					<div class="header-info">
						<h2>Transaction History</h2>
						<p>Track your payments and wallet top-ups.</p>
					</div>
					<button class="btn-refresh" onclick="refreshData()">
						<i class="ri-refresh-line"></i> <span>Refresh</span>
					</button>
				</div>

				<div class="table-responsive">
					<table class="modern-table">
						<thead>
							<tr>
								<th>Txn ID</th>
								<th>Date</th>
								<th>Purpose</th>
								<th>Gateway</th>
								<th>Status</th>
								<th class="text-right">Amount</th>
							</tr>
						</thead>
						<tbody id="txnTableBody">
						</tbody>
					</table>
				</div>

				<div id="loadingState" class="state-box">
					<div class="spinner"></div>
					<p>Fetching records...</p>
				</div>

				<div id="emptyState" class="state-box hidden">
					<div class="icon-circle">
						<i class="ri-secure-payment-line"></i>
					</div>
					<h3>No Transactions Found</h3>
					<p>You haven't made any payments yet.</p>
				</div>

				<div class="pagination-controls hidden" id="paginationBox">
					<button id="prevBtn" class="page-btn" onclick="changePage(-1)"
						disabled>
						<i class="ri-arrow-left-s-line"></i>
					</button>
					<span id="pageIndicator" class="page-num">Page 1</span>
					<button id="nextBtn" class="page-btn" onclick="changePage(1)">
						<i class="ri-arrow-right-s-line"></i>
					</button>
				</div>

			</div>
		</main>
	</div>

</body>
</html>