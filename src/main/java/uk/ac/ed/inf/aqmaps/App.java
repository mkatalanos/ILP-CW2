package uk.ac.ed.inf.aqmaps;

public class App {
	public static void main(String[] args) {

		if (args.length != 7) {
			System.out.println("Missing arguments! Format: DD MM YYYY Latitude Longitude Seed Port");
			return;
		}
		ArgumentParser arguments;
		try {
			arguments = new ArgumentParser(args);
		} catch (Exception e) {
			System.out.println("Could not parse arguments!\nMake sure that they are in the correct format.");
			return;
		}

		arguments.getClass();

		var connector = new Connector("localhost", 80);

		try {
			var json = connector.dateData(2020, 12, 2);
			int i=0;
			for (var obj : json) {
				System.out.printf("%d ",i++);
				System.out.println(obj);}

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}
}
