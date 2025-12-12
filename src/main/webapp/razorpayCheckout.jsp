<%@ page contentType="text/html; charset=UTF-8" %>
<%
    // Header Data
    String userName = (String) session.getAttribute("user_name");
    if(userName == null) userName = "Guest";

    // Payment Data
    String orderId = (String) request.getAttribute("order_id");
    String amount  = String.valueOf(request.getAttribute("amount"));
    String keyId   = (String) request.getAttribute("key");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Processing Payment | Sam Railways</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <link href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css" rel="stylesheet"/>
    <link rel="stylesheet" href="razorpayCheckout.css?v=2025">
    
    <script>
        const savedTheme = localStorage.getItem('sam_theme') || 'light';
        if (savedTheme === 'dark') document.documentElement.setAttribute('data-theme', 'dark');
    </script>
</head>
<body>

    <div class="ambient-light"></div>

    <div class="app-container">
        
        <nav class="navbar glass">
            <div class="nav-brand">
                <div class="logo-box"><i class="ri-train-fill"></i></div>
                <span class="brand-text">Sam Railways</span>
            </div>
            <div class="nav-menu">
                <a href="RailwayApplication.jsp"><i class="ri-home-5-line"></i> Home</a>
                <a href="#" class="active"><i class="ri-secure-payment-line"></i> Payment</a>
            </div>
            <div class="nav-profile">
                <button id="themeToggle" class="icon-btn"><i class="ri-moon-line"></i></button>
                <div class="user-dropdown">
                    <button class="user-btn">
                        <span class="u-avatar"><%=userName.charAt(0)%></span>
                        <span class="u-name">My Account</span>
                        <i class="ri-arrow-down-s-line"></i>
                    </button>
                    <div class="dropdown-content glass">
                        <a href="profile.jsp"><i class="ri-user-line"></i> Profile</a>
                        <a href="ticket_history.jsp"><i class="ri-history-line"></i> Bookings</a>
                        <a href="logout" class="danger"><i class="ri-logout-box-line"></i> Logout</a>
                    </div>
                </div>
            </div>
        </nav>

        <main class="main-content">
            <div class="process-card glass">
                <div class="spinner-box">
                    <div class="spinner"></div>
                    <div class="logo-overlay"><img src="https://upload.wikimedia.org/wikipedia/commons/8/89/Razorpay_logo.svg" alt="Razorpay"></div>
                </div>
                <h2>Secure Payment Gateway</h2>
                <p>Please wait, do not refresh or press back...</p>
                <button id="retryBtn" class="retry-btn hidden" onclick="location.reload()">Retry Payment</button>
            </div>
        </main>

    </div>

    <script src="https://checkout.razorpay.com/v1/checkout.js"></script>
    <script src="razorpayCheckout.js?v=2025"></script>

    <script>
        document.addEventListener("DOMContentLoaded", () => {
            openRazorpayCheckout("<%= keyId %>", "<%= amount %>", "<%= orderId %>");
        });
    </script>

</body>
</html>