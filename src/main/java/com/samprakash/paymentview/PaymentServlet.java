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

        String[] names = request.getParameterValues("pname[]");
        String[] ages = request.getParameterValues("page[]");
        String[] genders = request.getParameterValues("pgender[]");
        String[] nationalities = request.getParameterValues("pnationality[]");
        String[] berths = request.getParameterValues("berth[]");

        String mobile = request.getParameter("mobile");
        String email = request.getParameter("email");

        double fare = Double.parseDouble(request.getParameter("fare"));

        // EXTRA CHARGES
        double gst = fare * 0.05;      // 5% GST
        double serviceCharge = 20.0;   // fixed or dynamic
        double totalPayable = fare + gst + serviceCharge;

        request.setAttribute("names", names);
        request.setAttribute("ages", ages);
        request.setAttribute("genders", genders);
        request.setAttribute("nationalities", nationalities);
        request.setAttribute("berths", berths);

        request.setAttribute("mobile", mobile);
        request.setAttribute("email", email);

        request.setAttribute("fare", fare);
        request.setAttribute("gst", gst);
        request.setAttribute("serviceCharge", serviceCharge);
        request.setAttribute("totalPayable", totalPayable);

        try {
            request.getRequestDispatcher("payment.jsp").forward(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }
}
