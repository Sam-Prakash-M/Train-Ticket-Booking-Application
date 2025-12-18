package com.samprakash.cancelticketviewmodel;

import java.util.Map;

import com.samprakash.paymentmodel.Passenger;
import com.samprakash.repository.DataBaseConnector;

public class CancelTicketViewModel {

	public static void CancelTicket(String pnrNumber, Map<String, Passenger> passengerToCancel) {

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();

		dataBaseConnector.cancelPassengerTickets(pnrNumber, passengerToCancel);
		dataBaseConnector.findRacAndWlPassengerIfExists(pnrNumber,passengerToCancel);

	
	}

}
