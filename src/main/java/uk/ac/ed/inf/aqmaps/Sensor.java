package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

public class Sensor {

	private String location;
	private Point position;
	private double battery;
	private String reading;

	public Sensor(String location, double battery, double reading) {
		this.location = location;
		this.battery = battery;
		this.reading = String.valueOf(reading);
	}

	@Override
	public String toString() {
		return String.format("location: %s, battery:%.3f, reading:%s", this.location, this.battery, this.reading);
	}

}
