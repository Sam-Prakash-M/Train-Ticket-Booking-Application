package com.samprakash.ticketbookmodel;

public record BookingData(String userName, String travelDate, String trainId, String traiName,String classType, String pnrNo,
		String source, String destination, String transactionId, double totalFare, String bookingStatus,
		String transactionStatus) {

}
