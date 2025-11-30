package com.samprakash.ticketbookmodel;

import java.util.Set;

import com.samprakash.paymentmodel.Passenger;

public record Ticket(String trainId,String trainName,String className,String sourceArr,String destinationArr,String pnrNumber,String transactionId,
		Set<Passenger> associatedPassenger) {

}
