package uk.ac.ed.inf.aqmaps;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class MapMakerTest {
	MapMaker mapmaker;
	MapData map;

	@Before
	public void prepareTest() {
		var connector = new Connector("localhost", 80);
		var arguments = new ArgumentParser(new String[] { "02", "12", "2020", "0", "0", "1234", "80" });
		this.mapmaker = new MapMaker(connector, arguments);
		this.map = mapmaker.make();
	}

	@Test
	public void buildingsTest() {
		System.out.println(map.getForbidden_areas().get(0).walls.get(0).toString());
		assertTrue(map.getForbidden_areas().size() != 0);
	}

	@Test
	public void sensorsTest() {
		assertTrue(map.getSensors().length != 0);
	}
}
