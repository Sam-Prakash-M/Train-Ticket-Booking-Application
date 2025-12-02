package com.samprakash.ticketbookviewmodel;

import java.util.Set;

import com.samprakash.paymentmodel.Passenger;
import com.samprakash.repository.DataBaseConnector;
import com.samprakash.ticketbookmodel.Ticket;

public class TicketBookingHelper {

	public static Ticket bookTicket(String travelDate,Set<Passenger> passengerDetails, String mobile, String email, String trainName,
			String trainId, Double totalAmount, String source, String destination, String classType,
			boolean isAutoUpgrade) {
		
		
		
	
		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		
		Ticket confirmedTicket = dataBaseConnector.getConfirmedTicketForAllPassenger(passengerDetails,trainId,trainName,
				source,destination,classType,travelDate);
		
		System.out.println(confirmedTicket);
		
		return confirmedTicket;
		
		
	}


	

	
	
}
