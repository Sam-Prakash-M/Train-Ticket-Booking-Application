package com.samprakash.ticketbookview;

import java.io.IOException;

import com.samprakash.ticketbookmodel.Ticket;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/PrintTicket")
public class PrintTicketServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Ticket ticket = (Ticket) session.getAttribute("ConfirmedTicket");

        if (ticket == null) {
            response.getWriter().println("Error: No ticket found in session.");
            return;
        }

        request.setAttribute("ticket", ticket);
        request.getRequestDispatcher("PrintTicket.jsp").forward(request, response);
    }
}


