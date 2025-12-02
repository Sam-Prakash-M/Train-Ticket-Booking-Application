package com.samprakash.baseview;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) {
	    String trainId = request.getParameter("trainId");
	    String trainName = request.getParameter("trainName");
	    String classType = request.getParameter("classType");
	    String source = request.getParameter("source");
	    String destination = request.getParameter("destination");
	    String fare = request.getParameter("fare");

	    // NEW ARRIVAL/DEPARTURE FIELDS
	    String sourceArrival = request.getParameter("sourceArrival");
	    String sourceDeparture = request.getParameter("sourceDeparture");
	    String destinationArrival = request.getParameter("destinationArrival");
	    String destinationDeparture = request.getParameter("destinationDeparture");
	    String travelDate = request.getParameter("travelDate");


	    System.out.println("Secure Booking:");
	    System.out.println("Travel Date : "+travelDate);
	    System.out.println(trainName + " ("+ trainId + ")");
	    System.out.println("From: " + source + " ("+sourceDeparture+") ➜ "
	                       + destination + " ("+destinationArrival+")");
	    System.out.println("Fare: ₹" + fare);

	    // SET ATTRIBUTES FOR CONFIRMATION PAGE
	    request.setAttribute("trainId", trainId);
	    request.setAttribute("trainName", trainName);
	    request.setAttribute("classType", classType);
	    request.setAttribute("source", source);
	    request.setAttribute("destination", destination);
	    request.setAttribute("fare", fare);

	    // NEW VALUES
	    request.setAttribute("sourceArrival", sourceArrival);
	    request.setAttribute("sourceDeparture", sourceDeparture);
	    request.setAttribute("destinationArrival", destinationArrival);
	    request.setAttribute("destinationDeparture", destinationDeparture);
	    request.setAttribute("travelDate", travelDate);

	    try {
	        RequestDispatcher rd = request.getRequestDispatcher("Confirmation.jsp");
	        rd.forward(request, response);
	    } catch (IOException | ServletException e) {
	        e.printStackTrace();
	    }
	}

}
