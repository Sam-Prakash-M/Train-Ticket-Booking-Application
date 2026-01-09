package com.samprakash.profileview;

import com.samprakash.profileviewmodel.ProfileUpdateViewModel;

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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {

		String userName = request.getParameter("UserName");
		String fullName = request.getParameter("FullName");
		String email = request.getParameter("Email");
		String contactNo = request.getParameter("ContactNo");
		
		if(ProfileUpdateViewModel.updatePassengerDetails(userName, fullName, email, contactNo)) {
			
			System.out.println("Updated Passenger Details Successfully");
		}
		else {
			System.out.println("Failed to Update Passenger Details");
		}
		

	}

}
