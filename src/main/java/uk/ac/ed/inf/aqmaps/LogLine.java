package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

/**
 * This class is used to represent a line which will be then written to a file.
 * 
 * @author marios
 *
 */
public class LogLine {
	private final int index;
	private final Point startingPos;
	private final Point endingPos;
	private final int angle;
	private Sensor s;

	/**
	 * Public constructor of the object in the case where a sensor was not read.
	 * 
	 * @param index       Move's index.
	 * @param startingPos Drone's starting position before executing the move.
	 * @param endingPos   Drone's ending position after executing the move.
	 * @param angle       Angle at which the drone moved.
	 */
	public LogLine(int index, Point startingPos, Point endingPos, int angle) {
		this.index = index;
		this.startingPos = startingPos;
		this.endingPos = endingPos;
		this.angle = angle;
		this.s = null;
	}

	/**
	 * Public constructor of the object in the case where a sensor was read.
	 * 
	 * @param index       Move's index.
	 * @param startingPos Drone's starting position before executing the move.
	 * @param endingPos   Drone's ending position after executing the move.
	 * @param angle       Angle at which the drone moved.
	 * @param s           Sensor which was read after executing the move.
	 */
	public LogLine(int index, Point startingPos, Point endingPos, int angle, Sensor s) {
		this.index = index;
		this.startingPos = startingPos;
		this.endingPos = endingPos;
		this.angle = angle;
		this.s = s;
	}

	/**
	 * String representation of the object. It's what gets written to the file. Of
	 * the form: int,double,double,int,double,double,string
	 */
	public String toString() {
		if (s == null)
			return String.format("%d,%f,%f,%d,%f,%f,%s\n", index, startingPos.longitude(), startingPos.latitude(),
					angle, endingPos.longitude(), endingPos.latitude(), "null");
		else
			return String.format("%d,%f,%f,%d,%f,%f,%s\n", index, startingPos.longitude(), startingPos.latitude(),
					angle, endingPos.longitude(), endingPos.latitude(), s.getLocation());

	}
}
