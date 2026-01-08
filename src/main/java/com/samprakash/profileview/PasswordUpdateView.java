package com.samprakash.profileview;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/PasswordUpdate")
public class PasswordUpdateView extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	protected void doPost(HttpServletRequest request,HttpServletResponse response) {
		
		String newPassword = request.getParameter("newPassword");
		
		
		
	}

}
