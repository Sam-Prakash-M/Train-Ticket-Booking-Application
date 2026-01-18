package com.samprakash.profileview;

import java.io.IOException;

import com.samprakash.basemodel.Status;
import com.samprakash.profileviewmodel.PasswordUpdateViewModel;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/PasswordUpdate")
public class PasswordUpdateView extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {
            String userName = (String) request.getSession(false).getAttribute("user_name");
            
            // Basic validation
            if (userName == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String newPassword = request.getParameter("newPassword");
            String currentPassword = request.getParameter("currentPassword");

            Status updateStatus = PasswordUpdateViewModel.updatePassword(userName, currentPassword, newPassword);

            // Forward back to the Profile Page Controller to reload user data & show toast
            RequestDispatcher dispatcher = request.getRequestDispatcher("ProfileUpdate.jsp");
       System.out.println("Update Status : "+updateStatus.name());
            switch (updateStatus) {
                case SUCCESS -> {
                    request.setAttribute("success", "Password updated successfully!");
                }
                case CURRENT_PASSWORD_MISMATCHED -> {
                    request.setAttribute("failure", "The current password you entered is incorrect.");
                }
                case OLD_PASSWORD_REUSED -> {
                    request.setAttribute("failure", "New password cannot be the same as the old password.");
                }
                case FAILURE -> {
                    request.setAttribute("failure", "Failed to update password. Please try again.");
                }
                default -> {
                    request.setAttribute("failure", "An unexpected error occurred.");
                }
            }

            dispatcher.forward(request, response);

        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }
}