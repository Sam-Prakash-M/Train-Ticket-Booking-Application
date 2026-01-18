<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Forgot Username | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css"
	rel="stylesheet" />
<link
	href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&family=Space+Grotesk:wght@500;700&display=swap"
	rel="stylesheet">

<link rel="stylesheet" href="forgotUsername.css?v=EMAIL_MODE">
<link rel="icon" type="image/png" href="train_logo_all.png">

<script>
	const savedTheme = localStorage.getItem('sam_theme') || 'light';
	if (savedTheme === 'dark')
		document.documentElement.setAttribute('data-theme', 'dark');
</script>

<script defer src="forgotUsername.js?v=EMAIL_MODE"></script>
</head>
<body>

	<div class="ambient-mesh"></div>

	<div class="app-container">

		<nav class="navbar glass-panel">
			<div class="nav-brand">
				<div class="logo-box">
					<i class="ri-train-fill"></i>
				</div>
				<span class="brand-text">Sam Railways</span>
			</div>
			<a href="login.jsp" class="nav-link"><i
				class="ri-arrow-left-line"></i> Back to Login</a>
		</nav>

		<main class="main-content">

			<div class="auth-card glass-panel animate-up">

				<div class="card-header">
					<div class="icon-bubble">
						<i class="ri-mail-send-line"></i>
					</div>
					<h2>Forgot Username?</h2>
					<p>Enter your registered email to receive an OTP.</p>
				</div>

				<div id="step-email" class="step-container active">
					<form id="emailForm" onsubmit="return false;">
						<div class="input-group">
							<i class="ri-at-line icon"></i> <input type="email"
								id="emailInput" class="custom-input"
								placeholder="e.g. sam@example.com" required> <label
								class="floating-label">Email Address</label>
						</div>

						<button id="getOtpBtn" class="btn-primary btn-full hover-glow">
							<span class="btn-text">Send OTP</span>
							<div class="spinner hidden"></div>
						</button>
					</form>
				</div>

				<div id="step-otp" class="step-container hidden">
					<div class="otp-instruction">
						Code sent to <span id="displayEmail" class="highlight-text"></span>
						<button class="btn-link" id="editEmailBtn">Change</button>
					</div>

					<form id="otpForm" onsubmit="return false;">
						<div class="otp-inputs">
							<input type="text" maxlength="1" class="otp-box" autofocus>
							<input type="text" maxlength="1" class="otp-box"> <input
								type="text" maxlength="1" class="otp-box"> <input
								type="text" maxlength="1" class="otp-box"> <input
								type="text" maxlength="1" class="otp-box"> <input
								type="text" maxlength="1" class="otp-box">
						</div>

						<button id="verifyBtn" class="btn-primary btn-full hover-glow">
							<span class="btn-text">Verify & Get Username</span>
							<div class="spinner hidden"></div>
						</button>

						<div class="timer-box">
							Resend code in <span id="timer">60</span>s
						</div>
					</form>
				</div>

				<div id="step-success" class="step-container hidden">
					<div class="success-animation">
						<i class="ri-checkbox-circle-fill"></i>
					</div>
					<h3>Verified Successfully!</h3>
					<p>Your username is:</p>

					<div class="username-display">
						<span id="retrievedUsername">loading...</span>
						<button class="btn-copy" onclick="copyUsername()">
							<i class="ri-file-copy-line"></i>
						</button>
					</div>

					<a href="login.jsp" class="btn-secondary btn-full">Login Now</a>
				</div>

				<div id="errorMessage" class="error-banner hidden">
					<i class="ri-error-warning-fill"></i> <span id="errorText"></span>
				</div>

			</div>

		</main>
	</div>

</body>
</html>