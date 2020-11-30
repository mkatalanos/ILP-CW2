package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

public class LogLine {
	private int index;
	private Point startingPos;
	private Point endingPos;
	private int angle;
	private Sensor s;

	public LogLine(int index, Point startingPos, Point endingPos, int angle) {
		this.index = index;
		this.startingPos = startingPos;
		this.endingPos = endingPos;
		this.angle = angle;
		this.s = null;
	}

	public LogLine(int index, Point startingPos, Point endingPos, int angle, Sensor s) {
		this.index = index;
		this.startingPos = startingPos;
		this.endingPos = endingPos;
		this.angle = angle;
		this.s = s;
	}

	public String toString() {
		if (s == null)
			return String.format("%d,%f,%f,%d,%f,%f,%s\n", index, startingPos.longitude(), startingPos.latitude(),
					angle, endingPos.longitude(), endingPos.latitude(), "null");
		else
			return String.format("%d,%f,%f,%d,%f,%f,%s\n", index, startingPos.longitude(), startingPos.latitude(),
					angle, endingPos.longitude(), endingPos.latitude(), s.getLocation());

	}
}
