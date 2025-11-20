package com.samprakash.paymentview;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        // 1. Passenger Details
        String[] names = request.getParameterValues("pname[]");
        String[] ages = request.getParameterValues("page[]");
        String[] genders = request.getParameterValues("pgender[]");
        String[] nationalities = request.getParameterValues("pnationality[]");
        String[] berths = request.getParameterValues("berth[]");

        // 2. Contact Details
        String mobile = request.getParameter("mobile");
        String email = request.getParameter("email");

        // 3. Ticket Fare (sent from JSP as request attribute, so you must send it as hidden field)
        String fareStr = request.getParameter("fare");
        double fare = Double.parseDouble(fareStr);

        // Debug print
        System.out.println("----- PASSENGER LIST -----");

        for (int i = 0; i < names.length; i++) {
            System.out.println("Passenger " + (i + 1));
            System.out.println("Name     : " + names[i]);
            System.out.println("Age      : " + ages[i]);
            System.out.println("Gender   : " + genders[i]);
            System.out.println("Nationality : " + nationalities[i]);
            System.out.println("Berth    : " + berths[i]);
            System.out.println("----------------------	---");
        }

        System.out.println("Mobile: " + mobile);
        System.out.println("Email : " + email);
        System.out.println("Fare  : " + fare);
        // Forward details to payment.jsp
        request.setAttribute("names", names);
        request.setAttribute("ages", ages);
        request.setAttribute("genders", genders);
        request.setAttribute("nationalities", nationalities);
        request.setAttribute("berths", berths);

        request.setAttribute("mobile", mobile);
        request.setAttribute("email", email);
        request.setAttribute("fare", fare);

        try {
            request.getRequestDispatcher("payment.jsp").forward(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }
}
