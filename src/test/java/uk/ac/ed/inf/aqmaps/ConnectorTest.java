package uk.ac.ed.inf.aqmaps;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.http.HttpRequest;

import org.junit.Before;
import org.junit.Test;

import com.mapbox.geojson.Point;

public class ConnectorTest {

	Connector connector;

	@Before
	public void makeConnector() {
		this.connector = new Connector("localhost", 80);
	}

	@Test
	public void hostTest() {
		var generated = connector.getHost();
		var host = "http://localhost:80/";
		assertTrue("Generated: " + generated + " Should be: " + host, generated.equals(host));
	}

	@Test
	public void requestCreatorTest() {
		var generated = this.connector.requestCreator("a/b/c/d.json");
		var request = HttpRequest.newBuilder(URI.create("http://localhost:80/a/b/c/d.json")).build();
		assertTrue(request.equals(generated));
	}

	@Test
	public void dateDataTest() {
		// Test is assumed to pass if it returns 33 sensors;
		try {
			var sensors = this.connector.dateData(2020, 12, 2);
			assertTrue(sensors.length == 33);
		} catch (Exception e) {
			assert (false);
		}
	}

	@Test
	public void forbidenBuildingsTest() {
		try {
			var buildings = this.connector.forbiddenAreas();
			assertTrue(buildings.features().size() != 0);
		} catch (Exception e) {
			assert (false);
		}
	}

	@Test
	public void W3WTest() {
		try {
			var sensors = this.connector.dateData(2020, 12, 2);
			var wrd = this.connector.getWord(sensors[0]);

			assertTrue(wrd.getCoordinate().toPoint().getClass() == Point.class);
		} catch (Exception e) {
			assert (false);
		}
	}
}
