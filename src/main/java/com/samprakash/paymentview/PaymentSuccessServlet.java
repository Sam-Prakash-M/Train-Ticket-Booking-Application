package com.samprakash.paymentview;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

		String[] names = (String[]) session.getAttribute("names");
		String[] ages = (String[]) session.getAttribute("ages");
		String[] genders = (String[]) session.getAttribute("genders");
		String[] nationalities = (String[]) session.getAttribute("nationalities");
		String[] berths = (String[]) session.getAttribute("berths");

		String travelDate = (String) session.getAttribute("travelDate");
		String trainName = (String) session.getAttribute("trainName");
		String trainId = (String) session.getAttribute("trainId");
		String source = (String) session.getAttribute("source");
		String destination = (String) session.getAttribute("destination");
		String sourceDeparture = (String) session.getAttribute("sourceDeparture");
		String sourceArrival = (String) session.getAttribute("sourceArrival");
		String destinationArrival = (String) session.getAttribute("destinationArrival");
		String destinationDeparture = (String) session.getAttribute("destinationDeparture");
		String classType = (String) session.getAttribute("classType");
		boolean isAutoUpgrade = (boolean) session.getAttribute("autoUpgrade");

		String mobile = (String) session.getAttribute("mobile");
		String email = (String) session.getAttribute("email");

		Double totalAmount = (Double) session.getAttribute("total");

		Set<Passenger> passengerDetails = new HashSet<>();

		for (int i = 0; i < names.length; i++) {

			Passenger newPassenger;
			switch (berths[i]) {

			case "Lower" -> {
				newPassenger = new Passenger(names[i], "LB", Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i]);
			}
			case "Middle" -> {
				newPassenger = new Passenger(names[i], "MB", Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i]);
			}
			case "Upper" -> {
				newPassenger = new Passenger(names[i], "UB", Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i]);
			}
			case "Side Lower" -> {
				newPassenger = new Passenger(names[i], "SL", Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i]);
			}
			case "Side Upper" -> {
				newPassenger = new Passenger(names[i], "SU", Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i]);
			}
			default -> {
				newPassenger = new Passenger(names[i], berths[i], Byte.parseByte(ages[i]), genders[i].charAt(0),
						nationalities[i]);
			}
			}

			passengerDetails.add(newPassenger);

		}
		System.out.println("Travel Date in SUccess : " + travelDate);

		Ticket userTicket = TicketBookingHelper.bookTicket(travelDate, passengerDetails, mobile, email, trainName,
				trainId, totalAmount, source, destination, classType, isAutoUpgrade);

		RequestDispatcher requestDispatcher = request.getRequestDispatcher("TicketBookingConfirmation.jsp");

		try {

			if (userTicket == null) {
				request.setAttribute("errorMessage", "Ticket booking failed. Seats may not be available.");
				requestDispatcher.forward(request, response);
			} else {
				session.setAttribute("ConfirmedTicket", userTicket);
				request.setAttribute("ConfirmedTicket", userTicket);
				requestDispatcher.forward(request, response);
			}

		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
