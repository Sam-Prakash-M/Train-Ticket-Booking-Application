🚆 Sam Railways | NextGen Booking Platform
==========================================

A futuristic, high-performance web application for train ticket booking, PNR status checking, and route visualization. Built with a robust **Java Servlet (Jakarta EE)** backend and a cutting-edge **2026 Neon Glassmorphism** frontend.

🌟 Key Features
---------------

### 🎨 User Interface (2026 Style)

*   **Glassmorphism Design:** Deep blur effects, translucent panels, and vibrant ambient mesh backgrounds.
    
*   **Dynamic Theming:** Seamless **Dark/Light mode** switching with persistence.
    
*   **Micro-Interactions:** Smooth animations, floating input labels, and hover glow effects.
    
*   **Responsive:** Fully optimized for Desktop, Tablet, and Mobile devices.
    

### 🚄 Core Train Services

*   **Smart Search:** Search trains by Source, Destination, Date, Class (1A, 2A, 3A, SL), and Quota.
    
*   **Live PNR Status:** AJAX-based PNR lookup with visual status indicators.
    
*   **Train Data Visualization:** Vertical timeline for routes and dynamic fare tables.
    
*   **Chart Vacancy:** View chart preparation status and vacant berths.
    

### 🔐 Advanced Security & Auth

*   **Secure Login/Signup:** Form validation and secure session management.
    
*   **Secure Password Hashing:** Uses jBCrypt for password encryption.
    
*   **Account Recovery (Multi-Step):**
    
    *   **Forgot Username:** Retrieve username via Email OTP.
        
    *   **Forgot Password:**
        
        1.  **Check Username:** Validates username existence first.
            
        2.  **Verify Email:** Displays registered email (masked/read-only).
            
        3.  **OTP Verification:** 6-digit OTP sent via **Gmail SMTP**.
            
        4.  **Reset:** Secure password update.
            

### 💳 Payment Integration (Test Mode)

Supports three major payment gateways for ticket booking:

1.  **PayPal:** Server-side integration using paypal-server-sdk.
    
2.  **Razorpay:** Integrated via razorpay-java.
    
3.  **Cashfree:** Integrated using cashfree\_pg.
    

🛠️ Tech Stack
--------------

### Frontend

*   **HTML5 & CSS3:** Advanced CSS Variables, Flexbox/Grid, Animations.
    
*   **JavaScript (ES6+):** Fetch API, DOM Manipulation.
    
*   **Libraries:** RemixIcon, Flatpickr, Google Fonts (Outfit & Space Grotesk).
    

### Backend

*   **Language:** Java (JDK 17+).
    
*   **Framework:** Jakarta EE (Servlets 6.0, JSP 3.1).
    
*   **Database:** MongoDB (via mongodb-driver-sync).
    
*   **Build Tool:** Maven.
    

### Libraries & Dependencies (pom.xml)

*   **JSON:** Google Gson, Jackson, org.json.
    
*   **Mail:** Jakarta Mail (for SMTP).
    
*   **Payments:** PayPal SDK, Razorpay Java, Cashfree PG.
    
*   **Security:** jBCrypt.
    
*   **QR Codes:** ZXing.
    

📂 Project Structure
--------------------  
```SamRailways/  
├── src/main/java/com/samprakash/  │   
├── basemodel/          # Enums (Status, etc.)  
│   
├── trainmodel/         # POJOs (TrainData, FareAmount, Routes)  
│   ├
── trainview/          # Servlets (Search, PNR, TrainData)  
│  
├── trainviewmodel/     # Business Logic & DB Connectors  
│   
├── profileview/        # Auth Servlets (Login, ForgotPassword)  
│   
├── profileviewmodel/   # User Logic (UserViewModel, MailService)  
│   └── trainutil/          # Utilities (EmailService, DateUtils)  
│ 
├── src/main/webapp/  
│   
├── css/                # Styles (ticketsearch.css, forgotPassword.css)  
│   
├── js/                 # Logic (ticketsearch.js, forgotPassword.js)  
│  
├── WEB-INF/            # web.xml  
│   
├── ticketsearch.jsp    # Main Dashboard  
│   
├── TrainData.jsp       # Train Info Page  
│   
├── login.jsp           # Login Page  
│   
├── forgotPassword.jsp  # Password Reset Flow  
│   
├── forgotUsername.jsp  # Username Recovery Flow  
│  
└── cashfreeCheckout.jsp # Payment Page  
└── pom.xml                 # Dependencies  
```


⚙️ Installation & Setup
-----------------------

### 1\. Prerequisites

*   **IDE:** Eclipse (Enterprise) or IntelliJ IDEA Ultimate.
    
*   **Server:** Apache Tomcat 10.0+ (Jakarta EE compatible).
    
*   **Java:** JDK 17 or higher.
    
*   **Database:** MongoDB installed locally or on Atlas.
    

### 2\. Configuration

1.  Bashgit clone https://github.com/your-username/SamRailways.git
    
2.  **Import Maven Project:** Open your IDE and import as a Maven project to download dependencies.
    
3.  Open src/main/java/com/samprakash/profileviewmodel/MailService.java and update:Javaprivate static final String SENDER\_EMAIL = "your-email@gmail.com";private static final String APP\_PASSWORD = "xxxx-xxxx-xxxx-xxxx"; // Google App Password
    
4.  **Database Setup:** Ensure your DataBaseConnector.java points to your MongoDB instance.
    

### 3\. Running the App

1.  Right-click project -> **Run As** -> **Run on Server**.
    
2.  Select **Tomcat v10.0**.
    
3.  Access via: http://localhost:8080/TrainTicketBookingApplication/ticketsearch.jsp
    

🔌 API Endpoints (Servlets)
---------------------------

**EndpointMethodDescription**/SearchServletGETSearches for trains./PnrStatusGET (AJAX)Returns PNR status JSON./TrainDataGET (AJAX)Returns train details JSON./ForgotPasswordServletPOST (AJAX)Actions: checkUsername, sendOtp, verifyOtp, resetPassword./ForgotUsernameServletPOST (AJAX)Actions: sendOtp, verifyOtp./PaymentServletPOSTHandles payment gateway callbacks.

📊 Class Diagram (Simplified)
-----------------------------

   classDiagram      
```class SearchServlet {          +doGet()      }      
class ForgotPasswordServlet {
       +doPost()
       -action: checkUsername
       -action: sendOtp
       -action: verifyOtp
 }     
class UserViewModel {
       +getUserNameByEmailId()
      +getEmailIdByUserName()
      +updateUserPassword()
 }      
class MailService {
      +sendOtpEmail()
 }
 class TrainViewModel {
       +getTrainDetails()
       +getPnrStatus()
 }      
class DataBaseConnector {
      +getConnection()
}
 ForgotPasswordServlet --> UserViewModel : Uses
 ForgotPasswordServlet --> MailService : Uses
 SearchServlet --> TrainViewModel : Uses
 UserViewModel --> DataBaseConnector : Connects
 TrainViewModel --> DataBaseConnector : Connects   
```
📜 License
----------

Developed by Sam Prakash.

Private Project - All Rights Reserved.
