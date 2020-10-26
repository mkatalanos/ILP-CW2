package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.mapbox.geojson.Point;

public class ClosestFirst {

	Drone drone;
	List<Point> path;
	MapData map;
	List<Sensor> sensors;
	int steps;

	public ClosestFirst(Drone d, MapData map) {
		this.drone = d;
		this.map = map;
		this.sensors = Arrays.asList(map.getSensors());
		this.steps = 0;

		path = new ArrayList<>();
	}

	public Sensor chooseSensor() {
		var maxDist = Double.MAX_VALUE;
		var closest = sensors.get(0);
		for (Sensor s : sensors) {
			var dist = drone.distanceToSensor(s);
			if (dist < maxDist) {
				closest = s;
				maxDist = dist;
			}
		}
		return closest;
	}

	public void run() {
		int sensorsLeft = 33;
		var nextSensor = chooseSensor();
		ArrayList<Point> path = drone.findPath(nextSensor.getPosition());
		while (sensorsLeft > 0 && steps < 150) {
			drone.move(path.get(0));
			path.remove(0);
			steps++;
			if (drone.distanceToSensor(nextSensor) < 0.0002) {
//				drone.readSensor(nextSensor);
				sensors.remove(nextSensor);
				nextSensor = chooseSensor();
				sensorsLeft--;
				path = drone.findPath(nextSensor.getPosition());
			}
		}
		path = drone.findPath(drone.getStarting_position());
		while (steps < 150) {
			drone.move(path.get(0));
			path.remove(0);
			steps++;
		}

	}

}
