package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;

import com.mapbox.geojson.Point;

public class Drone {
	private Point position;
	private final Point starting_position;

	public Drone(ArgumentParser arguments) {
		this.starting_position = arguments.getStartingPos();
		this.position = starting_position;
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

	// TODO COMPLETE HELPER METHODS
	public ArrayList<Point> findPath(Point a,Point b) {
		
		if (straightPath(b)) {
			
		}
		return null;
	}

	private int findAngle(Point p1, Point p2) {
		var y2 = p2.latitude();
		var y1 = p1.latitude();
		var x2 = p2.longitude();
		var x1 = p1.longitude();
		double angle = (Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 360) % 360;
		angle = Math.round(angle / 10f) * 10;
		return (int) angle;
	}

	private boolean straightPath(Point target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void move(Point point) {
		// TODO Auto-generated method stub

	}
	
}
