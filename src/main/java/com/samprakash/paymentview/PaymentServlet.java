package com.samprakash.paymentview;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
        
        String travelDate = request.getParameter("travelDate");
        String trainName = request.getParameter("trainName");
        String trainId = request.getParameter("trainId");
        String source = request.getParameter("source");
        String destination = request.getParameter("destination");
        String sourceDeparture = request.getParameter("sourceDeparture");
        String sourceArrival = request.getParameter("sourceArrival");
        String destinationArrival = request.getParameter("destinationArrival");
        String destinationDeparture = request.getParameter("destinationDeparture");
        String classType = request.getParameter("classType");
        boolean isPassengerGivenAutoUpgrade = request.getParameter("autoUpgrade") == null ? false : true ;


        String mobile = request.getParameter("mobile");
        String email = request.getParameter("email");

        int numberOfPerson = names.length;
        System.out.println("Passenger Count : "+numberOfPerson);
        System.out.println("Currency : "+request.getParameter("fare"));
        double totalFare = Double.parseDouble(request.getParameter("fare")) * numberOfPerson;
        System.out.println("Servlet Total Fare : "+totalFare);

        // EXTRA CHARGES
        double gst = totalFare * 0.05;      // 5% GST
        double serviceCharge = 20.0;   // fixed or dynamic
       
        double totalPayable = totalFare + gst + serviceCharge;

        request.setAttribute("names", names);
        request.setAttribute("ages", ages);
        request.setAttribute("genders", genders);
        request.setAttribute("nationalities", nationalities);
        request.setAttribute("berths", berths);

        request.setAttribute("mobile", mobile);
        request.setAttribute("email", email);

        request.setAttribute("totalFare", totalFare);
        request.setAttribute("gst", gst);
        request.setAttribute("serviceCharge", serviceCharge);
        request.setAttribute("totalPayable", totalPayable);
        request.setAttribute("travelDate", travelDate);
        
        HttpSession session = request.getSession();

        session.setAttribute("travelDate", travelDate);
        session.setAttribute("names", names);
        session.setAttribute("ages", ages);
        session.setAttribute("genders", genders);
        session.setAttribute("nationalities", nationalities);
        session.setAttribute("berths", berths);

        session.setAttribute("mobile", mobile);
        session.setAttribute("email", email);

        session.setAttribute("totalFare", totalFare);
        session.setAttribute("gst", gst);
        session.setAttribute("serviceCharge", serviceCharge);
        session.setAttribute("total", totalPayable);
        session.setAttribute("trainName", trainName);
        session.setAttribute("trainId", trainId);
        session.setAttribute("source", source);
        session.setAttribute("destination", destination);
        session.setAttribute("sourceDeparture", sourceDeparture);
        session.setAttribute("sourceArrival", sourceArrival);
        session.setAttribute("destinationArrival", destinationArrival);
        session.setAttribute("destinationDeparture", destinationDeparture);
        session.setAttribute("classType", classType);
        session.setAttribute("autoUpgrade", isPassengerGivenAutoUpgrade);



        try {
            request.getRequestDispatcher("payment.jsp").forward(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }
}
