package com.samprakash.ticketbookviewmodel;

import java.util.Set;

import com.samprakash.paymentmodel.Passenger;
import com.samprakash.repository.DataBaseConnector;
import com.samprakash.ticketbookmodel.SeatMetaData;

public class TicketBookingHelper {

	public static void bookTicket(Set<Passenger> passengerDetails, String mobile, String email, String trainName,
			String trainId, Double totalAmount, String source, String destination, String classType,
			boolean isAutoUpgrade) {
		
		
		for(Passenger passenger : passengerDetails) {
			SeatMetaData ticket = getTicketForTheUser(trainId,trainName,source,destination,classType,passenger.getPreference());
			
		}
		
		
		
	}

	private static SeatMetaData getTicketForTheUser(String trainId, String trainName, String source, String destination,
			String classType,String passengerPreference) {
		
		
		DataBaseConnector databaseConnector = DataBaseConnector.getInstance();
		
		return null;
	}

	

	
	
}
