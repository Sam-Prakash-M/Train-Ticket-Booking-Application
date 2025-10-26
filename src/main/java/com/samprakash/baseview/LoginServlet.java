package com.samprakash.baseview;
import java.io.IOException;
import java.io.PrintWriter;

import com.samprakash.repository.DataBaseConnector;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	
	
	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) {
		
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		
		try {
			
			DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
			
			if(dataBaseConnector.isUserCredentialIsCorrect(userName,password)) {
				
				PrintWriter printWriter = response.getWriter();
				printWriter.println("User logged In SuccessFully");
			}
			else {
				
				request.setAttribute("message", "UserName or Password is Invalid");
				
				RequestDispatcher requestDispatcher = request.getRequestDispatcher("login.jsp?error=mismatch");
				try {
					requestDispatcher.forward(request, response);
				} catch (ServletException e) {
					
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
}
