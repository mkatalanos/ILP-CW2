package uk.ac.ed.inf.aqmaps;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.http.HttpRequest;

import org.junit.Before;
import org.junit.Test;

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
}
