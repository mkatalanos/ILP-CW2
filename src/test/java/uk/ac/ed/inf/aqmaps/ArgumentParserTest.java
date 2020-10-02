package uk.ac.ed.inf.aqmaps;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.mapbox.geojson.Point;

public class ArgumentParserTest {

	@Test
	public void ArgumentParserDate() {
		var args = new String[] { "02", "12", "1999", "68.54321", "-7.85421", "159789", "80" };
		var parser = new ArgumentParser(args);
		var fromParser = parser.getDate();
		var date = new int[] { 2, 12, 1999 };

		assertTrue(String.format("Parser: %d %d %d, Should be: %d %d %d", fromParser[0], fromParser[1], fromParser[2],
				date[0], date[1], date[2]), Arrays.equals(date, fromParser));
	}

	@Test
	public void ArgumentParserPos() {
		var args = new String[] { "02", "12", "1999", "68.54321", "-7.85421", "159789", "80" };
		var parser = new ArgumentParser(args);
		var fromParser = parser.getStartingPos();
		var startingPos = Point.fromLngLat(-7.85421, 68.54321);

		assertTrue(String.format("Parser: (%f,%f), Should be: (%f,%f)", fromParser.longitude(), fromParser.latitude(),
				startingPos.longitude(), startingPos.latitude()), fromParser.equals(startingPos));
	}

	@Test
	public void ArgumentParserSeed() {
		var args = new String[] { "02", "12", "1999", "68.54321", "-7.85421", "159789", "80" };
		var parser = new ArgumentParser(args);
		var fromParser = parser.getRandomSeed();
		var seed = 159789;

		assertTrue(String.format("Parser: %d, Should be: %d", fromParser, seed), fromParser == seed);
	}

	@Test
	public void ArgumentParserPort() {
		var args = new String[] { "02", "12", "1999", "68.54321", "-7.85421", "159789", "80" };
		var parser = new ArgumentParser(args);
		var fromParser = parser.getPort();
		var port = 80;

		assertTrue(String.format("Parser: %d, Should be: %d", fromParser, port), fromParser == port);
	}

}
