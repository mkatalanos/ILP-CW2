package uk.ac.ed.inf.aqmaps;

import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is used to write the logs to files.
 * 
 * @author marios
 *
 */
public class Writer {
	private final Logger log;
	private final ArgumentParser args;

	/**
	 * Public constructor of the class.
	 * 
	 * @param log  The drone's logger.
	 * @param args Standard input arguments.
	 */
	public Writer(Logger log, ArgumentParser args) {
		this.log = log;
		this.args = args;
	}

	/**
	 * Writes the flightpath file. Each line is of the form: index,
	 * startingPos.longitude, startingPos.latitude, angle, endingPos.longitude,
	 * endingPos.latitude, Sensor read after executing the move.
	 * 
	 * @throws IOException If the file cannot be written.
	 */
	public void writeAngles() throws IOException {
		var date = args.getDate();
		var day = date[0];
		var month = date[1];
		var year = date[2];
		var path = String.format("flightpath-%d-%d-%d.txt", day, month, year);

		var loglines = log.createLines();

		var fw = new FileWriter(path);
		for (var line : loglines)
			fw.write(line.toString());
		fw.close();
	}

	/**
	 * Creates the geoJson file with the path of the drone and the readings.
	 * 
	 * @throws IOException If the file cannot be written.
	 */
	public void writeJson() throws IOException {
		var date = args.getDate();
		var day = date[0];
		var month = date[1];
		var year = date[2];

		var path = String.format("readings-%d-%d-%d.geojson", day, month, year);

		var collection = log.toCollection();

		var fw = new FileWriter(path);

		fw.write(collection.toJson());

		fw.close();
	}

}
