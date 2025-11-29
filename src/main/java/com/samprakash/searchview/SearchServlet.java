package com.samprakash.searchview;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.samprakash.baseviewmodel.TrainDataFetcher;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		String fromStation = request.getParameter("fromStation");

		String toStation = request.getParameter("toStation");

		String travelDateAsString  = request.getParameter("travelDate");
	
		LocalDate travelDate = LocalDate.parse(travelDateAsString);
		
		String travelDateFormatted = travelDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		

		String dayName = travelDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		
		TrainDataFetcher trainDataFetcher = new TrainDataFetcher(fromStation,toStation,dayName);

	
		JSONArray matchedTrainList = trainDataFetcher.getMatchedTrain();

		for (int i = 0; i < matchedTrainList.length(); i++) {

			System.out.println(matchedTrainList.getJSONObject(i).toString());
		}

		
		JSONObject trainSeatAvailability = TrainDataFetcher.getSeatAvailabilityForTrain(matchedTrainList,travelDateFormatted);

		Map<String, String> trainData = TrainDataFetcher.getTrainNameByID(matchedTrainList);
		
		request.setAttribute("SourceStation", fromStation);
		request.setAttribute("DestinationStation", toStation);
		request.setAttribute("MatchedTrainList", matchedTrainList);
		request.setAttribute("TrainData", trainData);
		request.setAttribute("TrainSeatAvailability", trainSeatAvailability);

		/*
		 * System.out.println("Train Seat Availability\n"+trainSeatAvailability.toString
		 * ());
		 * 
		 * System.out.println("Train Data "); System.out.println(trainData);
		 */

		RequestDispatcher requestDispatcher = request.getRequestDispatcher("booking.jsp");

		try {
			requestDispatcher.forward(request, response);
		} catch (IOException | ServletException e) {

			e.printStackTrace();
		}

	}
}
