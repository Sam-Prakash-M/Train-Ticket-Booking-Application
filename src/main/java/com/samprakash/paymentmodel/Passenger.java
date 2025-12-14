package com.samprakash.paymentmodel;

import com.samprakash.ticketbookmodel.SeatMetaData;

public class Passenger implements Comparable<Passenger> {

	private String name;
	private String preference;
	private byte age;
	private char gender;
	private String nationalities;
	private SeatMetaData seatMetaData;
	private boolean isAutoUpgrade;
	private String ticketStatus = "CNF";

	public boolean isAutoUpgrade() {
		return isAutoUpgrade;
	}

	public void setAutoUpgrade(boolean isAutoUpgrade) {
		this.isAutoUpgrade = isAutoUpgrade;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public SeatMetaData getSeatMetaData() {
		return seatMetaData;
	}

	public void setSeatMetaData(SeatMetaData seatMetaData) {
		this.seatMetaData = seatMetaData;
	}

	public Passenger(String name, String preference, byte age, char gender, String nationalities,
			boolean isAutoUpgrade) {

		this.name = name;
		this.preference = preference;
		this.age = age;
		this.gender = gender;
		this.nationalities = nationalities;
		this.isAutoUpgrade = isAutoUpgrade;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPreference() {
		return preference;
	}

	public void setPreference(String preference) {
		this.preference = preference;
	}

	public short getAge() {
		return age;
	}

	public void setAge(byte age) {
		this.age = age;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public String getNationalities() {
		return nationalities;
	}

	public void setNationalities(String nationalities) {
		this.nationalities = nationalities;
	}

	@Override
	public String toString() {
		return "Passenger [name=" + name + ", preference=" + preference + ", age=" + age + ", gender=" + gender
				+ ", nationalities=" + nationalities + ", seatMetaData=" + seatMetaData + "]";
	}

	@Override
	public int compareTo(Passenger thatPassenger) {

		if (this.ticketStatus == thatPassenger.ticketStatus) {

			return Integer.compare(this.seatMetaData.seatNumber(), this.seatMetaData.seatNumber());
		}
		return this.ticketStatus.compareTo(thatPassenger.ticketStatus);
	}

}
