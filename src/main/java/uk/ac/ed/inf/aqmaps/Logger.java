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

	public Logger() {
		super();
		this.readings = new ArrayList<>();
		this.angles = new ArrayList<>();
		this.pointsVisited = new ArrayList<>();
	}

	void logPos(Point p) {
		pointsVisited.add(p);
	}

	void logAngle(int angle) {
		angles.add(angle);
	}

	void logReading(SensorReading reading) {
		readings.add(reading);
	}

	FeatureCollection toCollection() {
		FeatureCollection fc;
		var sensorFeatureList = readings.stream().map(SensorReading::toFeature).collect(Collectors.toList());
		var path = Feature.fromGeometry(LineString.fromLngLats(pointsVisited));
		var features = new ArrayList<Feature>();
		features.add(path);
		features.addAll(sensorFeatureList);
		fc = FeatureCollection.fromFeatures(features);
		return fc;
	}

	@Override
	public String toString() {
		return toCollection().toJson();
	}
}
