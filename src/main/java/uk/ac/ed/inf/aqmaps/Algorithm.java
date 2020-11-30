package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.mapbox.geojson.Point;

public abstract class Algorithm {
	Drone drone;
	List<Point> path;
	MapData map;
	Sensor[] sensors;
	int steps;

	public Algorithm(Drone d, MapData map) {
		this.drone = d;
		this.map = map;
		this.sensors = map.getSensors();
		this.steps = 0;

		path = new ArrayList<>();
	}

	int angleAdjuster(int angle, int lastAngle, Point2D position) {
//		System.out.println("Angle Adjuster");
		if (validMove(angle, position)) {
//			System.out.println("False Positive");
			return angleRemapper(angle);
		}
		var validPos = validMove(angle + 10, position);
		var validNeg = validMove(angle - 10, position);
		var validLast = validMove(lastAngle, position);

		if (validLast)
			return angleRemapper(lastAngle);
		if (validNeg)
			return angleRemapper(angle - 10);
		if (validPos)
			return angleRemapper(angle + 10);
		if (validMove(angle + 180, position))
			return angleRemapper(angle + 180);
		while (!validMove(angle, position)) {
			angle += 10;
			if (angle > 540)
				return angleRemapper(angle);
		}
		System.out.println("Out of control");
		return angleRemapper(angle);
	}

	int angleRemapper(int angle) {
		while (!(angle >= 0 && angle <= 350)) {
			if (angle < 0)
				angle += 360;
			else if (angle > 350)
				angle -= 360;
		}
		return angle;
	}

	public abstract Sensor chooseSensor();

	public Graph<Point2D, Line2D> createGraph(List<Obstacle> obstacles, Point2D a, Point2D b) {
		var graph = new SimpleWeightedGraph<Point2D, Line2D>(Line2D.class);

		var vertices = new HashSet<Point2D>();
		// Add all corners
		for (var obstacle : obstacles)
			for (var corner : obstacle.points)
				vertices.add(corner);

		// Add origin and target
		vertices.add(a);
		vertices.add(b);

		// Add vertices to the graph
		for (var vertex : vertices) {
			graph.addVertex(vertex);
		}

		// AROUND OBSTACLE SHAPE EDGES
		var shapeEdges = new ArrayList<Line2D>();
		for (var obstacle : obstacles) {
			var corners = obstacle.points;
			for (int i = 0; i < corners.size() - 1; i++) {
				shapeEdges.add(new Line2D(corners.get(i), corners.get(i + 1)));
			}
		}

		var otherEdges = new ArrayList<Line2D>();
		var verticesArr = vertices.toArray();

		for (int i = 0; i < verticesArr.length - 1; i++) {
			next: for (int j = i + 1; j < verticesArr.length; j++) {
				var testLine = new Line2D((Point2D) verticesArr[i], (Point2D) verticesArr[j]);
				for (var obstacle : obstacles)
					if (testLine.inObstacle(obstacle))
						continue next;
				for (var edge : shapeEdges) {
					if (testLine == edge)
						continue;
					if (Line2D.intersect(testLine, edge) && !Line2D.touching(testLine, edge))
						continue next;
				}
				otherEdges.add(testLine);
			}
		}

		// Add obstacle edges
		for (var edge : shapeEdges)
			graph.addEdge(edge.a, edge.b, edge);
		// Add connection edges
		for (var edge : otherEdges)
			graph.addEdge(edge.a, edge.b, edge);
		// Set weights to lengths
		for (var edge : graph.edgeSet())
			graph.setEdgeWeight(edge, edge.getLength());

		return graph;
	}

	public ArrayList<Integer> findPath(Point a, Point b) {
		var pathAngles = new ArrayList<Integer>();
		var lastAngle = 0;
		var now = new Point2D(a);
		var target = new Point2D(b);
		if (straightPath(a, b)) {
			while (Point2D.dist(now, target) > 0.0002) {
				var angle = Point2D.findAngle(now, target);
				if (!validMove(angle, now))
					System.out.println("Tough cookies");
				angle = angleAdjuster(angle, lastAngle, now);
				lastAngle = angle;
				now.add(0.0003, angle);
				pathAngles.add(Integer.valueOf(angle));
			}
			return pathAngles;
		}
		var goals = rayPath(a, b);
		for (var point : goals) {
			var pointAngles = new ArrayList<Integer>();

			while (Point2D.dist(now, point) > 0.0002) {
				var angle = Point2D.findAngle(now, point);
				if (!validMove(angle, now))

					System.out.println("Tough cookies Rays");
				angle = angleAdjuster(angle, lastAngle, now);
				lastAngle = angle;
				now.add(0.0003, angle);
				pointAngles.add(Integer.valueOf(angle));
			}
			pathAngles.addAll(pointAngles);
		}
		return pathAngles;

	}

	public List<Point2D> rayPath(Point a, Point b) {
//		System.out.println(a.toString() + b.toString());
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
//		System.out.println(collisionObstacles.size());
		var graph = createGraph(collisionObstacles, start, target);
		var path = shortestPath(graph, start, target);
		return path;
	}

	public void run() {
		int sensorsLeft = 33;
		var nextSensor = chooseSensor();
		var pos = new Point2D(drone.getPosition());
		System.out.println(new Point2D(nextSensor.getPosition()));
		ArrayList<Integer> path = findPath(drone.getStarting_position(), nextSensor.getPosition());
		while (sensorsLeft > 0 && steps < 150) {
			pos = new Point2D(drone.getPosition());
			if (path.size() == 0) {
				var angle = Point2D.findAngle(new Point2D(drone.getPosition()), new Point2D(nextSensor.getPosition()))
						- 10;
				if (!validMove(angle, pos))
					if (validMove(angle + 20, pos))
						angle += 20;
				if (!validMove(angle, pos))
					System.out.println("KONTAA!");
				drone.move(angle);

			} else {
				var angle = path.get(0);
				if (!validMove(angle, pos)) {
					System.out.println("EXECUTING INVALID MOVE");
					path = findPath(drone.getPosition(), nextSensor.getPosition());
					continue;
				}
				drone.move(angle);
				path.remove(0);
			}
			steps++;
			// READ SENSOR IF CLOSE
			if (drone.distanceToSensor(nextSensor) < 0.0002) {
				drone.readSensor(nextSensor);
				sensorsLeft--;
				if (sensorsLeft == 0)
					break;
				nextSensor = chooseSensor();
				path = findPath(drone.getPosition(), nextSensor.getPosition());
			}
		}
		// GO BACK
		path = findPath(drone.getPosition(), drone.getStarting_position());
		while (steps < 150 && path.size() > 0) {
			drone.move(path.get(0));
			path.remove(0);
			steps++;
		}
		if (steps == 150)
			drone.getLogger().addNonRead(sensors);

	}

	public List<Point2D> shortestPath(Graph<Point2D, Line2D> graph, Point2D source, Point2D target) {
		var astar = new AStarShortestPath<Point2D, Line2D>(graph, new AStarAdmissibleHeuristic<Point2D>() {
			@Override
			public double getCostEstimate(Point2D sourceVertex, Point2D targetVertex) {
				return Point2D.dist(sourceVertex, targetVertex);
			}
		});

		var path = astar.getPath(source, target);
		if (path == null) {
			var pathn = new ArrayList<Point2D>();
			pathn.add(target);
			return pathn;
		}
		var pointList = path.getVertexList();
//		pointList.remove(pointList.size() - 1);
//		pointList.remove(0);
		return pointList;
	}

	private boolean straightPath(Point source, Point target) {
		var drone2D = new Point2D(source);
		var target2D = new Point2D(target);

		var targetLine = new Line2D(drone2D, target2D);
		var obstacles = map.getForbidden_areas();
		for (var obstacle : obstacles) {
			for (var wall : obstacle.walls)
				if (Line2D.intersect(targetLine, wall) || Line2D.touching(targetLine, wall))
					return false;
		}
		return true;
	}

//	private boolean validMove2(int angle) {
//		var pos2D = new Point2D(drone.getPosition());
//		pos2D.add(0.0003, angle);
//		for (var obstacle : map.getForbidden_areas())
//			if (pos2D.inObstacle(obstacle))
//				return false;
////		var walls = map.getWalls();
//		if (!pos2D.inWalls())
//			return false;
//
//		var pos2Dbef = new Point2D(drone.getPosition());
//		var testline = new Line2D(pos2D, pos2Dbef);
//		for (var obstacle : map.getForbidden_areas())
//			for (var wall : obstacle.walls)
//				if (Line2D.intersect(wall, testline) || Line2D.touching(wall, testline)
//						|| testline.a.inObstacle(obstacle) || testline.b.inObstacle(obstacle))
//					return false;
//
//		return true;
//	}

	private boolean validMove(int angle, Point2D position) {
		var oldPos = position.clone();
		var nextPos = position.clone();
		nextPos.add(0.0003, angle);
		var testLine = new Line2D(oldPos, nextPos);

//		System.out.println("Walls Check");
		if (!nextPos.inWalls()) {
			System.out.println("WALL FAIL");
			return false;
		}
		var obstacles = map.getForbidden_areas();

		for (var obstacle : obstacles) {
			if (nextPos.inObstacle(obstacle)) {
//				System.out.println("In obstacle fail");
				return false;
			}

			for (var wall : obstacle.walls) {
				if (Line2D.intersect(testLine, wall)) {
					System.out.println("Intersect Obstacle walls");
					return false;
				}
			}
		}
//		System.out.println("Passed");
		return true;
	}
}
