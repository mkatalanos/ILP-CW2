package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

public class Sensor {

	private String location;
	private Point position;
	private double battery;

	private String reading;

	public double getBattery() {
		return battery;
	}

	public String getLocation() {
		return this.location;
	}

//	public Sensor(String location, double bat, double reading) {
//		this.location = location;
//		this.battery = 0;
//		this.reading = String.valueOf(reading);
//	}

	public Point getPosition() {
		return position;
	}

	public String getReading() {
		return reading;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return String.format("location: %s, battery:%.3f, reading:%s", this.location, this.battery, this.reading);
	}

}
