package com.samprakash.baseview;
import java.io.IOException;
import java.io.PrintWriter;

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
			PrintWriter out = response.getWriter();
			
			out.print("UserName is : "+userName+" Password : "+password);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
