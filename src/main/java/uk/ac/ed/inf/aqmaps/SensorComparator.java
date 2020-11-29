package uk.ac.ed.inf.aqmaps;

import java.util.Comparator;

public class SensorComparator implements Comparator<Sensor> {
	private final Drone drone;

	public SensorComparator(Drone drone) {
		this.drone = drone;
	}

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
