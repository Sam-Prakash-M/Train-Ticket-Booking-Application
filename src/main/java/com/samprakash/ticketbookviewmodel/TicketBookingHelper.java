package com.samprakash.ticketbookviewmodel;

import java.util.Set;

import com.samprakash.paymentmodel.Passenger;
import com.samprakash.repository.DataBaseConnector;
import com.samprakash.ticketbookmodel.Ticket;

public class TicketBookingHelper {

	public static void bookTicket(Set<Passenger> passengerDetails, String mobile, String email, String trainName,
			String trainId, Double totalAmount, String source, String destination, String classType,
			boolean isAutoUpgrade) {
		
		
		// Need to Get via Servlet
		String travelDate = null;
		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		
		Ticket confirmedTicket = dataBaseConnector.getConfirmedTicektForAllPassenger(passengerDetails,trainId,trainName,
				source,destination,classType,travelDate);
		
		
		
		
		
		
	}


	

	
	
}
