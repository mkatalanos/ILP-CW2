package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.FeatureCollection;

public class MapMaker {

	private Connector connector;
	private ArgumentParser arguments;
	private FeatureCollection forbidden_areas;
	private Sensor[] sensors;

	public MapMaker(Connector connector, ArgumentParser arguments) {
		this.connector = connector;
		this.arguments = arguments;
	}

	private void downloadBuildings() {
		try {
			this.forbidden_areas = connector.forbiddenAreas();
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
