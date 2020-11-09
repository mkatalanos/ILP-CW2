package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

public class Point2D {
	double x;
	double y;

	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point2D(Point p) {
		this.y = p.latitude();
		this.x = p.longitude();
	}

	public Point toPoint() {
		return Point.fromLngLat(x, y);
	}

	public void add(double r, double theta) {
		this.x += r * Math.cos(Math.toRadians(theta));
		this.y += r * Math.sin(Math.toRadians(theta));
	}

	public static int findAngle(Point2D p1, Point2D p2) {
		double angle = (Math.toDegrees(Math.atan2(p2.y - p1.y, p2.x - p1.x)) + 360) % 360;
		angle = Math.round(angle / 10f) * 10;
		return (int) angle;
	}

	public static double dist(Point2D p1, Point2D p2) {
		return Math.sqrt(Math.pow(p2.y - p1.y, 2) + Math.pow(p2.x - p1.x, 2));
	}

	@Override
	public String toString() {
		return String.format("(%f,%f)", x, y);
	}

}
