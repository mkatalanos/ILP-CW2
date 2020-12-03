package uk.ac.ed.inf.aqmaps;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This method is a greedier approach to choosing the sensor list. It always
 * chooses the closest sensor to the drone which is available.
 * 
 * @author marios
 *
 */
public class ClosestFirst extends Algorithm {
	private final List<Sensor> availableSensors;
	private final SensorComparator c;

	protected boolean lastResort = true; // Used to signify that if the program is stuck this is our only chance to
											// finding a correct path.

	/**
	 * Public constructor for the class. Calls the parent class constructor and
	 * initialises the array needed for choosing the next sensor.
	 * 
	 * @param d   The drone
	 * @param map The map
	 */
	public ClosestFirst(Drone d, MapData map) {
		super(d, map);
		availableSensors = new LinkedList<Sensor>(Arrays.asList(this.sensors));
		this.c = new SensorComparator(d);
	}

	/**
	 * This method chooses the next sensor to be visited. It does this by finding
	 * the closest available sensor
	 * 
	 * @return The next Sensor to be visitted.
	 */
	@Override
	protected Sensor chooseSensor() {
		var minDist = Double.MAX_VALUE;
		var closest = availableSensors.get(0);
		for (Sensor s : availableSensors) {
			var dist = drone.distanceToSensor(s);
			if (dist < minDist) {
				closest = s;
				minDist = dist;
			}
		}
		if (minDist > 0.0001) {
			availableSensors.remove(closest);
			return closest;
		} else {
			Collections.sort(availableSensors, c);
			return availableSensors.remove(1);
		}
	}

	/**
	 * This method is used to add a sensor to the end of the available sensors, so
	 * that it gets chosen last.
	 */
	@Override
	protected void ignoreSensor(Sensor s) {
		availableSensors.add(s);
	}

}
