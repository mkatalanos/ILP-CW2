package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Point;

public class Drone {
	private Point position;
	private final Point starting_position;
	private final List<Point> poslog;

	public Drone(ArgumentParser arguments) {
		this.starting_position = arguments.getStartingPos();
		this.position = starting_position;

		poslog = new ArrayList<>();
		poslog.add(starting_position);
	}

	public double distanceToSensor(Sensor s) {
		var sPos = s.getPosition();
		double distance = Math.sqrt(Math.pow(position.latitude() - sPos.latitude(), 2)
				+ Math.pow(position.longitude() - sPos.longitude(), 2));
		return distance;
	}

	public Point getPosition() {
		return position;
	}

	public Point getStarting_position() {
		return starting_position;
	}

	public void move(int angle) {
		var pos2D = new Point2D(this.getPosition());
		pos2D.add(0.0003, angle);
		this.position = Point.fromLngLat(pos2D.x, pos2D.y);
		poslog.add(position);
	}

	public void readSensor(Sensor s) {

	}

}
