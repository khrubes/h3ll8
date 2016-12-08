package cs311.hw8.graph;

public class VertexData {

	private double lat;
	private double lon;

	// VertexData for the OSMMap, stores the lat and lon
	public VertexData(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

}
