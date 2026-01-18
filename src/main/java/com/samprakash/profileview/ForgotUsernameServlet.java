package com.samprakash.profileview;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.samprakash.profileviewmodel.MailService;
import com.samprakash.profileviewmodel.UserViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ForgotUsernameServlet")
public class ForgotUsernameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Setup JSON Response
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		JsonObject jsonResponse = new JsonObject();

		String action = request.getParameter("action");
		HttpSession session = request.getSession();

		try {
			if ("sendOtp".equals(action)) {

				String email = request.getParameter("email");

				// 1. Check if Email Exists in DB (Optional but recommended)
				// boolean exists = TrainViewModel.isEmailRegistered(email);
				// if (!exists) throw new Exception("Email not registered.");

				// 2. Generate 6-Digit OTP
				String otp = String.format("%06d", new Random().nextInt(999999));

				// 3. Store in Session (to verify later)
				session.setAttribute("RESET_OTP", otp);
				session.setAttribute("RESET_EMAIL", email);
				session.setMaxInactiveInterval(300); // OTP valid for 5 mins

				// 4. Send Email via Gmail
				MailService.sendOtpEmail(email, otp);

				jsonResponse.addProperty("status", "success");
				jsonResponse.addProperty("message", "OTP Sent Successfully");

			} else if ("verifyOtp".equals(action)) {

				String userOtp = request.getParameter("otp");
				String userEmail = request.getParameter("email");

				// 1. Retrieve OTP from Session
				String sessionOtp = (String) session.getAttribute("RESET_OTP");
				String sessionEmail = (String) session.getAttribute("RESET_EMAIL");

				if (sessionOtp != null && sessionOtp.equals(userOtp) && sessionEmail.equals(userEmail)) {

					// 2. OTP Matched! Fetch Username from DB
					// Replace this with your actual DB call: TrainViewModel.getUsername(userEmail);
					String username = UserViewModel.getUserNameByEmailId(userEmail);

					// Fallback if DB call is not ready yet
					if (username == null) {
						jsonResponse.addProperty("status", "error");
						jsonResponse.addProperty("message", "User Does Not Exist With the Email Id");
					}
					else {
						jsonResponse.addProperty("status", "success");
						jsonResponse.addProperty("username", username);

					}
						
					// Clear session OTP
					session.removeAttribute("RESET_OTP");

				} else {
					jsonResponse.addProperty("status", "error");
					jsonResponse.addProperty("message", "Invalid or Expired OTP.");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			jsonResponse.addProperty("status", "error");
			jsonResponse.addProperty("message", "Error: " + e.getMessage());
		}

		out.print(gson.toJson(jsonResponse));
		out.flush();
	}
}
