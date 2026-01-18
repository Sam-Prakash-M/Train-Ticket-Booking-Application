package com.samprakash.trainviewmodel;

import com.samprakash.repository.DataBaseConnector;
import com.samprakash.trainmodel.TrainData;

public class TrainViewModel {

	private static String getTrainId(String trainNameOrId) {

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();

		String trainId = dataBaseConnector.getTrainId(trainNameOrId);

		if (trainId != null) {

			return trainId;
		}
		System.out.println("train Id or TrainName is Not Exist" + trainNameOrId);

		return trainId;
	}

	public static TrainData getTrainDetails(String trainNameOrId) {

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();

		String trainId = getTrainId(trainNameOrId);
		if (trainId != null) {

			return dataBaseConnector.getTrainFullDetails(trainId);
		}

		return null;
	}
}
