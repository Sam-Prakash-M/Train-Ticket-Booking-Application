package com.samprakash.baseview;

import java.io.IOException;

import com.samprakash.repository.DataBaseConnector;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		try {
			String userName = request.getParameter("username");
			String password = request.getParameter("password");
			

			if (userName == null || password == null) {
				RequestDispatcher requestDispatcher = request.getRequestDispatcher("login.jsp?error=loggedOut");

				requestDispatcher.forward(request, response);
			}

			DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();

			if (dataBaseConnector.isUserCredentialIsCorrect(userName, password)) {
				HttpSession httpSession = request.getSession(true);
				httpSession.setAttribute("user_name", userName);
				httpSession.setMaxInactiveInterval(600);
				response.sendRedirect("ticketsearch.jsp");

			} else {

				request.setAttribute("message", "UserName or Password is Invalid");

				RequestDispatcher requestDispatcher = request.getRequestDispatcher("login.jsp?error=mismatch");

				requestDispatcher.forward(request, response);

			}
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ServletException e) {

			e.printStackTrace();
		}

	}
}
