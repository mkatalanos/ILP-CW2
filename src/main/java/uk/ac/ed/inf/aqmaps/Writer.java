package uk.ac.ed.inf.aqmaps;

import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	private final Logger log;
	private final ArgumentParser args;

	public Writer(Logger log, ArgumentParser args) {
		this.log = log;
		this.args = args;
	}

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
