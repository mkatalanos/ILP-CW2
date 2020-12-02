package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

/**
 * This class holds the representatoin fo a sensor.
 * 
 * @author marios
 *
 */
public class Sensor {

	private String location;
	private Point position;
	private double battery;

	private String reading;

	/**
	 * Private constructor to resist creation by constructor. Sensors get created by
	 * desirialization in map creation.
	 */
	private Sensor() {
	}

	/**
	 * Public getter for the sensor battery.
	 * 
	 * @return double battery.
	 */
	public double getBattery() {
		return battery;
	}

	/**
	 * Public getter for the sensor's location
	 * 
	 * @return W3W location of the sensor.
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * Public getter for the sensor's point position.
	 * 
	 * @return Point position.
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Public getter for the sensor's reading.
	 * 
	 * @return String can be either a number NaN or null.
	 */
	public String getReading() {
		return reading;
	}

	/**
	 * Public setter for the sensor's point position. Should only be called by map
	 * maker.
	 * 
	 * @param position the point position.
	 */
	public void setPosition(Point position) {
		this.position = position;
	}

	/**
	 * String representation of the sensor.
	 */
	@Override
	public String toString() {
		return String.format("location: %s, battery:%.3f, reading:%s", this.location, this.battery, this.reading);
	}

}
