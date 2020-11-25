package uk.ac.ed.inf.aqmaps;

public class SensorReading {

	private Sensor sensor;
	private double parsedReading;
	private boolean needsBattery;

	public SensorReading(Sensor s) {
		this.sensor = s;
		var battery = s.getBattery();
		needsBattery = battery < 0.10;
		parsedReading = !needsBattery ? parseReading(s.getReading()) : 0;
	}

	public static Double parseReading(String reading) {
		if (reading != null && reading != "null" && reading != "NaN")
			return Double.valueOf(reading);
		else
			return null;
	}

}
