package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.alg.tour.TwoOptHeuristicTSP;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * This class is used to choose the order of vistited sensors by using the
 * twoOpt heuristic. It extends Algorithm so that an instance of this class can
 * run the algorithm with the sensors being chosen from here.
 * 
 * @author marios
 *
 */
public class TwoOptTour extends Algorithm {

	private List<Sensor> sensorOrder;
	private final SensorComparator c;
	private final long seed;
	protected boolean lastResort = false;

	/**
	 * Public constructor of the class. Calls the superclass constructor and also
	 * sets the variables needed for using twoOpt.
	 * 
	 * @param d         The drone
	 * @param map       The map
	 * @param arguments The standard input arguments.
	 */
	public TwoOptTour(Drone d, MapData map, ArgumentParser arguments) {
		super(d, map);
		this.seed = arguments.getRandomSeed();
		var graph = generateSensorGraph();
		var points = twoOptTour(graph);
		this.c = new SensorComparator(d);
		createOrder(points);
	}

	/**
	 * This method creates a dense interconnected graph with all the sensors with
	 * weights the euclidean distance between each two.
	 * 
	 * @return A graph formed with all the sensors.
	 */
	private Graph<Point2D, Line2D> generateSensorGraph() {
		var graph = new SimpleWeightedGraph<Point2D, Line2D>(Line2D.class);
		var vertices = new HashSet<Point2D>();

		// Add all sensors as vertices
		for (var sensor : sensors)
			vertices.add(new Point2D(sensor.getPosition()));

		var edges = new ArrayList<Line2D>();
		var verticesArr = vertices.toArray();

		// Connect every vertex to all others.
		for (int i = 0; i < verticesArr.length - 1; i++) {
			for (int j = i + 1; j < verticesArr.length; j++) {
				var edge = new Line2D((Point2D) verticesArr[i], (Point2D) verticesArr[j]);
				edges.add(edge);
			}
		}

		// Add vertices to graph
		for (var vertex : vertices)
			graph.addVertex(vertex);
		for (var edge : edges)
			graph.addEdge(edge.a, edge.b, edge);
		// Set weights to lengths
		for (var edge : graph.edgeSet())
			graph.setEdgeWeight(edge, edge.getLength());

		return graph;
	}

	/**
	 * This method calculates the tour from which the sensors will be chosen.
	 * 
	 * @param graph A dense interconnected graph of Points where each point is the
	 *              location of a sensor.
	 * @return A list of points where each point is the location of a sensor.
	 */
	private List<Point2D> twoOptTour(Graph<Point2D, Line2D> graph) {
		var random = new Random(seed);
		var twoOpt = new TwoOptHeuristicTSP<Point2D, Line2D>(5000, random);
		var tour = twoOpt.getTour(graph);

		var path = tour.getVertexList();
		path.remove(path.size() - 1);
		return path;
	}

	/**
	 * This method converts the list of vertices calculated from twoOpt to a list of
	 * sensors.
	 * 
	 * @param vertices List of vertices with a vertex at every point.
	 */
	private void createOrder(List<Point2D> vertices) {
		// For every vertex you find the closest sensor
		var sensorStream = vertices.stream().map(point -> sensorFromPoint2D(point)).filter(sensor -> sensor != null);
		this.sensorOrder = sensorStream.collect(Collectors.toList());
		var min = Collections.min(sensorOrder, c);
		// Rotate the order to put the closest one first.
		Collections.rotate(sensorOrder, -sensorOrder.indexOf(min));

	}

	/**
	 * This method maps a point to a sensor. It essentially looks through all the
	 * sensors and sees which sensor is the closest.
	 * 
	 * @param point To be checked
	 * @return Sensor that matches.
	 */
	private Sensor sensorFromPoint2D(Point2D point) {
		var minDist = Double.MAX_VALUE;
		Sensor minSensor = null;
		for (int i = 0; i < sensors.length; i++) {
			var testSensor = sensors[i];
			var dist = Point2D.dist(new Point2D(testSensor.getPosition()), point);
			if (dist <= minDist) {
				minDist = dist;
				minSensor = testSensor;
			}
		}
		// Not A sensor
		if (minDist > 0.0001)
			return null;

		return minSensor;
	}

	/**
	 * This method takes the given sensor and adds it to the end of the list.
	 * 
	 * @param s Sensor to be added
	 */
	@Override
	protected void ignoreSensor(Sensor s) {
		sensorOrder.add(s);
	}

	/**
	 * This method is used to choose the next sensor. It returns the next chosen
	 * sensor removing it from the list of available ones.
	 */
	@Override
	protected Sensor chooseSensor() {
		return sensorOrder.remove(0);
	}

}
