package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

public class MapMaker {

	private Connector connector;
	private ArgumentParser arguments;
	private List<Obstacle> forbidden_areas;
	private Sensor[] sensors;

	public MapMaker(Connector connector, ArgumentParser arguments) {
		this.connector = connector;
		this.arguments = arguments;
	}

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

	public MapData make() {
		downloadSensors();
		updateSensors();
		downloadBuildings();

		return new MapData(forbidden_areas, sensors);
	}
}
