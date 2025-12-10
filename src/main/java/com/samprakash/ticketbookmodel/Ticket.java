package com.samprakash.ticketbookmodel;

import java.util.Set;

import com.samprakash.paymentmodel.Passenger;

public record Ticket(String bookingDate,String trainId, String trainName, String className, String sourceArr, String destinationArr,
		String pnrNumber, String transactionId, Set<Passenger> associatedPassenger,double totalFare) {
	// JavaBean-style getters for JSP EL compatibility
	public String getTrainId() {
		return trainId;
	}

	public String getTrainName() {
		return trainName;
	}

	public String getClassName() {
		return className;
	}

	public String getSourceArr() {
		return sourceArr;
	}

	public String getBookingDate() {
		return bookingDate;
	}

	public String getDestinationArr() {
		return destinationArr;
	}

	public String getPnrNumber() {
		return pnrNumber;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public Set<Passenger> getAssociatedPassenger() {
		return associatedPassenger;
	}
}
