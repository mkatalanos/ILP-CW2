package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

/**
 * This class is used to represent a point in space. It also provides multiple
 * mathematical utilities that make it useful. Easier way of working with
 * points.
 * 
 * @author marios
 *
 */
public class Point2D {
	public double x;
	public double y;

	/**
	 * A constructor to the class that makes it from 2 points.
	 * 
	 * @param x
	 * @param y
	 */
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Converts a mapbox point to Point2D to be able to be used in calculations.
	 * 
	 * @param p mapbox sdk' point.
	 */
	public Point2D(Point p) {
		this.y = p.latitude();
		this.x = p.longitude();
	}

	/**
	 * Converts a Point2d back to a mapbox sdk point.
	 * 
	 * @return A new mapbox sdk point.
	 */
	public Point toPoint() {
		return Point.fromLngLat(x, y);
	}

	/**
	 * Updates the current object to add the new point from polar coordinates.
	 * 
	 * @param r     Radius of the point.
	 * @param theta Angle of the point.
	 */
	public void add(double r, double theta) {
		this.x += r * Math.cos(Math.toRadians(theta));
		this.y += r * Math.sin(Math.toRadians(theta));
	}

	/**
	 * Finds the angle formed between two points.
	 * 
	 * @param p1
	 * @param p2
	 * @return The angle formed rounded to the closest ten.
	 */
	public static int findAngle(Point2D p1, Point2D p2) {
		double angle = (Math.toDegrees(Math.atan2(p2.y - p1.y, p2.x - p1.x)) + 360) % 360;
		angle = Math.round(angle / 10f) * 10;
		return (int) angle;
	}

	/**
	 * Finds the euclidean distance between the two points.
	 * 
	 * @param p1
	 * @param p2
	 * @return Euclidean distance between the two points.
	 */
	public static double dist(Point2D p1, Point2D p2) {
		return Math.sqrt(Math.pow(p2.y - p1.y, 2) + Math.pow(p2.x - p1.x, 2));
	}

	/**
	 * Checks if the current point is inside the given obstacle. It does this by
	 * casting a ray to infinity and measures how many times it crosses an edge.
	 * 
	 * @param obstacle
	 * @return True if the number it crosses an obstacle is odd and false if even.
	 */
	public boolean inObstacle(Obstacle obstacle) {
		int counter = 0;
		var ray = new Line2D(this, new Point2D(Double.MAX_VALUE, Double.MAX_VALUE));
		for (var wall : obstacle.walls) {
			if (Line2D.intersect(ray, wall))
				counter++;
		}
		for (var corner : obstacle.points)
			if (corner.x == this.x && corner.y == this.y)
				return true;

		return (counter % 2 != 0);
	}

	/**
	 * Checks whether the current point is inside the bounding walls.
	 * 
	 * @return True if strictly inside the walls. False if not.
	 */
	public boolean inWalls() {
//		 Corners
//		-3.192473, 55.946233
//		-3.184319, 55.942617
		return (this.x > -3.192473 && this.x < -3.184319 && this.y < 55.946233 && this.y > 55.942617);

	}

	/**
	 * Creates a copy of the Point2D object.
	 * 
	 * @return A new Point2D at the same location.
	 */
	@Override
	public Point2D clone() {
		Point2D out = new Point2D(this.x, this.y);
		return out;
	}

	/**
	 * String representation of the object.
	 * 
	 * @return (x,y)
	 */
	@Override
	public String toString() {
		return String.format("(%f,%f)", x, y);
	}

}
