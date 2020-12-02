package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

/**
 * This class deals with the arguments from the standard input.
 * 
 * @author marios
 *
 */
public class ArgumentParser {
	private final int[] date; // Day,Month,Year
	private final Point startingPos;
	private final int randomSeed;
	private final int port;

	/**
	 * Constructor of the class
	 * 
	 * @param args - The arguments from standard input.
	 * @throws NumberFormatException - If the arguments cannot be converted
	 *                               correctly to numbers.
	 */
	public ArgumentParser(String[] args) throws NumberFormatException {
		int day, month, year, seed, port;
		double latitude, longitude;

		day = Integer.parseInt(args[0]);
		month = Integer.parseInt(args[1]);
		year = Integer.parseInt(args[2]);
		latitude = Double.parseDouble(args[3]);
		longitude = Double.parseDouble(args[4]);
		seed = Integer.parseInt(args[5]);
		port = Integer.parseInt(args[6]);

		// Date
		this.date = new int[] { day, month, year };
		// Pos
		this.startingPos = Point.fromLngLat(longitude, latitude);
		// Seed
		this.randomSeed = seed;
		// Port
		this.port = port;
	}

	/**
	 * Public getter for the date array
	 * 
	 * @return int[] date of the form: Day,Month,Year
	 */
	public int[] getDate() {
		return date;
	}

	/**
	 * Public getter for the port number to the server.
	 * 
	 * @return int port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Public getter for the random seed given from the arguments
	 * 
	 * @return int random seed.
	 */
	public int getRandomSeed() {
		return randomSeed;
	}

	/**
	 * Public getter for the drone's starting position.
	 * 
	 * @return Point at the starting position.
	 */
	public Point getStartingPos() {
		return startingPos;
	}

}
