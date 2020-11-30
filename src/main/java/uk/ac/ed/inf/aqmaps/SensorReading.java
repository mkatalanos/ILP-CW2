package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Feature;

public class SensorReading {

	protected Sensor sensor;
	private double parsedReading;
	private boolean needsBattery;

	public SensorReading(Sensor s) {
		this.sensor = s;
		var battery = s.getBattery();
		needsBattery = battery < 10;
		parsedReading = !needsBattery ? parseReading(s.getReading()) : -1.0;
	}

	public Sensor getSensor() {
		return sensor;
	}

	private Double parseReading(String reading) {
		Double d;
		try {
			d = Double.valueOf(reading);
		} catch (Exception e) {
			d = -1.0;
		}
		if (needsBattery)
			d = -1.0;
		return d;
	}

	public Feature toFeature() {
		var feature = Feature.fromGeometry(sensor.getPosition());
		feature.addStringProperty("marker-size", "medium");
		var color = colorFromData();
		feature.addStringProperty("rgb-string", color);
		feature.addStringProperty("marker-color", color);
		var symbol = symbolFromData();
		feature.addStringProperty("marker-symbol", symbol);
		feature.addStringProperty("location", sensor.getLocation());
		return feature;
	}

	private String symbolFromData() {
		String s = "";
		if (0 <= parsedReading && parsedReading < 128)
			s = "lighthouse";
		else if (128 <= parsedReading && parsedReading < 256)
			s = "danger";
		else
			s = "cross";
		return s;
	}

	private String colorFromData() {
		String s = "";
		if (0 <= parsedReading && parsedReading < 32)
			s = "#00ff00";
		else if (32 <= parsedReading && parsedReading < 64)
			s = "#40ff00";
		else if (64 <= parsedReading && parsedReading < 96)
			s = "#80ff00";
		else if (96 <= parsedReading && parsedReading < 128)
			s = "#c0ff00";
		else if (128 <= parsedReading && parsedReading < 160)
			s = "#ffc000";
		else if (160 <= parsedReading && parsedReading < 192)
			s = "#ff8000";
		else if (192 <= parsedReading && parsedReading < 224)
			s = "#ff4000";
		else if (224 <= parsedReading && parsedReading < 256)
			s = "#ff0000";
		else
			s = "#000000";
		return s;
	}

}
