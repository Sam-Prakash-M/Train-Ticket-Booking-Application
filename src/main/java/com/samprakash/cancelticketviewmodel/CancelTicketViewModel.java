package com.samprakash.cancelticketviewmodel;

import java.util.Map;

import com.samprakash.paymentmodel.Passenger;
import com.samprakash.repository.DataBaseConnector;

public class CancelTicketViewModel {

	public static void CancelTicket(String pnrNumber, Map<String, Passenger> passengerToCancel) {

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();

		System.out.println("list of Passengers..."+passengerToCancel);
		dataBaseConnector.cancelPassengerTickets(pnrNumber, passengerToCancel);
		dataBaseConnector.findRacAndWlPassengerIfExists(pnrNumber,passengerToCancel);

	
	}

}
