package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.SimpleWeightedGraph;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class App {
	public static void main(String[] args) {

		if (args.length != 7) {
			System.out.println("Missing arguments! Format: DD MM YYYY Latitude Longitude Seed Port");
			return;
		}
		ArgumentParser arguments;
		try {
			arguments = new ArgumentParser(args);
		} catch (Exception e) {
			System.out.println("Could not parse arguments!\nMake sure that they are in the correct format.");
			return;
		}

		var connector = new Connector("localhost", arguments.getPort());

		var map = new MapMaker(connector, arguments).make();

		var drone = new Drone(arguments, map);

		var algorithm = new ClosestFirst(drone, map);

		algorithm.run();
//
//		List<Feature> features = new ArrayList<>();
//
//		features.add(Feature.fromGeometry(LineString.fromLngLats(drone.poslog)));
//		var sensors = map.getSensors();
//		for (Sensor s : sensors) {
//			features.add(Feature.fromGeometry(s.getPosition()));
//		}
//		System.out.println(FeatureCollection.fromFeatures(features).toJson());
//		map.getForbidden_areas();

		var a = new Point2D(0, 0);
		var b = new Point2D(52, 100);
		var graph = drone.createGraph(map.getForbidden_areas(), a, b);
//		graph.edgeSet().forEach(edge -> {
////			System.out.println(String.format("%s", edge));
//			System.out.printf("%s,weight: %e,length: %e\n", edge, graph.getEdgeWeight(edge), edge.getLength());
//		});

//		var vv = drone.astar(graph, a, b);
//		System.out.println(vv);

	}
}
