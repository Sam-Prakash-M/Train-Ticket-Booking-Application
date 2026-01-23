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
    
*   **Chart Vacancy:** View chart preparation status and vacant berths.(Under Development)
    

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
Train-Ticket-Booking-Application/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── samprakash/
│   │   │           ├── basemodel/                  # [CORE ENUMS & RECORDS]
│   │   │           │   ├── Status.java
│   │   │           │   ├── TrainBookingDatabase.java
│   │   │           │   ├── TrainCollection.java
│   │   │           │   ├── UserCollection.java
│   │   │           │   └── Users.java
│   │   │           │
│   │   │           ├── baseview/                   # [CORE SERVLETS]
│   │   │           │   ├── BookingServlet.java
│   │   │           │   ├── LoginServlet.java
│   │   │           │   ├── LogoutServlet.java
│   │   │           │   └── RegisterServlet.java
│   │   │           │
│   │   │           ├── baseviewmodel/              # [CORE LOGIC]
│   │   │           │   ├── Hashing.java
│   │   │           │   └── TrainDataFetcher.java
│   │   │           │
│   │   │           ├── cancelticketview/           # [CANCELLATION SERVLET]
│   │   │           │   └── CancelTicketServlet.java
│   │   │           │
│   │   │           ├── cancelticketviewmodel/      # [CANCELLATION LOGIC]
│   │   │           │   └── CancelTicketViewModel.java
│   │   │           │
│   │   │           ├── exception/                  # [CUSTOM EXCEPTIONS]
│   │   │           │   └── SeatNotAvailableException.java
│   │   │           │
│   │   │           ├── jerseyconfig/               # [REST CONFIG]
│   │   │           │   └── JerseyConfig.java
│   │   │           │
│   │   │           ├── paymentmodel/               # [PAYMENT DATA]
│   │   │           │   ├── Passenger.java
│   │   │           │   ├── PaymentsCollection.java
│   │   │           │   └── TransactionPurpose.java
│   │   │           │
│   │   │           ├── paymentview/                # [PAYMENT GATEWAYS]
│   │   │           │   ├── cashfreeview/
│   │   │           │   │   └── CashFreeClientView.java
│   │   │           │   ├── paypalview/
│   │   │           │   │   └── PayPalClientView.java
│   │   │           │   ├── razorpayview/
│   │   │           │   │   └── RazorPayClientView.java
│   │   │           │   ├── CashfreeClient.java
│   │   │           │   ├── PaymentGateway.java     # Enum
│   │   │           │   ├── PaymentServlet.java
│   │   │           │   ├── PaymentSuccessServlet.java
│   │   │           │   └── PayPalClient.java
│   │   │           │
│   │   │           ├── paymentviewmodel/           # [PAYMENT LOGIC]
│   │   │           │   └── TransactionStatusHandler.java
│   │   │           │
│   │   │           ├── pnrview/                    # [PNR SERVLET]
│   │   │           │   └── PnrStatusView.java
│   │   │           │
│   │   │           ├── pnrviewmodel/               # [PNR LOGIC]
│   │   │           │   └── PnrStatusViewModel.java
│   │   │           │
│   │   │           ├── profilemodel/               # [PROFILE DATA]
│   │   │           │   └── TransactionData.java
│   │   │           │
│   │   │           ├── profileview/                # [PROFILE & AUTH SERVLETS]
│   │   │           │   ├── ForgotPasswordServlet.java
│   │   │           │   ├── ForgotUsernameServlet.java
│   │   │           │   ├── PasswordUpdateView.java
│   │   │           │   ├── ProfileUpdate.java
│   │   │           │   └── TransactionView.java
│   │   │           │
│   │   │           ├── profileviewmodel/           # [PROFILE LOGIC]
│   │   │           │   ├── MailService.java
│   │   │           │   ├── PasswordUpdateViewModel.java
│   │   │           │   ├── ProfileUpdateViewModel.java
│   │   │           │   ├── TransactionViewModel.java
│   │   │           │   └── UserViewModel.java
│   │   │           │
│   │   │           ├── repository/                 # [DB CONNECTION]
│   │   │           │   └── DataBaseConnector.java
│   │   │           │
│   │   │           ├── searchview/                 # [SEARCH SERVLET]
│   │   │           │   └── SearchServlet.java
│   │   │           │
│   │   │           ├── searchviewmodel/            # [SEARCH API]
│   │   │           │   └── SearchRestApi.java
│   │   │           │
│   │   │           ├── ticketbookmodel/            # [TICKET DATA]
│   │   │           │   ├── BookingData.java
│   │   │           │   ├── BookingState.java
│   │   │           │   ├── PassengerCollection.java
│   │   │           │   ├── Payements.java
│   │   │           │   ├── SeatCounts.java
│   │   │           │   ├── SeatMetaData.java
│   │   │           │   ├── Ticket.java
│   │   │           │   └── TicketStatus.java
│   │   │           │
│   │   │           ├── ticketbookview/             # [BOOKING VIEW SERVLETS]
│   │   │           │   ├── BookingViewServlet.java
│   │   │           │   ├── PrintTicketServlet.java
│   │   │           │   └── QRServlet.java
│   │   │           │
│   │   │           ├── ticketbookviewmodel/        # [BOOKING LOGIC]
│   │   │           │   └── TicketBookingHelper.java
│   │   │           │
│   │   │           ├── trainmodel/                 # [TRAIN DATA]
│   │   │           │   ├── ClassType.java
│   │   │           │   ├── Days.java
│   │   │           │   ├── FareAmount.java
│   │   │           │   ├── Routes.java
│   │   │           │   └── TrainData.java
│   │   │           │
│   │   │           ├── trainview/                  # [TRAIN INFO SERVLET]
│   │   │           │   └── TrainView.java
│   │   │           │
│   │   │           └── trainviewmodel/             # [TRAIN LOGIC]
│   │   │               └── TrainViewModel.java
│   │   │
│   │   └── resources/
│   │       ├── cashfree.properties
│   │       ├── googlemail.properties
│   │       ├── paypal.properties
│   │       └── razorpayprops.properties
│   │
│   └── webapp/
│       ├── css/
│       │   ├── ticketsearch.css
│       │   ├── TrainData.css
│       │   ├── forgotPassword.css
│       │   ├── forgotUsername.css
│       │   ├── login.css
│       │   └── register.css
│       │
│       ├── js/
│       │   ├── ticketsearch.js
│       │   ├── TrainData.js
│       │   ├── forgotPassword.js
│       │   ├── forgotUsername.js
│       │   ├── login.js
│       │   └── register.js
│       │
│       ├── images/
│       │   └── train_logo_all.png
│       │
│       ├── WEB-INF/
│       │   ├── web.xml
│       │   └── lib/ (gson, mongo, mail, paypal-sdk, razorpay, etc.)
│       │
│       ├── ticketsearch.jsp            # Dashboard
│       ├── TrainData.jsp               # Train Info
│       ├── login.jsp                   # Login
│       ├── register.jsp                # Register
│       ├── forgotPassword.jsp          # Password Recovery
│       ├── forgotUsername.jsp          # Username Recovery
│       ├── cashfreeCheckout.jsp        # Payment
│       ├── razorpayCheckout.jsp        # Payment
│       ├── payment.jsp                 # Payment Options (Implicitly referenced)
│       ├── UserBookings.jsp            # History
│       ├── refunds.jsp                 # Refunds
│       ├── ProfileUpdate.jsp           # Profile
│       ├── TransactionList.jsp         # Transactions
│       ├── TransactionView.jsp         # Transaction View Container
│       ├── TicketBookingConfirmation.jsp # Success Page
│       ├── booking.jsp                 # Search Results Container
│       ├── Confirmation.jsp            # Pre-booking confirmation
│       ├── PrintTicket.jsp             # Print View
│       └── PnrStatusView.jsp           # PNR Result View
│
└── pom.xml					       #Maven Dependencies

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
    
3.  Access via: http://localhost:8080/TrainTicketBookingApplication/RailwayApplication.jsp
    

📊 Class Diagram (Simplified)
-----------------------------

classDiagram

    %% --- REPOSITORY LAYER (SINGLETON) ---
    class DataBaseConnector {
        -static instance: DataBaseConnector
        +getInstance(): DataBaseConnector
        +addUser(Users)
        +isUserCredentialIsCorrect(user, pass)
        +getMatchedTrain(source, dest, date)
        +getTicketByPNR(pnr)
        +cancelAndPromoteTickets(pnr, passengers)
        +storeBookingStateInDB(bookingData)
        +storeTransactionStatusInDb(...)
    }

    %% --- CONTROLLERS (SERVLETS) ---
    class LoginServlet { +doPost() }
    class RegisterServlet { +doPost() }
    class SearchServlet { +doGet() }
    class TrainView { +doGet() }
    class PnrStatusView { +doGet() }
    class PaymentServlet { +doPost() }
    class PaymentSuccessServlet { +doPost() }
    class CancelTicketServlet { +doPost() }
    class ForgotPasswordServlet { +doPost() }
    class ForgotUsernameServlet { +doPost() }
    class ProfileUpdate { +doPost() }
    
    %% --- VIEW MODELS (BUSINESS LOGIC) ---
    class TrainDataFetcher {
        +getMatchedTrain()
        +getSeatAvailabilityForTrain()
    }
    class TicketBookingHelper {
        +bookTicket()
        +storeConfirmedTicketInDB()
    }
    class UserViewModel {
        +getUserNameByEmailId()
        +updateUserPassword()
    }
    class CancelTicketViewModel {
        +CancelTicket()
    }
    class PnrStatusViewModel {
        +getBookingDetails()
    }
    class TransactionStatusHandler {
        +storeTransactionStatusInDb()
    }

    %% --- UTILITIES ---
    class MailService {
        +sendOtpEmail(email, otp)
    }
    class Hashing {
        +getHashedPassword()
        +checkPassword()
    }

    %% --- PAYMENT CLIENTS ---
    class CashfreeClient { +init() }
    class PayPalClient { +client() }
    
    %% --- RELATIONSHIPS ---
    %% Auth Flow
    LoginServlet --> DataBaseConnector
    RegisterServlet --> Hashing
    RegisterServlet --> DataBaseConnector
    
    %% Search Flow
    SearchServlet --> TrainDataFetcher
    TrainDataFetcher --> DataBaseConnector
    
    %% Train Info & PNR
    TrainView --> TrainViewModel
    PnrStatusView --> PnrStatusViewModel
    PnrStatusViewModel --> DataBaseConnector
    
    %% Booking & Payment Flow
    PaymentServlet --> PayPalClient
    PaymentServlet --> CashfreeClient
    PaymentSuccessServlet --> TicketBookingHelper
    PaymentSuccessServlet --> TransactionStatusHandler
    TicketBookingHelper --> DataBaseConnector
    TransactionStatusHandler --> DataBaseConnector
    
    %% Recovery Flow
    ForgotPasswordServlet --> MailService
    ForgotPasswordServlet --> UserViewModel
    ForgotUsernameServlet --> MailService
    ForgotUsernameServlet --> UserViewModel
    UserViewModel --> DataBaseConnector
    
    %% Cancellation
    CancelTicketServlet --> CancelTicketViewModel
    CancelTicketViewModel --> DataBaseConnector
📜 License
----------

Developed by Sam Prakash.

Private Project - All Rights Reserved.
