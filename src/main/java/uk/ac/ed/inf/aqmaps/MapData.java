package uk.ac.ed.inf.aqmaps;

import java.util.List;

public class MapData {

	private List<Obstacle> forbidden_areas;
	private Sensor[] sensors;

	MapData(List<Obstacle> forbidden_areas, Sensor[] sensors) {
		this.forbidden_areas = forbidden_areas;
		this.sensors = sensors;
	}

	public List<Obstacle> getForbidden_areas() {
		return forbidden_areas;
	}

	public Sensor[] getSensors() {
		return sensors;
	}

}
