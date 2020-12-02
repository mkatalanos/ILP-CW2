package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

/**
 * This class is used to log everything the drone does. It is also responsible
 * for creating the outputs which will then be written to a file.
 * 
 * @author marios
 *
 */
public class Logger {
	private final List<SensorReading> readings;
	private final List<Integer> angles;
	private final List<Point> pointsVisited;
	private final List<Integer> readingMoves;

	/**
	 * Public constructor of the class
	 */
	public Logger() {
		this.readings = new ArrayList<>();
		this.angles = new ArrayList<>();
		this.pointsVisited = new ArrayList<>();
		this.readingMoves = new ArrayList<>();
	}

	/**
	 * Adds to the sensor readings list all the sensors which were not read from the
	 * list.
	 * 
	 * @param sensors Sensors to be tested if they were read.
	 */
	void addNonRead(Sensor[] sensors) {
		var nonVisited = new ArrayList<Sensor>();
		outer: for (var s : sensors) {
			for (var reading : readings) {
				if (s.equals(reading.getSensor()))
					continue outer;
			}
			// If this point is reached the sensor was not found whatsoever.
			nonVisited.add(s);
		}

		for (var s : nonVisited)
			readings.add(new NonReadSensor(s));
	}

	/**
	 * This method creates the log lines which will then be written to a file.
	 * 
	 * @return A list of log lines which for every move hold starting position,
	 *         ending position, index and whether there was a sensor reading.
	 */
	public List<LogLine> createLines() {
		var lines = new ArrayList<LogLine>();
		var sensorIndex = 0;

		for (int i = 0; i < angles.size(); i++) {
			var line = readingMoves.contains(i)
					? new LogLine(i + 1, pointsVisited.get(i), pointsVisited.get(i + 1), angles.get(i),
							readings.get(sensorIndex++).getSensor())
					: new LogLine(i + 1, pointsVisited.get(i), pointsVisited.get(i + 1), angles.get(i));
			lines.add(line);
		}
		return lines;
	}

	/**
	 * Logs the angle given.
	 * 
	 * @param angle
	 */
	void logAngle(int angle) {
		angles.add(angle);
	}

	/**
	 * Logs that the following point was visited.
	 * 
	 * @param p
	 */
	void logPos(Point p) {
		pointsVisited.add(p);
	}

	/**
	 * Logs the sensor reading. It also logs the angle index at which the sensor was
	 * read.
	 * 
	 * @param reading
	 */
	void logReading(SensorReading reading) {
		readings.add(reading);
		readingMoves.add(angles.size() - 1);
	}

	/**
	 * Creates a feature collection from the read sensors and the drone's path.
	 * 
	 * @return FeatureCollection formed by the path and sensors.
	 */
	public FeatureCollection toCollection() {
		FeatureCollection fc;
		var sensorFeatureList = readings.stream().map(SensorReading::toFeature).collect(Collectors.toList());
		var path = Feature.fromGeometry(LineString.fromLngLats(pointsVisited));
		var features = new ArrayList<Feature>();
		if (angles.size() > 0)
			features.add(path);
		features.addAll(sensorFeatureList);
		fc = FeatureCollection.fromFeatures(features);
		return fc;
	}

}
