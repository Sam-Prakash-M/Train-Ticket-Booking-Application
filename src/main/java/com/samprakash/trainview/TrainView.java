package com.samprakash.trainview;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson; // Requires GSON dependency
import com.samprakash.trainmodel.TrainData;
import com.samprakash.trainviewmodel.TrainViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/TrainData")
public class TrainView extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Capture Input (Parameter name matches the AJAX call)
        String searchInput = request.getParameter("trainInput"); 
        
        // 2. Detect AJAX Request
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isAjax) {
            // Return JSON Response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();

            if (searchInput == null || searchInput.trim().isEmpty()) {
                out.print("{\"status\":\"error\", \"message\":\"Please enter a Train Name or Number.\"}");
                return;
            }

            // Fetch Data form ViewModel
            TrainData trainData = TrainViewModel.getTrainDetails(searchInput.trim());

            if (trainData != null) {
                // Success: Convert Java Object to JSON
                String json = gson.toJson(trainData);
                out.print("{\"status\":\"success\", \"data\":" + json + "}");
            } else {
                // Error: No Data Found
                out.print("{\"status\":\"error\", \"message\":\"No train found with that Name/Number.\"}");
            }
            out.flush();
        } else {
            // Fallback: Standard Page Load
            request.getRequestDispatcher("TrainData.jsp").forward(request, response);
        }
    }
}