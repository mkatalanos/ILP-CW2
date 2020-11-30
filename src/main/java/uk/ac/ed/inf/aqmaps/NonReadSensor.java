package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Feature;

public class NonReadSensor extends SensorReading {

	public NonReadSensor(Sensor s) {
		super(s);
	}

	public Feature toFeature() {
		var feature = Feature.fromGeometry(this.sensor.getPosition());
		var color = "#aaaaaa";
		feature.addStringProperty("rgb-string", color);
		feature.addStringProperty("marker-color", color);
		return feature;
	}
}
