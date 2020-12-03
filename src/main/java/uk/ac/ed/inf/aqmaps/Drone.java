package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

/**
 * This class holds the drone's representation and variables.
 * 
 * @author marios
 *
 */
public class Drone {
	private Point position; // Current position
	private final Point starting_position; // Starting position
	private final Logger logger; // Used to log every move and readings

	/**
	 * Public constructor for the drone.
	 * 
	 * @param arguments Standard input arguments.
	 */
	public Drone(ArgumentParser arguments) {
		this.starting_position = arguments.getStartingPos();
		this.position = starting_position;
		this.logger = new Logger();
		logger.logPos(getStarting_position());
	}

	/**
	 * this method is used to calculate the drone's position from the sensor.
	 * 
	 * @param s The sensor to be checked against.
	 * @return The euclidean distance to the sensor.
	 */
	public double distanceToSensor(Sensor s) {
		var sPos = s.getPosition();
		double distance = Math.sqrt(Math.pow(position.latitude() - sPos.latitude(), 2)
				+ Math.pow(position.longitude() - sPos.longitude(), 2));
		return distance;
	}

	/**
	 * Public getter for the logger object of the drone.
	 * 
	 * @return The logger that logs this drone's moves.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Public getter for the current position of the drone.
	 * 
	 * @return The current position of the drone.
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Public getter for the drone's starting position. Is the same the starting
	 * position from the arguments.
	 * 
	 * @return Starting position.
	 */
	public Point getStarting_position() {
		return starting_position;
	}

	/**
	 * This method takes an angle and moves the drone at a radius of 0.0003 in that
	 * angle's direction. It also logs the new position and the log in the logger.
	 * 
	 * @param angle The angle of the direction. It should be a multiple of 10
	 *              between 0-350.
	 */
	public void move(int angle) {
		var pos2D = new Point2D(this.getPosition());
		pos2D.add(0.0003, angle);
		this.position = Point.fromLngLat(pos2D.x, pos2D.y);
		logger.logAngle(angle);
		logger.logPos(position);
	}

	/**
	 * Reads the sensor and logs it.
	 * 
	 * @param sensor The sensor to be read.
	 */
	public void readSensor(Sensor sensor) {
		var reading = new SensorReading(sensor);
		logger.logReading(reading);
	}

}
