package com.samprakash.profileview;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.samprakash.basemodel.Status;
import com.samprakash.profileviewmodel.MailService;
import com.samprakash.profileviewmodel.UserViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		JsonObject jsonResponse = new JsonObject();

		String action = request.getParameter("action");
		HttpSession session = request.getSession();

		try {
			// --- NEW ACTION: FETCH EMAIL BY USERNAME ---
			if ("checkUsername".equals(action)) {
				String username = request.getParameter("username");

				// Assuming you have this method in UserViewModel
				String email = UserViewModel.getEmailIdByUserName(username);

				if (email != null && !email.isEmpty()) {
					jsonResponse.addProperty("status", "success");
					jsonResponse.addProperty("email", email);
				} else {
					jsonResponse.addProperty("status", "error");
					jsonResponse.addProperty("message", "Username not found.");
				}
			}
			// --- ACTION: SEND OTP ---
			else if ("sendOtp".equals(action)) {

				String email = request.getParameter("email");

				// Optional: Double check if email matches the username in DB for security
				// But since we fetched it from DB in step 1, we trust the flow.

				String otp = String.format("%06d", new Random().nextInt(999999));

				session.setAttribute("RESET_OTP", otp);
				session.setAttribute("RESET_EMAIL", email);
				session.setMaxInactiveInterval(300);

				MailService.sendOtpEmail(email, otp);

				jsonResponse.addProperty("status", "success");
				jsonResponse.addProperty("message", "OTP Sent Successfully");

			}
			// --- ACTION: VERIFY OTP ---
			else if ("verifyOtp".equals(action)) {

				String userOtp = request.getParameter("otp");
				String userEmail = request.getParameter("email");

				String sessionOtp = (String) session.getAttribute("RESET_OTP");
				String sessionEmail = (String) session.getAttribute("RESET_EMAIL");

				if (sessionOtp != null && sessionOtp.equals(userOtp) && sessionEmail.equals(userEmail)) {

					// Clear OTP to prevent re-use
					session.removeAttribute("RESET_OTP");

					jsonResponse.addProperty("status", "success");
				} else {
					jsonResponse.addProperty("status", "error");
					jsonResponse.addProperty("message", "Invalid or Expired OTP.");
				}
			}
			// --- ACTION: RESET PASSWORD ---
			else if ("resetPassword".equals(action)) {

				String userEmail = request.getParameter("email");
				String newPassWord = request.getParameter("newPassword");

				Status status = UserViewModel.updateUserPassword(userEmail, newPassWord);

				switch (status) {
				case SUCCESS -> {
					jsonResponse.addProperty("status", "success");
					jsonResponse.addProperty("message", "Password Changed Successfully");
				}
				case FAILURE -> {
					jsonResponse.addProperty("status", "error");
					jsonResponse.addProperty("message", "Internal Error: Could not change password");
				}
				case OLD_PASSWORD_REUSED -> {
					jsonResponse.addProperty("status", "error");
					jsonResponse.addProperty("message", "New password cannot be the same as past 3 passwords");
				}
				case CURRENT_PASSWORD_MISMATCHED -> {
					// This case might not apply for Forgot Password flow, but handling just in case
					jsonResponse.addProperty("status", "error");
					jsonResponse.addProperty("message", "Password Mismatch");
				}
				case USER_DOES_NOT_EXIST -> {
					jsonResponse.addProperty("status", "error");
					jsonResponse.addProperty("message", "User Does Not Exist");
				}
				default -> {
					jsonResponse.addProperty("status", "error");
					jsonResponse.addProperty("message", "Internal Server Error");
				}
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