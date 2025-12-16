package com.samprakash.cancelticketviewmodel;

import java.util.PriorityQueue;
import java.util.Queue;

import com.samprakash.paymentmodel.Passenger;
import com.samprakash.repository.DataBaseConnector;

public class CancelTicketViewModel {

	
	
	public static Queue<Passenger> getRacAndWLPassengerList(String trainName,String trainId) {
		
		Queue<Passenger> passengerCount = new PriorityQueue<>();
		
		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
	
		return dataBaseConnector.getRacAndWLPassengerList(trainName,trainId);
	}
	
	
	//private static BOOKING
}
