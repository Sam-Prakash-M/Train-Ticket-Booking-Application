package com.samprakash.baseview;

import java.io.IOException;

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
	public void doPost(HttpServletRequest request,HttpServletResponse response) {
		
		
		
		
		
		String fullName = request.getParameter("fullname");
		String email = request.getParameter("email");
		String userName = request.getParameter("username");
		String rawPassWord = request.getParameter("password");
		
		String hashedPassword = Hashing.getHashedPassword(rawPassWord);
		
		
		Users newUser = new Users(fullName,email,userName,hashedPassword);
		
		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		
		boolean isUserAdded = dataBaseConnector.addUser(newUser);
		
		if(!isUserAdded) {
			
			request.setAttribute("message","User Already Exist With Username "+userName);
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("register.jsp?error=exists");
			
			try {
				requestDispatcher.forward(request, response);
			} catch (ServletException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			return;
			
		}
		
		
        try {
			response.sendRedirect("login.jsp?registered=true");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
		
	}
}
