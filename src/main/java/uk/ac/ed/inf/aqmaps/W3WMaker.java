package uk.ac.ed.inf.aqmaps;

public class W3WMaker {
	private String country;
	private Square square;
	private String nearestPlace;
	private String words;
	private String language;
	private String map;

	public static class Coordinate {
		double lng;
		double lat;
	}

	public static class Square {
		Coordinate southwest;
		Coordinate northeast;
	}

}