package uk.ac.ed.inf.aqmaps;

public class Line2D {
	final Point2D a;
	final Point2D b;

	public Line2D(Point2D a, Point2D b) {
		this.a = a;
		this.b = b;
	}

	public static boolean intersect(Line2D lineA, Line2D lineB) {
		// Can use t and u to find intersection if needed.
		// https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
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

	@Override
	public String toString() {
		return String.format("Point a: %s, Point b: %s", a, b);
	}
}
