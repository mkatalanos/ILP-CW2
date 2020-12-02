package uk.ac.ed.inf.aqmaps;

import java.util.Comparator;

/**
 * This class implements the comparator interface to compare sensors.
 * 
 * @author marios
 *
 */
public class SensorComparator implements Comparator<Sensor> {
	private final Drone drone;

	/**
	 * Constructor of the class.
	 * 
	 * @param drone Drone to compare the sensor's distance from.
	 */
	public SensorComparator(Drone drone) {
		this.drone = drone;
	}

	/**
	 * Compares two sensors
	 * 
	 * @param s1 Sensor 1 to be compared
	 * @param s2 Sensor 2 to be compared
	 * @return 1 if distance from 1 to drone is smaller than the distance from 2 to
	 *         the drone and 0 otherwise.
	 */
	@Override
	public int compare(Sensor s1, Sensor s2) {
		var dist1 = Point2D.dist(new Point2D(s1.getPosition()), new Point2D(drone.getPosition()));
		var dist2 = Point2D.dist(new Point2D(s2.getPosition()), new Point2D(drone.getPosition()));
		if (dist1 > dist2)
			return 1;
		else
			return 0;
	}

}
