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

}
