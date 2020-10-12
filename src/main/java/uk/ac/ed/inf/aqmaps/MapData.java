package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.FeatureCollection;

public class MapData {

	private FeatureCollection forbidden_areas;
	private Sensor[] sensors;

	MapData(FeatureCollection forbidden_areas, Sensor[] sensors) {
		this.forbidden_areas = forbidden_areas;
		this.sensors = sensors;
	}

}
