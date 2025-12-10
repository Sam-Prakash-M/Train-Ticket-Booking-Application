package com.samprakash.searchviewmodel;

import org.json.JSONArray;
import com.samprakash.baseviewmodel.TrainDataFetcher;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/trains")
@Produces(MediaType.APPLICATION_JSON)
public class SearchRestApi {

	@GET
	@Path("/matchedtrainlist")
	public String getMatchedTrainList(@QueryParam("sourceStation") String sourceStation,
			@QueryParam("destinationStation") String destinationStation, @QueryParam("travelDate") String travelDate) {

		TrainDataFetcher trainDataFetcher = new TrainDataFetcher(sourceStation, destinationStation, travelDate);
		return trainDataFetcher.getMatchedTrain().toString();
	}

	@GET
	@Path("/seatAvailability")
	public String getSeatAvailabilityForTrain(@QueryParam("sourceStation") String sourceStation,
			@QueryParam("destinationStation") String destinationStation, @QueryParam("travelDate") String travelDate) {

		JSONArray matchedTrainList = new JSONArray(
				this.getMatchedTrainList(sourceStation, destinationStation, travelDate));

		return TrainDataFetcher.getSeatAvailabilityForTrain(matchedTrainList,travelDate).toString();
	}

	@GET
	@Path("/test")
	public String test() {

		return "Testing";
	}
}
