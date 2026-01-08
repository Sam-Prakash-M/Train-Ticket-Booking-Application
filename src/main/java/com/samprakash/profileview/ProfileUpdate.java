package com.samprakash.profileview;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/ProfileUpdate")
public class ProfileUpdate extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	protected void doPost(HttpServletRequest request,HttpServletResponse response) {
		
		
		String userName = request.getParameter("user_name");
		
		
		
		
	}

}
