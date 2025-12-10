package com.samprakash.ticketbookview;

import java.io.IOException;

import com.samprakash.ticketbookmodel.Ticket;
import com.samprakash.ticketbookviewmodel.TicketBookingHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/PrintTicket")
public class PrintTicketServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String pnr = request.getParameter("pnr");

		if (pnr == null || pnr.isEmpty()) {
			response.getWriter().println("Invalid PNR.");
			return;
		}

		// ✅ Fetch ticket freshly from DB using PNR
		Ticket ticket = TicketBookingHelper.getTicketByPNR(pnr);

		if (ticket == null) {
			response.getWriter().println("Ticket not found.");
			return;
		}

		request.setAttribute("ticket", ticket);
		request.getRequestDispatcher("PrintTicket.jsp").forward(request, response);
	}
}
