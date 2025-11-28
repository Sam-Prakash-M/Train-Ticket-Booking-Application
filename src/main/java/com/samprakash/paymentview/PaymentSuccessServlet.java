package com.samprakash.paymentview;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/PaymentSuccess")
public class PaymentSuccessServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();

		String[] names = (String[]) session.getAttribute("names");
		String[] ages = (String[]) session.getAttribute("ages");
		String[] genders = (String[]) session.getAttribute("genders");
		String[] nationalities = (String[]) session.getAttribute("nationalities");
		String[] berths = (String[]) session.getAttribute("berths");

		String mobile = (String) session.getAttribute("mobile");
		String email = (String) session.getAttribute("email");

		Double totalAmount = (Double) session.getAttribute("total");
           
		
	}

}
