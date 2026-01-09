package com.samprakash.cancelticketview;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samprakash.cancelticketviewmodel.CancelTicketViewModel;
import com.samprakash.paymentmodel.Passenger;
import com.samprakash.ticketbookmodel.SeatMetaData;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CancelTicketServlet")
public class CancelTicketServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		String[] selectedPassengersJson = (String[]) request.getParameterValues("selectedPassengers");

		String pnrNumber = (String) request.getParameter("pnr");

		Map<String,Passenger> passengerToCancel = new TreeMap<>();
		for (String jsonString : selectedPassengersJson) {
			// Parse String to JsonObject
			JsonObject passengerObj = JsonParser.parseString(jsonString).getAsJsonObject();

			String name = passengerObj.get("name").getAsString();
			String age = passengerObj.get("age").getAsString();
			String gender = passengerObj.get("gender").getAsString();
			String status = passengerObj.get("status").getAsString();
			String seatNumber = passengerObj.get("seatNumber").getAsString();
			String classType = passengerObj.get("classType").getAsString();
			Passenger passenger = new Passenger(name,null,Byte.parseByte(age),gender.charAt(0),null,false);
			passenger.setTicketStatus(status.split("/")[0]);
			String [] coachAndSeatNumber = seatNumber.split("-");
			SeatMetaData seatMetaData = new SeatMetaData(classType,coachAndSeatNumber[0],Byte.parseByte(coachAndSeatNumber[1])); 
			passenger.setSeatMetaData(seatMetaData);
			passengerToCancel.put(status,passenger);
			System.out.println(passenger);

		}
		CancelTicketViewModel.CancelTicket(pnrNumber,passengerToCancel);
		
		response.setStatus(HttpServletResponse.SC_OK);
        try {
			response.getWriter().write("Cancellation Processed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
