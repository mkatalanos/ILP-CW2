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

		List<Feature> features = new ArrayList<>();
//
		features.add(Feature.fromGeometry(LineString.fromLngLats(drone.poslog)));
		var sensors = map.getSensors();
		for (Sensor s : sensors) {
			features.add(Feature.fromGeometry(s.getPosition()));
		}

		try {
			var areas = connector.forbiddenAreas();
			for (var area : areas.features()) {
				features.add(area);
			}
		} catch (Exception e) {
		}
//		System.out.println(FeatureCollection.fromFeatures(features).toJson());

//		drone.rayPath(new Point2D(-3.1865724040985173, 55.944938036504546).toPoint(),
//				new Point2D(-3.188319, 55.945518).toPoint()).forEach(point -> {
//					var f = Feature.fromGeometry(point);
//					f.addStringProperty("marker-color", "#0000ff");
//
//					features.add(f);
//				});
//		var f = Feature.fromGeometry(new Point2D(-3.186572,55.944938).toPoint());
//		f.addStringProperty("marker-color", "#ff0000");
//		f.addStringProperty("type", "START");
//		features.add(f);
//		var f2 = Feature.fromGeometry(new Point2D(-3.187113,55.945553).toPoint());
//		f2.addStringProperty("marker-color", "#ff0000");
//		f2.addStringProperty("type", "END");
//		features.add(f2);

		System.out.println(FeatureCollection.fromFeatures(features).toJson());
	}
}
