package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Point;

public class Drone {
	private Point position;
	private final Point starting_position;
	private MapData map;
	List<Point> poslog;

	public Drone(ArgumentParser arguments, MapData map) {
		this.starting_position = arguments.getStartingPos();
		this.position = starting_position;
		this.map = map;

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

	// TODO COMPLETE HELPER METHODS
	public ArrayList<Integer> findPath(Point a, Point b) {
		var pathAngles = new ArrayList<Integer>();

		var now = new Point2D(a);
		var target = new Point2D(b);

		while (straightPath(b)) {
			var angle = Point2D.findAngle(now, target);
			now.add(0.0003, angle);
			pathAngles.add(Integer.valueOf(angle));
			if (Point2D.dist(now, target) <= 0.0002)
				return pathAngles;
		}
		// If this point is reached it means there is no direct path. -->
		pathAngles.addAll(rayPath(a, b));
		return pathAngles;
	}

	private ArrayList<Integer> rayPath(Point a, Point b) {
		var start = new Point2D(a);
		var target = new Point2D(b);
		var ray = new Line2D(start, target);

		var obstacles = map.getForbidden_areas();

		// Find obstacles in front of the target
		var collisionObstacles = new ArrayList<Obstacle>();
		obstacleLoop: for (var obstacle : obstacles)
			for (var wall : obstacle.walls)
				if (Line2D.intersect(ray, wall)) {
					collisionObstacles.add(obstacle);
					continue obstacleLoop;
				}
		var graph = createGraph(collisionObstacles);
		var path = astar(graph, a, b);
		var points = new ArrayList<Integer>();
		var previous=a;
		for (var point : path) {
			points.addAll(findPath(previous, point))));
			previous=point;
		}

		return points;
	}

	private boolean straightPath(Point target) {
		var drone2D = new Point2D(this.position);
		var target2D = new Point2D(target);

		var targetLine = new Line2D(drone2D, target2D);
		var obstacles = map.getForbidden_areas();
		for (var obstacle : obstacles) {
			for (var wall : obstacle.walls)
				if (Line2D.intersect(targetLine, wall))
					return false;
		}
		return true;
	}

	public void move(int angle) {
		var pos2D = new Point2D(this.getPosition());
		pos2D.add(0.0003, angle);
		this.position = Point.fromLngLat(pos2D.x, pos2D.y);
		poslog.add(position);
//		System.out.println(pos2D);
	}

	public void readSensor(Sensor nextSensor) {
		// TODO Auto-generated method stub

	}

}
