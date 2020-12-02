package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

/**
 * This class is used to represent a What 3 words location.
 * 
 * @author marios
 *
 */
public class W3W {

	/**
	 * Inner class that holds a coordinate. Is used for deserialization.
	 * 
	 * @author marios
	 *
	 */
	public static class Coordinate {
		private double lng;
		private double lat;

		/**
		 * Creates a Point from the coordinate.
		 * 
		 * @return Point representation of the object.
		 */
		public Point toPoint() {
			return Point.fromLngLat(this.lng, this.lat);
		}

		/**
		 * String representation of the object.
		 */
		@Override
		public String toString() {
			return "(" + lng + "," + lat + ")";
		}
	}

	private Coordinate coordinates;

	/**
	 * Private constructor. Should only be created by deserialization.
	 */
	private W3W() {
	}

	/**
	 * Public getter for coordinates
	 * 
	 * @return the coordinates of the sensor.
	 */
	public Coordinate getCoordinate() {
		return this.coordinates;
	}

	/**
	 * String representation of the object.
	 */
	@Override
	public String toString() {
		return String.format("Coords: %s", coordinates);
	}
}