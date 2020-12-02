package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Feature;

/**
 * This class is used to represent that a sensor was read. It parses the reading
 * according to the battery status and contains a method to convert the object
 * to a feature.
 * 
 * @author marios
 *
 */
public class SensorReading {

	protected final Sensor sensor;
	private final double parsedReading;
	private final boolean needsBattery;

	/**
	 * Public constructor for the class.
	 * 
	 * @param s the sensor read.
	 */
	public SensorReading(Sensor s) {
		this.sensor = s;
		var battery = s.getBattery();
		needsBattery = battery < 10;
		parsedReading = !needsBattery ? parseReading(s.getReading()) : -1.0;
	}

	/**
	 * Public getter for the sensor
	 * 
	 * @return the sensor of the reading.
	 */
	public Sensor getSensor() {
		return sensor;
	}

	/**
	 * Parses a reading. should only be called if battery status is >10.
	 * 
	 * @param reading
	 * @return The value of the reading. -1.0 if the reading is invalid.
	 */
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

	/**
	 * Converts the object to a complete feature.
	 * 
	 * @return Feature with a color symbol maekrer and location.
	 */
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

	/**
	 * This method finds the appropriate symbol for the feature creation.
	 * 
	 * @return Appropriate symbol according to reading value.
	 */
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

	/**
	 * This method gets the appropriate colour for the feature creation.
	 * 
	 * @return Appropriate color according to reading value.
	 */
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
