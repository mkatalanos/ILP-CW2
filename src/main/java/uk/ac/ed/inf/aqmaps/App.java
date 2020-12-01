package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

		var drone = new Drone(arguments);

		Algorithm algorithm = new TwoOptTour(drone, map, arguments);
//		Algorithm algorithm = new ClosestFirst(drone, map);

		algorithm.run();

		var log = drone.getLogger();

		Writer w = new Writer(log, arguments);
		try {
			w.writeAngles();
			w.writeJson();
		} catch (IOException e) {
			System.out.println("Could not write to files!"); // In case the files could not be written output the file
																// to
																// stdout
			return;
		}

		List<Feature> features = new ArrayList<>();
		drone.getLogger().addNonRead(map.getSensors());
		features.add(Feature.fromGeometry(LineString.fromLngLats(drone.poslog)));
		var sensors = map.getSensors();
		for (Sensor s : sensors) {
			features.add(Feature.fromGeometry(s.getPosition()));
		}
		// Show Buildings
		try {
			var areas = connector.forbiddenAreas();
			for (var area : areas.features()) {
				features.add(area);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		var f = Feature.fromGeometry(arguments.getStartingPos());
		f.addStringProperty("marker-color", "#ff0000");
		f.addStringProperty("type", "START");
		features.add(f);
		// WALLS:
		var points = new ArrayList<Point>();
		var pa = new Point2D(-3.192473, 55.946233).toPoint();
		var pd = new Point2D(-3.184319, 55.946233).toPoint();
		var ka = new Point2D(-3.192473, 55.942617).toPoint();
		var kd = new Point2D(-3.184319, 55.942617).toPoint();
		points.add(pa);
		points.add(pd);
		points.add(kd);
		points.add(ka);
		points.add(pa);
		var wall = LineString.fromLngLats(points);
		var wallF = Feature.fromGeometry(wall);
		wallF.addStringProperty("stroke", "#0000ff");
		features.add(wallF);

		System.out.println(FeatureCollection.fromFeatures(features).toJson());
		System.out.println(algorithm.steps);
	}
}
