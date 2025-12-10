package com.samprakash.ticketbookmodel;

public record SeatMetaData(String coachNo,byte seatNumber) {

	public String getCoachNo() {
		return coachNo;
	}

	public byte getSeatNumber() {
		return seatNumber;
	}

}
