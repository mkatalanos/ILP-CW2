package uk.ac.ed.inf.aqmaps;

import java.io.IOException;

/**
 * This class acts as the entryway to the program. It is responsible for parsing
 * the arguments creating the map, the drone and running the algorithm. If the
 * algorithm fails it attempts another version of it. Afterwards, the two logs are created.
 * 
 * @author marios
 *
 */
public class App {
	public static void main(String[] args) {
		//Argument Parsing
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

		//Create a connection
		var connector = new Connector("localhost", arguments.getPort());

		//Create a map
		var map = new MapMaker(connector, arguments).make();
		
		//Create a drone
		var drone = new Drone(arguments);

		//Backup the drone and the map
		var drone_bak = new Drone(arguments);
		var map_bak = new MapMaker(connector, arguments).make();

		//Attempt running the drone with the twoOptTour algorithm
		Algorithm approach = new TwoOptTour(drone, map, arguments);
		//If it fails use the greedy approach
		if (approach.run() == 1) {
			drone = drone_bak;
			map = map_bak;
			System.out.println("Algorithm swap");
			approach = new ClosestFirst(drone, map);
			approach.run();
		}
		//Get the drone's log
		var log = drone.getLogger();

		//Add any non-read sensors
		log.addNonRead(map.getSensors());

		//Write the files
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

		// Print steps
		System.out.println(drone.getLogger().createLines().size());

	}
}
