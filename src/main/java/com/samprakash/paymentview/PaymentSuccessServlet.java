package com.samprakash.paymentview;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.cashfree.ApiException;
import com.cashfree.ApiResponse;
import com.cashfree.Cashfree;
import com.cashfree.model.OrderEntity;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.samprakash.basemodel.Status;
import com.samprakash.paymentmodel.Passenger;
import com.samprakash.ticketbookmodel.Ticket;
import com.samprakash.ticketbookviewmodel.TicketBookingHelper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/PaymentSuccess")
public class PaymentSuccessServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		String userID = (String) session.getAttribute("user_name");

		String travelDate = (String) session.getAttribute("travelDate");
		String trainName = (String) session.getAttribute("trainName");
		String trainId = (String) session.getAttribute("trainId");
		String source = (String) session.getAttribute("source");
		String destination = (String) session.getAttribute("destination");

		String classType = (String) session.getAttribute("classType");
		boolean isAutoUpgrade = (boolean) session.getAttribute("autoUpgrade");

		String mobile = (String) session.getAttribute("mobile");
		String email = (String) session.getAttribute("email");

		Double totalAmount = (Double) session.getAttribute("total");

		Set<Passenger> passengerDetails = getPassengerSet(request);

		try {
			Ticket userTicket = TicketBookingHelper.bookTicket(travelDate, passengerDetails, mobile, email, trainName,
					trainId, totalAmount, source, destination, classType, isAutoUpgrade);

			RequestDispatcher requestDispatcher = request.getRequestDispatcher("TicketBookingConfirmation.jsp");
			if (userTicket == null) {
				request.setAttribute("errorMessage", "Ticket booking failed. Seats may not be available.");
				userTicket = new Ticket(travelDate, trainId, trainName, classType, source, destination,
						Status.NOT_APPLICAPLE.name(), null, null, totalAmount);
				TicketBookingHelper.storeFailureBookingTransactionAmounInDB(userTicket, userID, mobile, email);
				requestDispatcher.forward(request, response);
			} else {
				request.setAttribute("ConfirmedTicket", userTicket);
				TicketBookingHelper.storeConfirmedTicketInDB(userTicket, userID, mobile, email);
				requestDispatcher.forward(request, response);

			}

		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private Set<Passenger> getPassengerSet(HttpServletRequest request) {

		HttpSession session = request.getSession();

		String[] names = (String[]) session.getAttribute("names");
		String[] ages = (String[]) session.getAttribute("ages");
		String[] genders = (String[]) session.getAttribute("genders");
		String[] nationalities = (String[]) session.getAttribute("nationalities");
		String[] berths = (String[]) session.getAttribute("berths");

		boolean isAutoUpgrade = (boolean) session.getAttribute("autoUpgrade");

		Set<Passenger> passengerDetails = new HashSet<>();

		for (int i = 0; i < names.length; i++) {

			Passenger newPassenger;
			switch (berths[i]) {

			case "Lower" -> {
				newPassenger = new Passenger(names[i], "LB", Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i], isAutoUpgrade);
			}
			case "Middle" -> {
				newPassenger = new Passenger(names[i], "MB", Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i], isAutoUpgrade);
			}
			case "Upper" -> {
				newPassenger = new Passenger(names[i], "UB", Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i], isAutoUpgrade);
			}
			case "Side Lower" -> {
				newPassenger = new Passenger(names[i], "SL", Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i], isAutoUpgrade);
			}
			case "Side Upper" -> {
				newPassenger = new Passenger(names[i], "SU", Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i], isAutoUpgrade);
			}
			default -> {
				newPassenger = new Passenger(names[i], berths[i], Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i], isAutoUpgrade);
			}
			}

			passengerDetails.add(newPassenger);

		}
		return passengerDetails;
	}

	// ---------------------------------------------------------
	// 2. Handle PayPal (GET Request - Redirect from PayPal)
	// ---------------------------------------------------------
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {

		String paymentSource = request.getParameter("source"); // We passed ?source=PAYPAL in the URL
		String token = request.getParameter("token"); // This is the PayPal Order ID

		HttpSession session = request.getSession();

		String userID = (String) session.getAttribute("user_name");

		String travelDate = (String) session.getAttribute("travelDate");
		String trainName = (String) session.getAttribute("trainName");
		String trainId = (String) session.getAttribute("trainId");
		String source = (String) session.getAttribute("source");
		String destination = (String) session.getAttribute("destination");

		String classType = (String) session.getAttribute("classType");
		boolean isAutoUpgrade = (boolean) session.getAttribute("autoUpgrade");

		String mobile = (String) session.getAttribute("mobile");
		String email = (String) session.getAttribute("email");

		Double totalAmount = (Double) session.getAttribute("total");

		Set<Passenger> passengerDetails = getPassengerSet(request);

		if ("PAYPAL".equals(paymentSource) && token != null) {
			try {
				// 1. Initialize Client
				PayPalHttpClient client = PayPalClient.client();

				// 2. Capture the Order (Actually take the money)
				// The 'token' param from the URL is the Order ID
				OrdersCaptureRequest captureRequest = new OrdersCaptureRequest(token);
				captureRequest.requestBody(new OrderRequest());

				HttpResponse<Order> responseApi = client.execute(captureRequest);

				// 3. Check Status
				if (responseApi.statusCode() == 201) { // 201 Created = Success
					Order order = responseApi.result();
					String transactionId = order.id(); // PayPal Transaction ID

					// 4. Set Attributes for Success Page
					request.setAttribute("payment_id", transactionId);
					request.setAttribute("order_id", token);
					request.setAttribute("status", "SUCCESS");
					request.setAttribute("source", "PayPal");

					Ticket userTicket = TicketBookingHelper.bookTicket(travelDate, passengerDetails, mobile, email,
							trainName, trainId, totalAmount, source, destination, classType, isAutoUpgrade);

					RequestDispatcher requestDispatcher = request.getRequestDispatcher("TicketBookingConfirmation.jsp");
					if (userTicket == null) {
						request.setAttribute("errorMessage", "Ticket booking failed. Seats may not be available.");
						userTicket = new Ticket(travelDate, trainId, trainName, classType, source, destination,
								Status.NOT_APPLICAPLE.name(), null, null, totalAmount);
						TicketBookingHelper.storeFailureBookingTransactionAmounInDB(userTicket, userID, mobile, email);
						requestDispatcher.forward(request, response);
					} else {
						request.setAttribute("ConfirmedTicket", userTicket);
						TicketBookingHelper.storeConfirmedTicketInDB(userTicket, userID, mobile, email);
						requestDispatcher.forward(request, response);

					}

				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "PayPal Capture Failed");
				}

			} catch (Exception e) {
				e.printStackTrace();
				try {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "PayPal Error: " + e.getMessage());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else if ("CASHFREE".equals(paymentSource)) {
			String orderId = request.getParameter("order_id");

			if (orderId == null) {
				try {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID missing");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}

			// 1. Verify Status with Cashfree Server
			Cashfree cashfree = new Cashfree();
			String apiVersion = "2023-08-01"; // Must match your setup

			// Fetch Order Details
			ApiResponse<OrderEntity> apiResponse = null;
			try {
				apiResponse = cashfree.PGFetchOrder(apiVersion, orderId, null, null, null);
			} catch (ApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OrderEntity order = apiResponse.getData();

			RequestDispatcher requestDispatcher = request.getRequestDispatcher("TicketBookingConfirmation.jsp");
			// 2. Check if PAID
			try {
				if ("PAID".equals(order.getOrderStatus())) {
					request.setAttribute("status", "SUCCESS");
					request.setAttribute("source", "Cashfree");
					request.setAttribute("order_id", orderId);
					request.setAttribute("payment_id", order.getCfOrderId()); // or payment_session_id

					Ticket userTicket = TicketBookingHelper.bookTicket(travelDate, passengerDetails, mobile, email,
							trainName, trainId, totalAmount, source, destination, classType, isAutoUpgrade);

					if (userTicket == null) {
						request.setAttribute("errorMessage", "Ticket booking failed. Seats may not be available.");
						userTicket = new Ticket(travelDate, trainId, trainName, classType, source, destination,
								Status.NOT_APPLICAPLE.name(), null, null, totalAmount);
						TicketBookingHelper.storeFailureBookingTransactionAmounInDB(userTicket, userID, mobile, email);
						requestDispatcher.forward(request, response);
					} else {
						request.setAttribute("ConfirmedTicket", userTicket);
						TicketBookingHelper.storeConfirmedTicketInDB(userTicket, userID, mobile, email);
						requestDispatcher.forward(request, response);

					}
				} else {
					request.setAttribute("errorMessage",
							"Payment failed With Cash Free Try Different Payment GateWay.");
					requestDispatcher.forward(request, response);

				}
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			try {
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Invalid Access");
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}

	}

}
