package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to prepare and build a MapData object which will hold
 * everything needed for the drone to move around the map.
 * 
 * @author marios
 *
 */
public class MapMaker {

	private final Connector connector;
	private final ArgumentParser arguments;
	private List<Obstacle> forbidden_areas;
	private Sensor[] sensors;

	/**
	 * Constructor of the class.
	 * 
	 * @param connector It takes a connector object from which the other objects
	 *                  will be downloaded from.
	 * @param arguments Standard input arguments.
	 */
	public MapMaker(Connector connector, ArgumentParser arguments) {
		this.connector = connector;
		this.arguments = arguments;
	}

	/**
	 * Using the connector it downloads and creates obstacle objects for each
	 * forbidden area.
	 */
	private void downloadBuildings() {
		this.forbidden_areas = new ArrayList<>();
		try {
			var forbidden_areas_collection = connector.forbiddenAreas().features();
			for (var feature : forbidden_areas_collection) {
				this.forbidden_areas.add(Obstacle.fromFeature(feature));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Using the connector it downloads and creates the sensor list for the day.
	 */
	private void downloadSensors() {
		var day = arguments.getDate()[0];
		var month = arguments.getDate()[1];
		var year = arguments.getDate()[2];
		try {
			this.sensors = connector.dateData(year, month, day);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Updates every sensor to add its w3w translation.
	 */
	private void updateSensors() {
		try {
			for (Sensor s : sensors) {
				var w3w = connector.getWord(s);
				s.setPosition(w3w.getCoordinate().toPoint());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates a MapData object based on the downloaded objects.
	 * 
	 * @return A new MapData object.
	 */
	public MapData make() {
		downloadSensors();
		updateSensors();
		downloadBuildings();

		return new MapData(forbidden_areas, sensors);
	}
}
