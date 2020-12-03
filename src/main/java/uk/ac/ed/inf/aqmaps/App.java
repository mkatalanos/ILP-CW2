package uk.ac.ed.inf.aqmaps;

import java.io.IOException;

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

		var drone_bak = new Drone(arguments);
		var map_bak = new MapMaker(connector, arguments).make();

		Algorithm approach = new TwoOptTour(drone, map, arguments);
		if (approach.run() == 1) {
			drone = drone_bak;
			map = map_bak;
			System.out.println("Algorithm swap");
			approach = new ClosestFirst(drone, map);
			approach.run();
		}
		var log = drone.getLogger();

		log.addNonRead(map.getSensors());

		Writer w = new Writer(log, arguments);
		try {
			w.writeAngles();
			w.writeJson();
		} catch (IOException e) {
			System.out.println("Could not write to files!"); // In case the files could not be written output the file
																// to
																// stdout
			System.exit(1);
		}
		
		//Print steps
		System.out.println(drone.getLogger().createLines().size());

	}
}
