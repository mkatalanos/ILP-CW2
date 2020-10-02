package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

public class ArgumentParser {
	private final int[] date;
	private final Point startingPos;
	private final int randomSeed;
	private final int port;

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

	public int[] getDate() {
		return date;
	}

	public Point getStartingPos() {
		return startingPos;
	}

	public int getRandomSeed() {
		return randomSeed;
	}

	public int getPort() {
		return port;
	}

	
	
}
