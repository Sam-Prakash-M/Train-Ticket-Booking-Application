package com.samprakash.ticketbookviewmodel;

import java.util.List;
import java.util.Set;

import com.samprakash.basemodel.Status;
import com.samprakash.exception.SeatNotAvailableException;
import com.samprakash.paymentmodel.Passenger;
import com.samprakash.repository.DataBaseConnector;
import com.samprakash.ticketbookmodel.BookingData;
import com.samprakash.ticketbookmodel.Ticket;

public class TicketBookingHelper {

	private final static DataBaseConnector DATA_BASE_CONNECTOR = DataBaseConnector.getInstance();

	public static Ticket bookTicket(String travelDate, Set<Passenger> passengerDetails, String mobile, String email,
			String trainName, String trainId, Double totalAmount, String source, String destination, String classType,
			boolean isAutoUpgrade) {

		Ticket confirmedTicket = null;
		try {
			confirmedTicket = DATA_BASE_CONNECTOR.getConfirmedTicketForAllPassenger(passengerDetails, trainId,
					trainName, source, destination, classType, travelDate, totalAmount);
		} catch (SeatNotAvailableException e) {

			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(confirmedTicket);

		return confirmedTicket;

	}

	public static void storeConfirmedTicketInDB(Ticket userTicket, String userID,String mobile,String email) {

		BookingData bookingData = new BookingData(userID, userTicket.bookingDate(), userTicket.trainId(),
				userTicket.trainName(), userTicket.className(), userTicket.pnrNumber(), userTicket.sourceArr(),
				userTicket.destinationArr(), userTicket.transactionId(), userTicket.totalFare(), Status.SUCCESS.name(),
				Status.SUCCESS.name());

		DATA_BASE_CONNECTOR.storeBookingStateInDB(bookingData,userTicket.associatedPassenger(),mobile,email);
	}

	public static void storeFailureBookingTransactionAmounInDB(Ticket userTicket, String userID,String mobile,String email) {
		BookingData bookingData = new BookingData(userID, userTicket.bookingDate(), userTicket.trainId(),
				userTicket.trainName(), userTicket.className(), Status.NOT_APPLICAPLE.name(), userTicket.sourceArr(),
				userTicket.destinationArr(), userTicket.transactionId(), userTicket.totalFare(), Status.FAILURE.name(),
				Status.SUCCESS.name());

		DATA_BASE_CONNECTOR.storeBookingStateInDB(bookingData,null,mobile,email);

	}

	public static Ticket getTicketByPNR(String pnrNumber) {
		
		
		
		return DATA_BASE_CONNECTOR.getTicketByPNR(pnrNumber);
	}

	public static List<BookingData>  getCurrentUserBooking(String userName) {
		
		
		return DATA_BASE_CONNECTOR.getCurrentUserBooking(userName);
		
	}

}
