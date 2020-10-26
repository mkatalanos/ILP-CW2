package uk.ac.ed.inf.aqmaps;

import java.util.List;

import com.mapbox.geojson.Point;

public interface Algorithm {

	public void run();

	public List<Point> shortestPath(Point a, Point b);

	public Sensor chooseSensor(Drone d);
}
