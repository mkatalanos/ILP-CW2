package uk.ac.ed.inf.aqmaps;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;
import com.mapbox.geojson.FeatureCollection;

/**
 * This class handles all connections to the server, downloading and parsing the
 * appropriate files.
 * 
 * @author marios
 *
 */
public class Connector {

	private String host;
	private HttpClient client;

	/**
	 * Public constructor of the class
	 * 
	 * @param host The address of the server. Example: 0.0.0.0 or localhost.
	 * @param port The port to which the client should connect.
	 */
	public Connector(String host, int port) {
		this.client = HttpClient.newHttpClient();
		this.host = String.format("http://%s:%d/", host, port);

	}

	/**
	 * This method connects to the server and gathers the sensor list for the date
	 * specified.
	 * 
	 * @param year  The year of the date.
	 * @param month The month of the date.
	 * @param day   The day of the date.
	 * @return Sensor[] A list of all sensors that belong to that date.
	 * @throws Exception If downloading the sensors or parsing them fails for any
	 *                   reason.
	 */
	public Sensor[] dateData(int year, int month, int day) throws Exception {
		var request = requestCreator(String.format("maps/%d/%02d/%02d/air-quality-data.json", year, month, day));

		var response = client.send(request, BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			var body = response.body();
			var sensors = new Gson().fromJson(body, Sensor[].class);
			return sensors;
		} else
			System.out.println("Failed to get data");
		return null;

	}

	/**
	 * This method connects to the server and downloads the forbidden areas that the
	 * drone cannot fly over.
	 * 
	 * @return FeatureCollection With the Features of the obstacles.
	 * @throws Exception If for any reason the parsing or downloading fails.
	 */
	public FeatureCollection forbiddenAreas() throws Exception {
		var request = requestCreator("buildings/no-fly-zones.geojson");
		var response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() == 200) {
			var body = response.body();
			var buildings = FeatureCollection.fromJson(body);
			return buildings;
		} else {
			System.out.println("Failed to get data");
			System.exit(1);
		}
		return null;
	}

	/**
	 * Public getter for the host.
	 * 
	 * @return host
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * This method gets a sensor finds its location in w3w form and returns a W3W
	 * object that represents that location.
	 * 
	 * @param s Sensor to be used.
	 * @return W3W The object that represents that location.
	 * @throws Exception If for any reason downloading or parsing the downloaded
	 *                   file fails.
	 */
	public W3W getWord(Sensor s) throws Exception {
		var words = s.getLocation();
		String[] args = words.split("\\.");
		var request = requestCreator(String.format("words/%s/%s/%s/details.json", args[0], args[1], args[2]));
		var response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() == 200) {
			var body = response.body();
			var w3w = new Gson().fromJson(body, W3W.class);
			return w3w;
		} else {
			System.out.println("Failed to get position from server");
			System.exit(1);
		}
		return null;
	}

	/**
	 * This method creates a request that once sent grabs the specified file.
	 * 
	 * @param path_to_file Relative path to the file in the server
	 * @return HttpRequest Request to the server.
	 * @throws IllegalArgumentException If the request cannot be created.
	 */
	public HttpRequest requestCreator(String path_to_file) throws IllegalArgumentException {
		var uri = URI.create(this.host + path_to_file);
		var request = HttpRequest.newBuilder(uri).build();
		return request;
	}
}
