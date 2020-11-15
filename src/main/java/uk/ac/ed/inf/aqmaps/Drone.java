package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

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
			while (!validMove(angle)) {
				System.out.println("INVALID");
				angle += 10;
			}
			now.add(0.0003, angle);
			pathAngles.add(Integer.valueOf(angle));
			if (Point2D.dist(now, target) <= 0.0002)
				return pathAngles;
		}
		// If this point is reached it means there is no direct path. -->
		var targets = rayPath(a, b);
//		System.out.println("We are here");

//		for (int i = 0; i < targets.size() - 1; i++) {
//			pathAngles.addAll(findPath(targets.get(i), targets.get(i + 1)));
//			System.out.println(i);
//		}
		return pathAngles;
	}

	public List<Point> rayPath(Point a, Point b) {
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

		var pointList = new ArrayList<Point>();
		for (var point : path)
			pointList.add(point.toPoint());
		return pointList;
	}

	public List<Point2D> shortestPath(Graph<Point2D, Line2D> graph, Point2D source, Point2D target) {
		var astar = new AStarShortestPath<Point2D, Line2D>(graph, new AStarAdmissibleHeuristic<Point2D>() {
			@Override
			public double getCostEstimate(Point2D sourceVertex, Point2D targetVertex) {
				return Point2D.dist(sourceVertex, targetVertex);
			}
		});
		var dijkstra = new DijkstraShortestPath<Point2D, Line2D>(graph);

//		System.out.println(graph.outgoingEdgesOf(source).size());
		var path = dijkstra.getPath(source, target);
//		System.out.println(path);
//		var vertices = path.getVertexList();
		return path.getVertexList();
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
				for (var edge : shapeEdges)
					if (Line2D.intersect(testLine, edge) && !Line2D.touching(testLine, edge))
						continue next;
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

	private boolean validMove(int angle) {
		var pos2D = new Point2D(this.getPosition());
		pos2D.add(0.0003, angle);
		for (var obstacle : map.getForbidden_areas())
			if (pos2D.inObstacle(obstacle))
				return false;
		var walls = map.getWalls();
		if (!pos2D.inObstacle(walls))
			return false;

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
