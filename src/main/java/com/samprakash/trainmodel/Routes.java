package com.samprakash.trainmodel;

public record Routes(String stationName, String arrivalTime, String departureTime, int distanceFromStart)
		implements Comparable<Routes> {

	@Override
	public int compareTo(Routes that) {

		return Integer.compare(this.distanceFromStart, that.distanceFromStart);
	}

}
