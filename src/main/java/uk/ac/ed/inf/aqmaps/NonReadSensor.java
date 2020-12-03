package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Feature;

/**
 * This class is used to represent a sensor that has not been read. Extends
 * sensor reading.
 * 
 * @author marios
 *
 */
public class NonReadSensor extends SensorReading {

	/**
	 * Public constructor. Just calls the parent constructor.
	 * 
	 * @param s The sensor which was not read.
	 */
	public NonReadSensor(Sensor s) {
		super(s);
	}

	/**
	 * Creates a feature representation of the sensor
	 * 
	 * @return Feature A complete feature with color.
	 */
	@Override
	public Feature toFeature() {
		var feature = Feature.fromGeometry(this.sensor.getPosition());
		var color = "#aaaaaa";
		feature.addStringProperty("rgb-string", color);
		feature.addStringProperty("marker-color", color);
		feature.addStringProperty("location", sensor.getLocation());
		return feature;
	}
}
