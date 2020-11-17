package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.mapbox.geojson.Point;

public class ClosestFirst {

	Drone drone;
	List<Point> path;
	MapData map;
	List<Sensor> sensors;
	int steps;

	public ClosestFirst(Drone d, MapData map) {
		this.drone = d;
		this.map = map;
		this.sensors = new LinkedList<Sensor>(Arrays.asList(map.getSensors()));
		this.steps = 0;

		path = new ArrayList<>();
	}

	public Sensor chooseSensor() {
		var maxDist = Double.MAX_VALUE;
		var closest = sensors.get(0);
		for (Sensor s : sensors) {
			var dist = drone.distanceToSensor(s);
			if (dist < maxDist) {
				closest = s;
				maxDist = dist;
			}
		}
		return closest;
	}

	public void run() {
		int sensorsLeft = 33;
		var nextSensor = chooseSensor();
		ArrayList<Integer> path = findPath(drone.getStarting_position(), nextSensor.getPosition());
		while (sensorsLeft > 0 && steps < 150) {
			drone.move(path.get(0));
			path.remove(0);
			steps++;
			if (drone.distanceToSensor(nextSensor) < 0.0002) {

				drone.readSensor(nextSensor);
				sensors.remove(nextSensor);
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

	}

	public ArrayList<Integer> findPath(Point a, Point b) {
		var pathAngles = new ArrayList<Integer>();

		var now = new Point2D(a);
		var target = new Point2D(b);
		if (straightPath(a, b)) {
			while (Point2D.dist(now, target) > 0.0002) {
				var angle = Point2D.findAngle(now, target);
				while (!validMove(angle)) {
					System.out.println("INVALID");
					angle += 10;
				}
				now.add(0.0003, angle);
				pathAngles.add(Integer.valueOf(angle));
			}
			return pathAngles;
		}
		var goals = rayPath(a, b);
		for (var point : goals) {
			var pointAngles = new ArrayList<Integer>();
			while (Point2D.dist(now, point) > 0.0001) {
				var angle = Point2D.findAngle(now, point);
				while (!validMove(angle)) {
					System.out.println("INVALID");
					angle += 10;
				}
				now.add(0.0003, angle);
				pointAngles.add(Integer.valueOf(angle));
			}
			pathAngles.addAll(pointAngles);
		}
		return pathAngles;
	}

	public List<Point2D> rayPath(Point a, Point b) {
		System.out.println(a.toString() + b.toString());
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

		var graph = createGraph(collisionObstacles, start, target);
		var path = shortestPath(graph, start, target);
		return path;
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
		pointList.remove(0);
		return pointList;
	}

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
				for (var edge : shapeEdges) {
					if (Line2D.intersect(testLine, edge) && !Line2D.touching(testLine, edge))
						continue next;
					for (var obstacle : obstacles)
						if (testLine.inObstacle(obstacle))
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

	private boolean straightPath(Point source, Point target) {
		var drone2D = new Point2D(source);
		var target2D = new Point2D(target);

		var targetLine = new Line2D(drone2D, target2D);
		var obstacles = map.getForbidden_areas();
		for (var obstacle : obstacles) {
			for (var wall : obstacle.walls)
				if (Line2D.intersect(targetLine, wall) && !Line2D.touching(targetLine, wall))
					return false;
		}
		return true;
	}

	private boolean validMove(int angle) {
		var pos2D = new Point2D(drone.getPosition());
		pos2D.add(0.0003, angle);
		for (var obstacle : map.getForbidden_areas())
			if (pos2D.inObstacle(obstacle))
				return false;
		var walls = map.getWalls();
		if (!pos2D.inObstacle(walls))
			return false;

		return true;
	}
}
