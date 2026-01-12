package com.samprakash.pnrviewmodel;

import com.samprakash.repository.DataBaseConnector;
import com.samprakash.ticketbookmodel.Ticket;

public class PnrStatusViewModel {
	
	
	public static Ticket getBookingDetails(String pnrNumber) {
		
		
		
		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		
		return dataBaseConnector.getTicketByPNR(pnrNumber);
	}

}
