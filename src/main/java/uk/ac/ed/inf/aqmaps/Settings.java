package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

/**
 * This class holds the constant settings used throughout the program
 * 
 * @author Marios Katalanos
 *
 */
public final class Settings {

	/**
	 * Used to restrict instantiation.
	 */
	private Settings() {
	}

	public static final Point[] corners = { Point.fromLngLat(-3.192473, 55.946233), // Forrest Hill
			Point.fromLngLat(-3.184319, 55.942617)// Buccleuch St bus stop
	};
}
