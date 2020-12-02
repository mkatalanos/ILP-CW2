package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

/**
 * This class is used to represent an obstacle the drone cannot fly over.
 * 
 * @author marios
 *
 */
public class Obstacle {

	public final List<Point2D> points;
	public final List<Line2D> walls;

	/**
	 * Public constructor
	 * 
	 * @param points A list of Point2D points which form the obstacle.
	 * @throws Exception If the point list has a size of less than or equal to 1.
	 */
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

	/**
	 * Creates a new obstacle from a feature. Called by MapMaker.
	 * 
	 * @param feature The feature from which to create the obstacle.
	 * @return The obstacle representation of the feature.
	 * @throws Exception if the point list that form the feature is smaller than 1.
	 */
	public static Obstacle fromFeature(Feature feature) throws Exception {
		var polygon = (Polygon) feature.geometry();
		var pointList = polygon.coordinates().get(0);
		var points2D = new ArrayList<Point2D>();
		for (Point p : pointList)
			points2D.add(new Point2D(p));

		return new Obstacle(points2D);
	}
}
