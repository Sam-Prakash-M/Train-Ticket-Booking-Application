package com.samprakash.trainmodel;

import java.util.List;
import java.util.Set;

public record TrainData(String trainId, String trainName, FareAmount fareAmount,Set<Routes> routes, List<String> availableDays) {

}
