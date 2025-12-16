package com.samprakash.cancelticketview;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samprakash.PassengerDTO;

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

		

			for (String jsonString : selectedPassengersJson) {
			    // Parse String to JsonObject
			    JsonObject passengerObj = JsonParser.parseString(jsonString).getAsJsonObject();
			    
			    String name = passengerObj.get("name").getAsString();
			    String age = passengerObj.get("age").getAsString();
			    String gender = passengerObj.get("gender").getAsString();
			    String status = passengerObj.get("status").getAsString();
			    String seatNumber = passengerObj.get("seatNumber").getAsString();
			    String pnrNumber = passengerObj.get("pnr").getAsString();
			    String classType = passengerObj.get("classType").getAsString();
			    
			    PassengerDTO passengerDTO = new PassengerDTO(name, age, gender, status, seatNumber, pnrNumber, classType);
			    
			    System.out.println(passengerDTO);
			    
			   
			}

		

	}

}
