<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Reset Password | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link
	href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&family=Poppins:wght@600;700&display=swap"
	rel="stylesheet">

<link rel="stylesheet" href="forgotPassword.css?v=101">
<link rel="icon" type="image/png" href="train_logo_all.png">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>

<script defer src="forgotPassword.js?v=100"></script>
</head>
<body>

	<!-- Animated Background -->
	<div class="animated-bg">
		<div class="gradient-orb orb-1"></div>
		<div class="gradient-orb orb-2"></div>
		<div class="gradient-orb orb-3"></div>
	</div>

	<div class="app-container">

		<!-- Enhanced Navbar -->
		<nav class="navbar">
			<div class="nav-brand">
				<div class="logo-wrapper">
					<i class="ri-train-fill"></i>
					<div class="logo-shine"></div>
				</div>
				<span class="brand-text">Sam Railways</span>
			</div>

			<div class="nav-right">
				<button id="themeToggle" class="theme-toggle"
					aria-label="Toggle theme">
					<div class="toggle-track">
						<div class="toggle-thumb">
							<i class="ri-sun-fill sun-icon"></i> <i
								class="ri-moon-stars-fill moon-icon"></i>
						</div>
					</div>
				</button>
				<a href="login.jsp" class="back-link"> <i
					class="ri-arrow-left-line"></i> <span>Back to Login</span>
				</a>
			</div>
		</nav>

		<main class="main-content">

			<div class="auth-card">

				<!-- Progress Indicator -->
				<div class="progress-bar">
					<div class="progress-step active" data-step="1">
						<div class="step-circle">1</div>
						<span class="step-label">Username</span>
					</div>
					<div class="progress-line"></div>
					<div class="progress-step" data-step="2">
						<div class="step-circle">2</div>
						<span class="step-label">Verify</span>
					</div>
					<div class="progress-line"></div>
					<div class="progress-step" data-step="3">
						<div class="step-circle">3</div>
						<span class="step-label">Reset</span>
					</div>
				</div>

				<!-- Card Header -->
				<div class="card-header" id="cardHeader">
					<div class="icon-wrapper">
						<i class="ri-lock-password-line"></i>
					</div>
					<h1>Reset Password</h1>
					<p id="headerText">Enter your username to find your account.</p>
				</div>

				<!-- STEP 1: Username -->
				<div id="step-username" class="step-container active">
					<form onsubmit="return false;">
						<div class="form-group">
							<label class="form-label">Username</label>
							<div class="input-wrapper">
								<i class="ri-user-line input-icon"></i> <input type="text"
									id="usernameInput" class="form-input"
									placeholder="Enter your username" required
									autocomplete="username">
							</div>
						</div>

						<button id="checkUserBtn" class="btn btn-primary" type="button">
							<span class="btn-content"> <span class="btn-text">Continue</span>
								<i class="ri-arrow-right-line btn-icon"></i>
							</span>
							<div class="btn-loader hidden">
								<div class="spinner"></div>
							</div>
						</button>
					</form>
				</div>

				<!-- STEP 2: Email Confirmation -->
				<div id="step-email" class="step-container">
					<form onsubmit="return false;">
						<div class="info-box">
							<i class="ri-information-line"></i>
							<p>Is this your registered email?</p>
						</div>

						<div class="form-group">
							<label class="form-label">Registered Email</label>
							<div class="input-wrapper locked">
								<i class="ri-mail-line input-icon"></i> <input type="email"
									id="emailInput" class="form-input" readonly> <i
									class="ri-lock-fill lock-badge"></i>
							</div>
						</div>

						<button id="getOtpBtn" class="btn btn-primary" type="button">
							<span class="btn-content"> <span class="btn-text">Send
									Verification Code</span> <i class="ri-send-plane-fill btn-icon"></i>
							</span>
							<div class="btn-loader hidden">
								<div class="spinner"></div>
							</div>
						</button>

						<button class="btn btn-text" type="button"
							onclick="location.reload()">
							<i class="ri-refresh-line"></i> Not you? Change Username
						</button>
					</form>
				</div>

				<!-- STEP 3: OTP Verification -->
				<div id="step-otp" class="step-container">
					<div class="otp-info">
						<div class="otp-icon">
							<i class="ri-mail-send-line"></i>
						</div>
						<p>Code sent to</p>
						<span id="displayEmail" class="email-display"></span>
					</div>

					<form onsubmit="return false;">
						<div class="form-group">
							<label class="form-label">Enter 6-Digit Code</label>
							<div class="otp-inputs">
								<input type="text" maxlength="1" class="otp-box"
									inputmode="numeric" autocomplete="off"> <input
									type="text" maxlength="1" class="otp-box" inputmode="numeric"
									autocomplete="off"> <input type="text" maxlength="1"
									class="otp-box" inputmode="numeric" autocomplete="off">
								<input type="text" maxlength="1" class="otp-box"
									inputmode="numeric" autocomplete="off"> <input
									type="text" maxlength="1" class="otp-box" inputmode="numeric"
									autocomplete="off"> <input type="text" maxlength="1"
									class="otp-box" inputmode="numeric" autocomplete="off">
							</div>
						</div>

						<button id="verifyBtn" class="btn btn-primary" type="button">
							<span class="btn-content"> <span class="btn-text">Verify
									Code</span> <i class="ri-checkbox-circle-line btn-icon"></i>
							</span>
							<div class="btn-loader hidden">
								<div class="spinner"></div>
							</div>
						</button>

						<div class="timer-wrapper">
							<i class="ri-time-line"></i> <span>Resend code in <strong
								id="timer">60</strong>s
							</span>
						</div>
					</form>
				</div>

				<!-- STEP 4: New Password -->
				<div id="step-reset" class="step-container">
					<form onsubmit="return false;">
						<div class="form-group">
							<label class="form-label">New Password</label>
							<div class="input-wrapper">
								<i class="ri-lock-line input-icon"></i> <input type="password"
									id="newPassword" class="form-input"
									placeholder="Enter new password" required
									autocomplete="new-password">
								<button type="button" class="toggle-password"
									data-target="newPassword">
									<i class="ri-eye-line"></i> <i class="ri-eye-off-line"></i>
								</button>
							</div>
							<div class="password-strength">
								<div class="strength-bar">
									<div class="strength-fill"></div>
								</div>
								<span class="strength-text">Password strength</span>
							</div>
						</div>

						<div class="form-group">
							<label class="form-label">Confirm Password</label>
							<div class="input-wrapper">
								<i class="ri-lock-check-line input-icon"></i> <input
									type="password" id="confirmPassword" class="form-input"
									placeholder="Confirm new password" required
									autocomplete="new-password">
								<button type="button" class="toggle-password"
									data-target="confirmPassword">
									<i class="ri-eye-line"></i> <i class="ri-eye-off-line"></i>
								</button>
							</div>
						</div>

						<button id="resetBtn" class="btn btn-primary" type="button">
							<span class="btn-content"> <span class="btn-text">Update
									Password</span> <i class="ri-shield-check-line btn-icon"></i>
							</span>
							<div class="btn-loader hidden">
								<div class="spinner"></div>
							</div>
						</button>
					</form>
				</div>

				<!-- STEP 5: Success -->
				<div id="step-success" class="step-container">
					<div class="success-animation">
						<div class="success-circle">
							<div class="success-checkmark">
								<i class="ri-check-line"></i>
							</div>
						</div>
					</div>
					<h2 class="success-title">Password Updated!</h2>
					<p class="success-text">Your password has been changed
						successfully. You can now login with your new password.</p>

					<a href="login.jsp" class="btn btn-success"> <span
						class="btn-content"> <span class="btn-text">Go to
								Login</span> <i class="ri-login-box-line btn-icon"></i>
					</span>
					</a>
				</div>

				<!-- Error Alert -->
				<div id="errorMessage" class="alert alert-error hidden">
					<i class="ri-error-warning-fill"></i> <span id="errorText"></span>
					<button class="alert-close"
						onclick="this.parentElement.classList.add('hidden')">
						<i class="ri-close-line"></i>
					</button>
				</div>

			</div>

		</main>
	</div>

</body>
</html>