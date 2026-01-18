package com.samprakash.profileview;

import java.io.IOException;

import com.samprakash.basemodel.Status;
import com.samprakash.basemodel.Users;
import com.samprakash.profileviewmodel.ProfileUpdateViewModel;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ProfileUpdate")
public class ProfileUpdate extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);

		try {
			if (session == null || session.getAttribute("user_name") == null) {
				response.sendRedirect("login.jsp");
				return;
			}

			String userName = (String) session.getAttribute("user_name");

			Users currentUser = ProfileUpdateViewModel.getUserDetails(userName);
			request.setAttribute("currentUser", currentUser);

			RequestDispatcher requestDispatcher = request.getRequestDispatcher("ProfileUpdate.jsp");
			requestDispatcher.forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {

		try {
			HttpSession session = request.getSession(false);
			if (session == null || session.getAttribute("user_name") == null) {
				response.sendRedirect("login.jsp");
				return;
			}

			String userName = (String) session.getAttribute("user_name");
			String fullName = request.getParameter("FullName");
			String email = request.getParameter("Email");
			String contactNo = request.getParameter("ContactNo");

			Status updatedStatus = ProfileUpdateViewModel.updatePassengerDetails(userName, fullName, email, contactNo);

			// 🔁 ALWAYS reload user
			Users currentUser = ProfileUpdateViewModel.getUserDetails(userName);
			request.setAttribute("currentUser", currentUser);

			switch (updatedStatus) {

			case Status.SUCCESS -> {
				request.setAttribute("success", "Updated Passenger Details Successfully");
			}
			case Status.FAILURE -> {
				request.setAttribute("failure", "Failed to Update Passenger Details");
			}
			case Status.EMAIL_ID_ALREADY_USED -> {
				request.setAttribute("failure", "Provided Email ID is Already Used by Someone");
			}
			case Status.CONTACT_NO_ALREADY_USED -> {
				request.setAttribute("failure", "Provided Contact No is Already Used by Someone");
			}
			case Status.EMPTY_CHANGES -> {
				request.setAttribute("failure", "Kindly Provide Any Changes and Update");
			}
			default -> {
				request.setAttribute("failure", "Internal Error");
			}
			}

			request.getRequestDispatcher("ProfileUpdate.jsp").forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}

	}

}
