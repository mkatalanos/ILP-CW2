package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

public class W3W {
//	private String country;
//	private Square square;
//	private String nearestPlace;
//	private String words;
	private Coordinate coordinates;
//	private String language;
//	private String map;

	public static class Coordinate {
		double lng;
		double lat;

		@Override
		public String toString() {
			return "(" + lng + "," + lat + ")";
		}

		public Point toPoint() {
			return Point.fromLngLat(this.lng, this.lat);
		}
	}

//	public static class Square {
//		Coordinate southwest;
//		Coordinate northeast;
//	}

	public Coordinate getCoordinate() {
		return this.coordinates;
	}

	@Override
	public String toString() {
		return String.format("Coords: %s", coordinates);
	}
}