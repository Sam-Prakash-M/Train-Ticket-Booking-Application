package com.samprakash.paymentmodel;

import com.samprakash.ticketbookmodel.SeatMetaData;

public class Passenger {

	private String name;
	private String preference;
	private byte age; 
	private char gender;
	private String nationalities;
	private SeatMetaData seatMetaData;
	
	
	public SeatMetaData getSeatMetaData() {
		return seatMetaData;
	}
	public void setSeatMetaData(SeatMetaData seatMetaData) {
		this.seatMetaData = seatMetaData;
	}
	public Passenger(String name,String preference,byte age,char gender,String nationalities) {
		
		this.name = name;
		this.preference = preference;
		this.age = age;
		this.gender = gender;
		this.nationalities = nationalities;
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
	
}
