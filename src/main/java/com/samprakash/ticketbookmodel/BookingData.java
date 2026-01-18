package com.samprakash.ticketbookmodel;

import java.util.List;

import com.samprakash.paymentmodel.Passenger;

public class BookingData {

	private String userName;
	private String travelDate;
	private String trainId;
	private String traiName;
	private String classType;
	private String pnrNo;
	private String source;
	private String destination;
	private String transactionId;
	private double totalFare;
	private String bookingStatus;
	private String transactionStatus;
	private List<Passenger> associatedPassenger;

	@Override
	public String toString() {
		return "BookingData [userName=" + userName + ", travelDate=" + travelDate + ", trainId=" + trainId
				+ ", traiName=" + traiName + ", classType=" + classType + ", pnrNo=" + pnrNo + ", source=" + source
				+ ", destination=" + destination + ", transactionId=" + transactionId + ", totalFare=" + totalFare
				+ ", bookingStatus=" + bookingStatus + ", transactionStatus=" + transactionStatus
				+ ", associatedPassenger=" + associatedPassenger + "]";
	}

	// ✅ No-Arg Constructor (MANDATORY for JSP & frameworks)
	public BookingData() {

	}

	// ✅ All-Arg Constructor
	public BookingData(String userName, String travelDate, String trainId, String traiName, String classType,
			String pnrNo, String source, String destination, String transactionId, double totalFare,
			String bookingStatus, String transactionStatus) {

		this.userName = userName;
		this.travelDate = travelDate;
		this.trainId = trainId;
		this.traiName = traiName;
		this.classType = classType;
		this.pnrNo = pnrNo;
		this.source = source;
		this.destination = destination;
		this.transactionId = transactionId;
		this.totalFare = totalFare;
		this.bookingStatus = bookingStatus;
		this.transactionStatus = transactionStatus;
	}

	// ✅ GETTERS & SETTERS (JSP Compatible)

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTravelDate() {
		return travelDate;
	}

	public void setTravelDate(String travelDate) {
		this.travelDate = travelDate; // 2025-12-09
	}

	public String getTrainId() {
		return trainId;
	}

	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}

	public String getTraiName() {
		return traiName;
	}

	public void setTraiName(String traiName) {
		this.traiName = traiName;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getPnrNo() {
		return pnrNo;
	}

	public void setPnrNo(String pnrNo) {
		this.pnrNo = pnrNo;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public double getTotalFare() {
		return totalFare;
	}

	public void setTotalFare(double totalFare) {
		this.totalFare = totalFare;
	}

	public String getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public List<Passenger> getAssociatedPassenger() {
		return associatedPassenger;
	}

	public void setAssociatedPassenger(List<Passenger> associatedPassenger) {
		this.associatedPassenger = associatedPassenger;
	}
}
