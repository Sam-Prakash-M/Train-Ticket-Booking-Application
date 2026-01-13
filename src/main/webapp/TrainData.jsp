<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Session Logic for Header consistency
    String userName = (String) session.getAttribute("user_name");
    boolean isLoggedIn = (userName != null);
    String userInitial = isLoggedIn ? String.valueOf(userName.charAt(0)).toUpperCase() : "U";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Train Details | Sam Railways</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link href="https://cdn.jsdelivr.net/npm/remixicon@4.1.0/fonts/remixicon.css" rel="stylesheet" />
<link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&family=Space+Grotesk:wght@500;700&display=swap" rel="stylesheet">

<link rel="stylesheet" href="TrainData.css?v=2026">
<link rel="icon" type="image/png" href="train_logo_all.png">

<script>
    const savedTheme = localStorage.getItem('sam_theme') || 'light';
    if (savedTheme === 'dark') document.documentElement.setAttribute('data-theme', 'dark');
</script>
<script defer src="TrainData.js?v=2026_1"></script>
</head>
<body>

    <div class="ambient-mesh"></div>

    <div class="app-container">

        <nav class="navbar glass-panel">
            <div class="nav-brand">
                <div class="logo-box"><i class="ri-train-fill"></i></div>
                <span class="brand-text">Sam Railways</span>
            </div>
            <div class="nav-menu">
                <a href="RailwayApplication.jsp" class="nav-link"><i class="ri-home-5-line"></i> Home</a>
                <a href="#" class="nav-link active"><i class="ri-search-eye-line"></i> Train Info</a>
            </div>
            <div class="nav-profile">
                <button id="themeToggle" class="theme-toggle-btn">
                    <i class="ri-sun-line light-icon"></i>
                    <i class="ri-moon-line dark-icon"></i>
                </button>
                <% if(isLoggedIn) { %>
                <div class="user-avatar"><%= userInitial %></div>
                <% } else { %>
                <a href="login.jsp" class="btn-login">Login</a>
                <% } %>
            </div>
        </nav>

        <main class="main-content">
            
            <div class="search-section animate-up">
                <div class="hero-text">
                    <h1>Find Your Train</h1>
                    <p>Search by Train Name or Number to get route & fare details.</p>
                </div>

                <form id="trainForm" class="search-bar glass-panel" onsubmit="return false;">
                    <div class="input-group">
                        <i class="ri-search-2-line icon"></i>
                        <input type="text" id="trainInput" name="trainInput" placeholder="Ex: T1001 or Nellai Express" required autocomplete="off">
                    </div>
                    <button type="submit" class="btn-search">
                        <span>Search</span>
                        <i class="ri-arrow-right-line"></i>
                        <div id="loader" class="spinner hidden"></div>
                    </button>
                </form>
            </div>

            <div id="resultContainer" class="result-box hidden">
                </div>

        </main>
    </div>

</body>
</html>