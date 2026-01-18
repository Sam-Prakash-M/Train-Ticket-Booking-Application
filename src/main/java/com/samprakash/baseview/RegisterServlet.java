package com.samprakash.baseview;

import java.io.IOException;

import com.samprakash.basemodel.Status;
import com.samprakash.basemodel.Users;
import com.samprakash.baseviewmodel.Hashing;
import com.samprakash.repository.DataBaseConnector;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		String fullName = request.getParameter("fullname");
		String email = request.getParameter("email");
		String userName = request.getParameter("username");
		String rawPassWord = request.getParameter("password");
		String contactNo = request.getParameter("contactno");

		String hashedPassword = Hashing.getHashedPassword(rawPassWord);

		Users newUser = new Users(fullName, email, contactNo, userName, hashedPassword);

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();

		Status userAddedStatus = dataBaseConnector.addUser(newUser);

		RequestDispatcher requestDispatcher = request.getRequestDispatcher("register.jsp");

		try {
			switch (userAddedStatus) {

			case Status.SUCCESS -> {
				response.sendRedirect("login.jsp?registered=true");
			}
			case Status.FAILURE -> {
				request.setAttribute("message", "Failed To Add User Details");
			}
			case Status.EMAIL_ID_ALREADY_USED -> {
				request.setAttribute("message", "Provided Email Id is Already used by Someone");
			}
			case Status.CONTACT_NO_ALREADY_USED -> {
				request.setAttribute("message", "Provided Contact No is Already used by Someone");
			}
			case Status.ALREADY_EXIST -> {
				request.setAttribute("message", "User Already Exist With Username " + userName);
			}
			default -> {
				request.setAttribute("message", "Internal Server Error");
			}
			}

			requestDispatcher.forward(request, response);
		}

		catch (ServletException | IOException e) {

			e.printStackTrace();
		}

	}
}
