package uk.ac.ed.inf.aqmaps;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClosestFirst extends Algorithm {
	private List<Sensor> availableSensors;
	private SensorComparator c;

	public ClosestFirst(Drone d, MapData map) {
		super(d, map);
		availableSensors = new LinkedList<Sensor>(Arrays.asList(this.sensors));
		this.c = new SensorComparator(d);
	}

	public Sensor chooseSensor1() {
		Collections.sort(availableSensors, c);
//		Collections.reverse(availableSensors);
		var topSensor = availableSensors.get(0);
		if (Point2D.dist(new Point2D(topSensor.getPosition()), new Point2D(drone.getPosition())) > 0.0001)
			return availableSensors.remove(0);
		else {
			return availableSensors.remove(1);
		}
	}

	public Sensor chooseSensor() {
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

}
