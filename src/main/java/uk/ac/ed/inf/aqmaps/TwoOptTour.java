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

public class TwoOptTour extends Algorithm {

	private List<Sensor> sensorOrder;
	private SensorComparator c;
	private long seed;

	public TwoOptTour(Drone d, MapData map, ArgumentParser arguments) {
		super(d, map);
		this.seed = arguments.getRandomSeed();
		var graph = generateSensorGraph();
		var points = twoOptTour(graph);
		this.c = new SensorComparator(d);
		createOrder(points);
	}

	Graph<Point2D, Line2D> generateSensorGraph() {
		var graph = new SimpleWeightedGraph<Point2D, Line2D>(Line2D.class);
		var vertices = new HashSet<Point2D>();
		for (var sensor : sensors)
			vertices.add(new Point2D(sensor.getPosition()));

		var edges = new ArrayList<Line2D>();
		var verticesArr = vertices.toArray();

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

	List<Point2D> twoOptTour(Graph<Point2D, Line2D> graph) {
		var random = new Random();
		random.setSeed(seed);

		System.out.println(random.nextInt());
		var twoOpt = new TwoOptHeuristicTSP<Point2D, Line2D>(2000, random);
//		var nearestInsert = new NearestInsertionHeuristicTSP<Point2D, Line2D>();
		var tour = twoOpt.getTour(graph);
//		tour = twoOpt.improveTour(tour);
		var path = tour.getVertexList();
		path.remove(path.size() - 1);
		return path;
	}

	void createOrder(List<Point2D> vertices) {
		var sensorStream = vertices.stream().map(point -> sensorFromPoint2D(point)).filter(sensor -> sensor != null);
		this.sensorOrder = sensorStream.collect(Collectors.toList());
		var min = Collections.min(sensorOrder, c);
		Collections.rotate(sensorOrder, -sensorOrder.indexOf(min));

		System.out.println(sensorOrder.size());
	}

	Sensor sensorFromPoint2D(Point2D point) {
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

	@Override
	public Sensor chooseSensor() {
//		if (Point2D.dist(new Point2D(sensorOrder.get(0).getPosition()), new Point2D(drone.getPosition())) > 0.0001)
		return sensorOrder.remove(0);
//		else
//			return sensorOrder.remove(1);
	}

}
