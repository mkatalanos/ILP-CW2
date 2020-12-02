package uk.ac.ed.inf.aqmaps;

/**
 * This is a class that represents a line segment of 2 points. It is used
 * throughout the program and contains multiple mathematic operations.
 * 
 * @author marios
 *
 */
public class Line2D {
	public final Point2D a;
	public final Point2D b;

	public final double length;

	/**
	 * Constructor of the class. Takes as arguments the two points that form it.
	 * 
	 * @param a
	 * @param b
	 */
	public Line2D(Point2D a, Point2D b) {
		this.a = a;
		this.b = b;
		this.length = Point2D.dist(a, b);
	}

	/**
	 * This method checks whether the 2 line segments intersect.
	 * Source:https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
	 * 
	 * @param lineA
	 * @param lineB
	 * @return True if the two lines intersect. False if they don't
	 */
	public static boolean intersect(Line2D lineA, Line2D lineB) {
		var x1 = lineA.a.x;
		var y1 = lineA.a.y;
		var x2 = lineA.b.x;
		var y2 = lineA.b.y;

		var x3 = lineB.a.x;
		var y3 = lineB.a.y;
		var x4 = lineB.b.x;
		var y4 = lineB.b.y;

		var denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		if (denominator == 0) {
			return false;
		}

		var t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator;
		var u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denominator;
		if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method checks if the edges of the two lines intersect.
	 * 
	 * @param lineA
	 * @param lineB
	 * @return True if they do. False if they do not.
	 */
	public static boolean touching(Line2D lineA, Line2D lineB) {
		var a1 = lineA.a;
		var b1 = lineA.b;

		var a2 = lineB.a;
		var b2 = lineB.b;

		return (a1 == a2 || a1 == b2 || b1 == a2 || b1 == b2);
	}

	/**
	 * Checks if a line is inside an obstacle.
	 * 
	 * @param obstacle
	 * @return True if the midpoint of the line segment is inside the object.
	 */
	public boolean inObstacle(Obstacle obstacle) {
		var mx = (this.a.x + this.b.x) / 2;
		var my = (this.a.y + this.b.y) / 2;
		var midpoint = new Point2D(mx, my);
		return midpoint.inObstacle(obstacle);
	}

	/**
	 * Public getter for the length of the line segment.
	 * 
	 * @return length
	 */
	public double getLength() {
		return length;
	}

	/**
	 * This method gets the string representation of the object.
	 */
	@Override
	public String toString() {
		return String.format("Point a: %s, Point b: %s", a, b);
	}

}
