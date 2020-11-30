package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Logger {
	private final List<SensorReading> readings;
	private final List<Integer> angles;
	private final List<Point> pointsVisited;
	private final List<Integer> readingMoves;

	public Logger() {
		super();
		this.readings = new ArrayList<>();
		this.angles = new ArrayList<>();
		this.pointsVisited = new ArrayList<>();
		this.readingMoves = new ArrayList<>();
	}

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

	void logAngle(int angle) {
		angles.add(angle);
	}

	void logPos(Point p) {
		pointsVisited.add(p);
	}

	void logReading(SensorReading reading) {
		readings.add(reading);
		readingMoves.add(angles.size() - 1);
	}

	private FeatureCollection toCollection() {
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

	List<LogLine> createLines() {
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

	@Override
	public String toString() {
		return toCollection().toJson();
	}
}
