package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.mapbox.geojson.Point;

/**
 * This class is the class that decides the movement of the drone. It contains
 * multiple functions which calculate the drone will move next.
 * 
 * @author marios
 *
 */
public abstract class Algorithm {
	protected final Drone drone;
	protected final MapData map;
	protected final Sensor[] sensors;
	private int steps;
	private int deathCounter = 0;

	protected boolean lastResort;

	/**
	 * This is the constructor of the class. It gets called by its subclasses.
	 * 
	 * @param d   A drone instance to be used.
	 * @param map
	 */
	protected Algorithm(Drone d, MapData map) {
		this.drone = d;
		this.map = map;
		this.sensors = map.getSensors();
		this.steps = 0;

	}

	/**
	 * This method is used to find a new valid angle if the attempted move was
	 * invalid.
	 * 
	 * @param angle     Angle you attempt to execute
	 * @param lastAngle Last angle successfully executed.
	 * @param position  Current position
	 * @return The new adjusted angle
	 */
	private int angleAdjuster(int angle, int lastAngle, Point2D position) {
		if (validMove(angle, position)) {
			return angleRemapper(angle);
		}

		var validPos = validMove(angle + 10, position); // Positive adjustment
		var validNeg = validMove(angle - 10, position); // Negative adjustment
		var validLast = validMove(lastAngle, position); // Last angle

		if (validLast)
			return angleRemapper(lastAngle);
		if (validNeg)
			return angleRemapper(angle - 10);
		if (validPos)
			return angleRemapper(angle + 10);
		if (validMove(angle + 180, position)) // Opposite direction
			return angleRemapper(angle + 180);
		while (!validMove(angle, position)) {
			// Keep goign until you find one or until the exit condition is met.
			angle += 10;
			if (angle > 540)
				return angleRemapper(angle);
		}
		return angleRemapper(angle);
	}

	/**
	 * This method takes an angle and re-maps it to the range 0-350.
	 * 
	 * @param angle
	 * @return The re-mapped angle
	 */
	private int angleRemapper(int angle) {
		while (!(angle >= 0 && angle <= 350)) {
			if (angle < 0)
				angle += 360;
			else if (angle > 350)
				angle -= 360;
		}
		return angle;
	}

	/**
	 * This method is used to choose the next sensor.
	 * 
	 * @return Next sensor to be visited.
	 */
	protected abstract Sensor chooseSensor();

	/**
	 * This method creates a Graph that interconnects the corners of the obstacles
	 * with the 2 points.
	 * 
	 * @param obstacles List of obstacles between a and b
	 * @param a         origin point
	 * @param b         target point
	 * @return A new graph with weights the Euclidean distance between points.
	 */
	private Graph<Point2D, Line2D> createGraph(List<Obstacle> obstacles, Point2D a, Point2D b) {
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

		// Edges that form the obstacles
		var shapeEdges = new ArrayList<Line2D>();
		for (var obstacle : obstacles) {
			var corners = obstacle.points;
			for (int i = 0; i < corners.size() - 1; i++) {
				shapeEdges.add(new Line2D(corners.get(i), corners.get(i + 1)));
			}
		}

		var otherEdges = new ArrayList<Line2D>();
		var verticesArr = vertices.toArray();

		// Create interconnecting edges that don't cross through obstacles.
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

	/**
	 * This method calculates the angles the drone should follow to go from a to b.
	 * Has 2 cases. One where the target is directly visible and one when there are
	 * obstacles in between. It gets the angle it needs to move adjusts it and
	 * executes it until it reaches the target or close enough.
	 * 
	 * @param a Starting position
	 * @param b Target
	 * @return ArrayList of Integers which represent the angles to follow. These
	 *         moves should all be valid and multiples of 10 in the range 0-350
	 *         inclusive.
	 */
	private ArrayList<Integer> findPath(Point a, Point b) {
		var pathAngles = new ArrayList<Integer>();
		var lastAngle = 0;
		var now = new Point2D(a); // Simulates the drone
		var target = new Point2D(b);
		// If there's a straight path without any obstacles
		if (straightPath(a, b)) {
			var safety_counter = 0; // Used to exit in case of an infinite loop or exit earlier
			while (Point2D.dist(now, target) > 0.0002 && safety_counter++ <= 10) {
				var angle = Point2D.findAngle(now, target);

				// Debug message
				if (!validMove(angle, now))
					System.out.println("Invalid Move Straight");

				// Execute move
				angle = angleAdjuster(angle, lastAngle, now); // Adjusts the angle
				lastAngle = angle;
				now.add(0.0003, angle);
				pathAngles.add(Integer.valueOf(angle));

			}

			return pathAngles;
		}
		// If there are obstacles inside calculate the list of goals to be visited.
		var goals = rayPath(a, b);
		for (var point : goals) { // For each goal
			var pointAngles = new ArrayList<Integer>();
			var safety_counter = 0; // Used to exit early or in case of an infinite loop
			while (Point2D.dist(now, point) > 0.0002 && safety_counter++ <= 5) {
				var angle = Point2D.findAngle(now, point);

				// Debug message
				if (!validMove(angle, now))
					System.out.println("Invalid Move in Raypath");

				// Execute move
				angle = angleAdjuster(angle, lastAngle, now);
				lastAngle = angle;
				now.add(0.0003, angle);
				pointAngles.add(Integer.valueOf(angle));
			}
			pathAngles.addAll(pointAngles);
		}

		return pathAngles;

	}

	/**
	 * This method Ignores and sensor and makes sure it gets chosen last when
	 * chooseSensor() is called.
	 * 
	 * @param s The sensor to be ignored.
	 */
	protected abstract void ignoreSensor(Sensor s);

	/**
	 * Calculates the points to visit to travel from a to b in order to avoid
	 * obstacles. It works by a casting a ray from a to b and sees what obstacles
	 * collide with that ray. Then creates a graph with these obstacles and the
	 * target and using A* finds the shortest path.
	 * 
	 * @param a Origin point
	 * @param b Target point
	 * @return List of points to visit.
	 */
	private List<Point2D> rayPath(Point a, Point b) {
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

		var graph = createGraph(collisionObstacles, start, target); // Create the graph
		var path = shortestPath(graph, start, target); // Find the shortest path
		return path;
	}

	/**
	 * This method is what gets the drone physically moving and reading sensors. It
	 * chooses a sensor and approaches it. It reads it when it can always after
	 * moving. Once all the sensors have been read it begins going back to its
	 * starting position.
	 * 
	 * @return 1 if the algorithm got stuck anywhere and or the results might be
	 *         better with a different approach. 0 If the results are good enough.
	 */
	public int run() {
		int sensorsLeft = 33;
		var nextSensor = chooseSensor();
		var pos = new Point2D(drone.getPosition()); // Point used for calculations
		// Calculates the path for the first sensor
		ArrayList<Integer> path = findPath(drone.getStarting_position(), nextSensor.getPosition());
		while (sensorsLeft > 0 && steps < 150) {
			pos = new Point2D(drone.getPosition());
			// If a path couldn't be calculated probably due to the sensor being too close.
			if (path.size() == 0) {
				var angle = Point2D.findAngle(new Point2D(drone.getPosition()), new Point2D(nextSensor.getPosition()))
						- 10; // Try supposed angle +10
				if (!validMove(angle, pos))
					if (validMove(angle + 20, pos))
						angle += 20; // Try -10 to the other direction
				if (!validMove(angle, pos)) { // If you still can't do it just ignore the sensor and add it to the end.
					System.out.println("Last resort");
					deathCounter++; // This is done to avoid infinite loops
					ignoreSensor(nextSensor);
					nextSensor = chooseSensor();

					if (deathCounter == 60 && !lastResort) // Exit if infinite loop and it's not the last resort
															// algorithm
						return 1;
					continue;
				}
				angle = angleRemapper(angle); // Remap the calculated angle and move
				drone.move(angle);

			} else {
				var angle = path.get(0);
				if (!validMove(angle, pos)) {
					System.out.println("Attempted to execute invalid move. Recalculating");
					path = findPath(drone.getPosition(), nextSensor.getPosition());
					continue;
				}
				// Move the drone
				angle = angleRemapper(angle);
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
		// Calculate path needed to follow
		path = findPath(drone.getPosition(), drone.getStarting_position());
		var point = new Point2D(drone.getPosition());
		var starting = new Point2D(drone.getStarting_position());
		while (steps < 150 && Point2D.dist(point, starting) >= 0.0003) {
			// Move towards starting position
			drone.move(path.remove(0));
			steps++;
			// If you run out of path moves recalculated
			if (path.size() == 0)
				path = findPath(drone.getPosition(), drone.getStarting_position());

			// Update point for distance comparison
			point = new Point2D(drone.getPosition());
		}

		// If steps are 150 then it means we have not read all the sensors
		if (steps == 150) {
			drone.getLogger().addNonRead(sensors);
			return 1;
		}

		// Check if any sensors weren't read
		drone.getLogger().addNonRead(sensors);
		return 0;
	}

	/**
	 * Finds the shortest path inside a graph from the source point to the target
	 * using the A* algorithm.
	 * 
	 * @param graph  An interconnected weighted graph
	 * @param source Source point Needs to be in graph
	 * @param target Target point Needs to be in graph
	 * @return List of points that form the shortest path
	 */
	private List<Point2D> shortestPath(Graph<Point2D, Line2D> graph, Point2D source, Point2D target) {
		// Instantiate the algorithm
		var astar = new AStarShortestPath<Point2D, Line2D>(graph, new AStarAdmissibleHeuristic<Point2D>() {
			/**
			 * This calculates the A*'s heuristic. It's the euclidean distance between the
			 * two points.
			 */
			@Override
			public double getCostEstimate(Point2D sourceVertex, Point2D targetVertex) {
				return Point2D.dist(sourceVertex, targetVertex);
			}
		});

		// Calculate the path
		var path = astar.getPath(source, target);

		// Safety precaution Should be called when source is target or if something
		// failed.
		if (path == null) {
			var pathn = new ArrayList<Point2D>();
			pathn.add(target);
			return pathn;
		}
		// Return the list of points that form the path
		var pointList = path.getVertexList();
		return pointList;
	}

	/**
	 * Checks if there's an unobstructed path between source and target
	 * 
	 * @param source
	 * @param target
	 * @return true if there's a clear path false if not.
	 */
	private boolean straightPath(Point source, Point target) {
		var drone2D = new Point2D(source);
		var target2D = new Point2D(target);

		var targetLine = new Line2D(drone2D, target2D);
		var obstacles = map.getForbidden_areas();
		// Check against all walls
		for (var obstacle : obstacles) {
			for (var wall : obstacle.walls)
				if (Line2D.intersect(targetLine, wall) || Line2D.touching(targetLine, wall))
					return false;
		}
		return true;
	}

	/**
	 * This method checks if an angle is valid
	 * 
	 * @param angle
	 * @param position
	 * @return true if the move can be executed without hitting any walls or
	 *         obstacles.
	 */
	private boolean validMove(int angle, Point2D position) {
		var oldPos = position.clone();
		var nextPos = position.clone();
		nextPos.add(0.0003, angle);
		var testLine = new Line2D(oldPos, nextPos);

		if (!nextPos.inWalls()) {
			System.out.println("WALL FAIL");
			return false;
		}
		var obstacles = map.getForbidden_areas();

		for (var obstacle : obstacles) {
			if (nextPos.inObstacle(obstacle)) {
				return false;
			}

			for (var wall : obstacle.walls) {
				if (Line2D.intersect(testLine, wall)) {
					return false;
				}
			}
		}

		return true;
	}
}
