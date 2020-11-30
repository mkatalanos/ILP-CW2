package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Point;

public class Drone {
	private Point position;
	private final Point starting_position;
	private final Logger logger;
	public final List<Point> poslog;

	public Drone(ArgumentParser arguments) {
		this.starting_position = arguments.getStartingPos();
		this.position = starting_position;
		this.logger = new Logger();
		logger.logPos(getStarting_position());
		poslog = new ArrayList<>();
		poslog.add(starting_position);
	}

	public double distanceToSensor(Sensor s) {
		var sPos = s.getPosition();
		double distance = Math.sqrt(Math.pow(position.latitude() - sPos.latitude(), 2)
				+ Math.pow(position.longitude() - sPos.longitude(), 2));
		return distance;
	}

	public Logger getLogger() {
		return logger;
	}

	public Point getPosition() {
		return position;
	}

	public Point getStarting_position() {
		return starting_position;
	}

	public String log() {
		return logger.toString();
	}

	public void move(int angle) {
		var pos2D = new Point2D(this.getPosition());
		pos2D.add(0.0003, angle);
		this.position = Point.fromLngLat(pos2D.x, pos2D.y);
		poslog.add(position);
		logger.logAngle(angle);
		logger.logPos(position);
	}

	public void readSensor(Sensor s) {
		var reading = new SensorReading(s);
		logger.logReading(reading);
	}
}
