package com.samprakash.baseviewmodel;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.samprakash.repository.DataBaseConnector;

public final class TrainDataFetcher {

	
	private String sourceStation,destinationStation,travelDay;
	
	private final static DataBaseConnector DATABASE_CONNECTOR = DataBaseConnector.getInstance();
	
	public TrainDataFetcher(String sourceStation,String destinationStation,String travelDay) {
		
		this.sourceStation = sourceStation;
		this.destinationStation = destinationStation;
		this.travelDay = travelDay;
	}


	public String getSourceStation() {
		return sourceStation;
	}


	public void setSourceStation(String sourceStation) {
		this.sourceStation = sourceStation;
	}


	public String getDestinationStation() {
		return destinationStation;
	}


	public void setDestinationStation(String destinationStation) {
		this.destinationStation = destinationStation;
	}


	public String getTravelDay() {
		return travelDay;
	}


	public void setTravelDay(String travelDay) {
		this.travelDay = travelDay;
	}


	public JSONArray getMatchedTrain() {
		
		return DATABASE_CONNECTOR.getMatchedTrain(sourceStation, destinationStation, travelDay);
	}


	public static JSONObject getSeatAvailabilityForTrain(JSONArray matchedTrainList) {
		return DATABASE_CONNECTOR.getSeatAvailabilityForTrain(matchedTrainList);
	}


	public static Map<String, String> getTrainNameByID(JSONArray matchedTrainList) {
		return DATABASE_CONNECTOR.getTrainNameByID(matchedTrainList);
	}
	
	
}
