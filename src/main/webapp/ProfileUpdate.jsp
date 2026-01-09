<%@ page contentType="text/html;charset=UTF-8"
	import="com.samprakash.basemodel.Users" language="java"%>
<%
String userName = (String) session.getAttribute("user_name");
if (userName == null) {
	response.sendRedirect("login.jsp");
	return;
}
Users currentUser = (Users) request.getAttribute("currentUser");

// Fallback if attribute is null (for direct access testing)
String currentName = (currentUser != null) ? currentUser.fullName() : (String) session.getAttribute("user_fullname");
String currentEmail = (currentUser != null) ? currentUser.email() : (String) session.getAttribute("user_email");
String currentPhone = (currentUser != null) ? currentUser.contactNo() : "";

String userInitial = String.valueOf(userName.charAt(0)).toUpperCase();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Edit Profile | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/intl-tel-input@18.2.1/build/css/intlTelInput.css">
<link rel="stylesheet" href="ProfileUpdate.css?v=2026_NEON">

<script>
    const savedTheme = localStorage.getItem('sam_theme') || 'light';
    if (savedTheme === 'dark') document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer
	src="https://cdn.jsdelivr.net/npm/intl-tel-input@18.2.1/build/js/intlTelInput.min.js"></script>
<script defer src="ProfileUpdate.js?v=2026_NEON"></script>
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
				<button id="themeToggle" class="icon-btn">
					<i class="ri-moon-line"></i>
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

			<div class="profile-card glass animate-up">

				<div class="edit-badge">
					<i class="ri-edit-2-fill"></i>
				</div>

				<div class="profile-header">
					<div class="avatar-glow">
						<div class="avatar-large"><%=userInitial%></div>
					</div>
					<h2><%=userName%></h2>
					<span class="role-pill">Verified Passenger <i
						class="ri-verified-badge-fill"></i></span>
				</div>

				<form action="ProfileUpdate" method="post" class="profile-form"
					id="profileForm">

					<div class="form-grid">

						<div class="input-group">
							<label>Full Name</label>
							<div class="field-box">
								<i class="ri-user-smile-line icon"></i> <input type="text"
									name="FullName"
									value="<%=currentName != null ? currentName : ""%>"
									placeholder="Enter full name" required>
							</div>
						</div>

						<div class="input-group">
							<label>Email Address</label>
							<div class="field-box">
								<i class="ri-mail-line icon"></i> <input type="email"
									name="Email"
									value="<%=currentEmail != null ? currentEmail : ""%>"
									placeholder="Enter email" required>
							</div>
						</div>

						<div class="input-group phone-group">
							<label>Contact Number</label>
							<div class="field-box phone-box">
								<input type="tel" id="mobile"
									value="<%=currentPhone != null ? currentPhone : ""%>"
									placeholder=" " required> <input type="hidden"
									name="ContactNo" id="fullMobileNumber">
							</div>
							<span id="valid-msg" class="hide">✓ Valid</span> <span
								id="error-msg" class="hide"></span>
						</div>

						<div class="input-group disabled">
							<label>Username</label>
							<div class="field-box">
								<i class="ri-shield-user-line icon"></i> <input type="text"
									value="<%=userName%>" disabled>
							</div>
						</div>

					</div>

					<div class="security-section">
						<div class="sec-info">
							<h4>
								<i class="ri-lock-password-line"></i> Password & Security
							</h4>
							<p>Manage your password to keep your account safe.</p>
						</div>
						<button type="button" class="btn-outline"
							onclick="openPasswordModal()">Change Password</button>
					</div>

					<div class="form-actions">
						<button type="button" class="btn-cancel"
							onclick="window.history.back()">Back</button>
						<button type="submit" class="btn-save">
							Update Profile <i class="ri-save-3-line"></i>
						</button>
					</div>

				</form>
			</div>

		</main>
	</div>

	<div id="passwordModal" class="modal-overlay hidden">
		<div class="modal-box glass">
			<div class="modal-header">
				<h3>Change Password</h3>
				<button class="close-modal" onclick="closePasswordModal()">
					<i class="ri-close-line"></i>
				</button>
			</div>
			<form action="PasswordUpdate" method="post" id="passForm">
				<div class="modal-body">
					<div class="input-group">
						<label>Current Password</label>
						<div class="field-box">
							<i class="ri-lock-unlock-line icon"></i> <input type="password"
								name="currentPassword" required>
						</div>
					</div>
					<div class="input-group">
						<label>New Password</label>
						<div class="field-box">
							<i class="ri-lock-line icon"></i> <input type="password"
								name="newPassword" required minlength="6">
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn-cancel"
						onclick="closePasswordModal()">Cancel</button>
					<button type="submit" class="btn-save">Update Password</button>
				</div>
			</form>
		</div>
	</div>

	<div id="toast" class="toast"></div>
	<%
	String success = (String) request.getAttribute("success");
	String failure = (String) request.getAttribute("failure");
	if (success != null) {
	%>
	<script>window.onload = function() { showToast("<%=success%>", false); }</script>
	<%
	} else if (failure != null) {
	%>
	<script>window.onload = function() { showToast("<%=failure%>", true);
		}
	</script>
	<%
	}
	%>

</body>
</html>