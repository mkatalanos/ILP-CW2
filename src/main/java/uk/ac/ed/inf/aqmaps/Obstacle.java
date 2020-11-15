package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class Obstacle {

	List<Point2D> points;
	List<Line2D> walls;

	public Obstacle(List<Point2D> points) throws Exception {
		this.points = points;
		walls = new ArrayList<Line2D>();
		if (points.size() > 1)
			for (int i = 0; i < points.size(); i++) {
				walls.add(new Line2D(points.get(i), points.get((i + 1) % points.size())));
			}
		else
			throw new Exception("Object has no walls");
	}
	
	
	public static Obstacle fromFeature(Feature feature) throws Exception {
		var polygon = (Polygon) feature.geometry();
		var pointList = polygon.coordinates().get(0);
		var points2D = new ArrayList<Point2D>();
		for (Point p : pointList)
			points2D.add(new Point2D(p));

		return new Obstacle(points2D);
	}
}
