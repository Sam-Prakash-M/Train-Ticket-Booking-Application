package com.samprakash.cancelticketview;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/CancelTicketServlet")
public class CancelTicketServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) {
		
		String pnrNumber = (String)request.getAttribute("pnr");
		
		
		
		
		
		
	}

	
	
}
