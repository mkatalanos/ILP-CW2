package uk.ac.ed.inf.aqmaps;

import java.util.List;

/**
 * This is the class used by the algorithm which holds the data for the sensors
 * and the forbidden areas.
 * 
 * @author marios
 *
 */
public class MapData {

	private final List<Obstacle> forbidden_areas;
	private final Sensor[] sensors;

	/**
	 * Public constructor of the class. Is called by map maker.
	 * 
	 * @param forbidden_areas List of Obstacle that contains the forbidden areas.
	 * @param sensors         List of sensors.
	 */
	public MapData(List<Obstacle> forbidden_areas, Sensor[] sensors) {
		this.forbidden_areas = forbidden_areas;
		this.sensors = sensors;

	}

	/**
	 * Getter for the forbidden areas.
	 * 
	 * @return List<Obstacle>
	 */
	public List<Obstacle> getForbidden_areas() {
		return forbidden_areas;
	}

	/**
	 * Getter for the sensor list.
	 * 
	 * @return Sensor[]
	 */
	public Sensor[] getSensors() {
		return sensors;
	}

}
