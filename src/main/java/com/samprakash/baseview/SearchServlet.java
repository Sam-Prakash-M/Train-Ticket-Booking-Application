package com.samprakash.baseview;

import java.io.IOException;
import java.time.LocalDate;

import org.json.JSONArray;

import com.samprakash.repository.DataBaseConnector;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		String fromStation = request.getParameter("fromStation");

		String toStation = request.getParameter("toStation");

		LocalDate travelDate = LocalDate.parse(request.getParameter("travelDate"));

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();

		JSONArray matchedTrainList = dataBaseConnector.getMatchedTrain(fromStation, toStation, travelDate.toString());

		for (int i = 0; i < matchedTrainList.length(); i++) {

			System.out.println(matchedTrainList.getJSONObject(i).toString());
		}
		
		request.setAttribute("MatchedTrainList", matchedTrainList);
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("booking.jsp"); 
		
		try {
			requestDispatcher.forward(request, response);
		}
		 catch (IOException | ServletException e) {
			
			e.printStackTrace();
		}

	}
}
