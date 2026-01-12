package com.samprakash.pnrview;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.samprakash.pnrviewmodel.PnrStatusViewModel;
import com.samprakash.ticketbookmodel.Ticket;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/PnrStatus")
public class PnrStatusView extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // Just load the page initially
        forwardToPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        
        String pnrInput = request.getParameter("pnrInput");

        if (pnrInput != null && !pnrInput.trim().isEmpty()) {
            
            // 1. Fetch Ticket
            Ticket ticket = PnrStatusViewModel.getBookingDetails(pnrInput.trim());

            if (ticket == null) {
                request.setAttribute("error", "Invalid PNR Number. Please check and try again.");
                request.setAttribute("pnrStatus", "INVALID");
            } else {
                request.setAttribute("pnrTicket", ticket);
                
                // 2. Check Date for "Flushed/Travelled" Logic
                try {
                    // Assuming ticket.getBookingDate() returns "YYYY-MM-DD" (Journey Date)
                    LocalDate journeyDate = LocalDate.parse(ticket.getBookingDate());
                    LocalDate today = LocalDate.now();

                    if (journeyDate.isBefore(today)) {
                        request.setAttribute("pnrStatus", "FLUSHED");
                        request.setAttribute("statusMsg", "Journey Completed / Chart Flushed");
                    } else if (journeyDate.isEqual(today)) {
                        request.setAttribute("pnrStatus", "CHARTING");
                        request.setAttribute("statusMsg", "Chart Prepared");
                    } else {
                        request.setAttribute("pnrStatus", "LIVE");
                        request.setAttribute("statusMsg", "Current Status");
                    }
                } catch (DateTimeParseException e) {
                    // Fallback if date parsing fails
                    request.setAttribute("pnrStatus", "LIVE");
                }
            }
        }
        forwardToPage(request, response);
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response) {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("PnrStatusView.jsp");
        try {
            requestDispatcher.forward(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }
}