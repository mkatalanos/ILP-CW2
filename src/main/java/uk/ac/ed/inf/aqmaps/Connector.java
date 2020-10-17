package uk.ac.ed.inf.aqmaps;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;
import com.mapbox.geojson.FeatureCollection;

public class Connector {

	private String host;
	private HttpClient client;

	Connector(String host, int port) {
		this.client = HttpClient.newHttpClient();
		this.host = String.format("http://%s:%d/", host, port);

	}

	public HttpRequest requestCreator(String path_to_file) throws IllegalArgumentException {
		var uri = URI.create(this.host + path_to_file);
		var request = HttpRequest.newBuilder(uri).build();
		return request;
	}

	public Sensor[] dateData(int year, int month, int day) throws Exception {
		var request = requestCreator(String.format("maps/%d/%02d/%02d/air-quality-data.json", year, month, day));

		var response = client.send(request, BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			var body = response.body();
			var sensors = new Gson().fromJson(body, Sensor[].class);
			return sensors;
		} else
			System.out.println("Sad");
		return null;

	}

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

	public String getHost() {
		return this.host;
	}
}
