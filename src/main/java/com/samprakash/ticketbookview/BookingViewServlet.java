package com.samprakash.ticketbookview;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.samprakash.ticketbookmodel.BookingData;
import com.samprakash.ticketbookviewmodel.TicketBookingHelper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/MyBookings")
public class BookingViewServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

	    HttpSession session = request.getSession(false);
	    String userName = (String) session.getAttribute("user_name");

	    List<BookingData> allBookings =
	            TicketBookingHelper.getCurrentUserBooking(userName);

	    List<BookingData> upcomingBookings = new ArrayList<>();
	    List<BookingData> pastBookings = new ArrayList<>();

	    LocalDate today = LocalDate.now();

	    for (BookingData b : allBookings) {
	        if (!LocalDate.parse(b.getTravelDate()).isBefore(today)) {
	            upcomingBookings.add(b);   // ✅ Today or Future
	        } else {
	            pastBookings.add(b);       // ✅ Older than today
	        }
	    }

	    request.setAttribute("allBookings", allBookings);
	    request.setAttribute("upcomingBookings", upcomingBookings);
	    request.setAttribute("pastBookings", pastBookings);
	    
	    System.out.println("Upcoming : "+upcomingBookings.size());
	    System.out.println("PastBookings : "+pastBookings.size());

	    RequestDispatcher rd = request.getRequestDispatcher("UserBookings.jsp");
	    try {
			rd.forward(request, response);
		} catch (ServletException | IOException e) {
			
			e.printStackTrace();
		}
	}

}
