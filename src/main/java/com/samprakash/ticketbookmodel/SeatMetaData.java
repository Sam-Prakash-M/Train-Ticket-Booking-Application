package com.samprakash.ticketbookmodel;

public record SeatMetaData(String classType,String coachNo,byte seatNumber) {

	public String getCoachNo() {
		return coachNo;
	}

	public byte getSeatNumber() {
		return seatNumber;
	}
	
	public String getClassType() {
		return classType;
	}

}
