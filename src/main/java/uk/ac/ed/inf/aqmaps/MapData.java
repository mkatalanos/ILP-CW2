package uk.ac.ed.inf.aqmaps;

import java.util.List;

public class MapData {

	private List<Obstacle> forbidden_areas;
	private Sensor[] sensors;
	private Obstacle walls;

	MapData(List<Obstacle> forbidden_areas, Sensor[] sensors, Obstacle walls) {
		this.forbidden_areas = forbidden_areas;
		this.sensors = sensors;
		this.walls = walls;
		System.out.println(walls);
	}

	public List<Obstacle> getForbidden_areas() {
		return forbidden_areas;
	}

	public Obstacle getWalls() {
		System.out.println(this.walls);
		return walls;
	}

	public Sensor[] getSensors() {
		return sensors;
	}

}
