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
	        String classType = request.getParameter("classType");
	        String source = request.getParameter("source");
	        String destination = request.getParameter("destination");
	        String fare = request.getParameter("fare");

	        System.out.println("Secure Booking:");
	        System.out.println("Train: " + trainId + " | Class: " + classType);
	        System.out.println("From: " + source + " ➜ " + destination);
	        System.out.println("Fare: ₹" + fare);

	        // Forward to confirmation page
	        request.setAttribute("trainId", trainId);
	        request.setAttribute("classType", classType);
	        request.setAttribute("source", source);
	        request.setAttribute("destination", destination);
	        request.setAttribute("fare", fare);

	        RequestDispatcher rd = request.getRequestDispatcher("Confirmation.jsp");
	        try {
				rd.forward(request, response);
			} catch (ServletException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	}
}
