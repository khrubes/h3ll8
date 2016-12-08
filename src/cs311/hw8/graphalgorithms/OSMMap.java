package cs311.hw8.graphalgorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cs311.hw8.graph.EdgeData;
import cs311.hw8.graph.Graph;
import cs311.hw8.graph.IGraph;
import cs311.hw8.graph.IGraph.Edge;
import cs311.hw8.graph.IGraph.Vertex;
import cs311.hw8.graph.VertexData;

public class OSMMap {

	private IGraph<VertexData, EdgeData> graph;

	// names for XML elements
	final String NODE_ELEMENT = "node";
	final String WAY_ELEMENT = "way";
	final String ND_ELEMENT = "nd";
	final String TAG_ELEMENT = "tag";

	// names for attributes within XML elements
	final String ID_ATTRIBUTE = "id";
	final String LAT_ATTRIBUTE = "lat";
	final String LONG_ATTRIBUTE = "lon";
	final String REF_ATTRIBUTE = "ref";
	final String K_ATTRIBUTE = "k";
	final String V_ATTRIBUTE = "v";

	// values of attributes
	final String NAME_ATTRIBUTE_VALUE = "name";
	final String HIGHWAY_ATTRIBUTE_VALUE = "highway";
	final String ONEWAY_ATTRIBUTE_VALUE = "oneway";

	public OSMMap() {
		graph = new Graph<>();
	}

	public OSMMap(String filename) {
		this.LoadMap(filename);
	}

	/**
	 * Adds vertices to graph from @param doc
	 */
	void addVerticesToMap(Document doc) {
		// Create vertices in graph from node elements
		NodeList nodeList = doc.getElementsByTagName(NODE_ELEMENT);

		int listLength = nodeList.getLength();
		for (int i = 0; i < listLength; i++) {
			Node node = nodeList.item(i);
			NamedNodeMap attributesMap = node.getAttributes();

			String nodeId = attributesMap.getNamedItem(ID_ATTRIBUTE).getNodeValue();
			
			double lat = Double.parseDouble(attributesMap.getNamedItem(LAT_ATTRIBUTE).getNodeValue());
			double lon = Double.parseDouble(attributesMap.getNamedItem(LONG_ATTRIBUTE).getNodeValue());

			graph.addVertex(nodeId, new VertexData(lat, lon));	
		}	
	}

	/**
	 * Adds edges to graph from @param doc
	 */
	void addEdgesToMap(Document doc) {
		
		// Create vertices in graph from node elements
		NodeList wayList = doc.getElementsByTagName(WAY_ELEMENT);

		int listLength = wayList.getLength();
		for (int wayIndex = 0; wayIndex < listLength; wayIndex++) {
			// both need to have a value for this way to be a valid street
			boolean hasHighwayAttribute = false;
			String streetName = null;

			boolean isOneway = false;

			// stores vertices to be added to the graph after verifying this is
			// a valid way
			List<Vertex<VertexData>> edgeVertices = new ArrayList<>();

			Node way = wayList.item(wayIndex);
			NodeList children = way.getChildNodes();

			// iterate over the children in this way processing the tags and nds
			int childrenLength = children.getLength();
			for (int childIndex = 0; childIndex < childrenLength; childIndex++) {
				Node child = children.item(childIndex);
				NamedNodeMap attributesMap = child.getAttributes();

				// If it's a <tag
				if (child.getNodeName().equals(TAG_ELEMENT)) {
					String attribute = attributesMap.getNamedItem(K_ATTRIBUTE).getNodeValue();
					if (attribute == null) {
						continue;
					}

					// Check if way has highway or name or value of oneway
					// attribute
					if (attribute.equals(HIGHWAY_ATTRIBUTE_VALUE)) {
						hasHighwayAttribute = true;
					} else if (attribute.equals(NAME_ATTRIBUTE_VALUE)) {
						streetName = attributesMap.getNamedItem(V_ATTRIBUTE).getNodeValue();
					} else if (attribute.equals(ONEWAY_ATTRIBUTE_VALUE)) {
						String oneWayValue = attributesMap.getNamedItem(V_ATTRIBUTE).getNodeValue();
						if (oneWayValue.equals("yes")) {
							isOneway = true;
						}
					}

					// If it's a <nd
				} else if (child.getNodeName().equals(ND_ELEMENT)) {
					String nodeId = attributesMap.getNamedItem(REF_ATTRIBUTE).getNodeValue();
					
					Vertex<VertexData> vertexRef = graph.getVertex(nodeId);

					if (nodeId == null || vertexRef == null) {
						// there is no vertex in the graph with this id
						continue;
					}

					edgeVertices.add(vertexRef);
				}

			} // end of iterating over children of way

			if (streetName != null && hasHighwayAttribute) {
				// this is valid street

				for (int i = 0; i < edgeVertices.size() - 1; i++) {
					Vertex<VertexData> v1 = edgeVertices.get(i);
					Vertex<VertexData> v2 = edgeVertices.get(i + 1);

										
					double distanceFromVertices = AlgorithmsHelper.distanceLatLon(v1.getVertexData().getLat(),
							v1.getVertexData().getLon(), v2.getVertexData().getLat(), v2.getVertexData().getLon());

					graph.addEdge(v1.getVertexName(), v2.getVertexName(), new EdgeData(distanceFromVertices, streetName));
					

					if (!isOneway) {
						// add edge in opposite direction
						graph.addEdge(v2.getVertexName(), v1.getVertexName(), new EdgeData(distanceFromVertices, streetName));
					}
					

				}
			}

		} // end of iterating over ways
	}

	/*
	 * Loads an XML OSM file into a graph as described above. If map data is
	 * already present from a previous load, that data is cleared and the new
	 * map is loaded.
	 */
	public void LoadMap(String filename) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filename));
			doc.normalize();

			graph = new Graph<>();
			graph.setDirectedGraph();

			addVerticesToMap(doc);
			addEdgesToMap(doc);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Outputs the approximate miles of roadway in the map. Since there are very
	 * few oneway roads compared to normal roads we will approximate this by
	 * dividing the total edge distance in the graph by two.
	 */
	double TotalDistance() {
		double distance = 0;
		List<Edge<EdgeData>> edges = graph.getEdges();
		for (Edge<EdgeData> edge : edges) {
			distance += edge.getEdgeData().getWeight();
		}

		return distance / 2;
	}

	public static class Location {
		private double lat;
		private double lon;

		public Location(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
		}

		// returns the latitude of the location
		public double getLatitude() {
			return this.lat;
		}

		// returns the longitude of the location
		public double getLongitude() {
			return this.lon;
		}
	}

	/**
	 * @return the vertex ID that is closest to the specified @param location
	 *         with an out degree greater than 0 (it has at least one edge out)
	 *         that is closest to the specified latitude and longitude.
	 */
	public String ClosestRoad(Location location) {

		// iterate over vertices storing min only if has

		String closestRoad = "";
		double minDistance = Double.MAX_VALUE;

		List<Vertex<VertexData>> vertices = graph.getVertices();
		for (Vertex<VertexData> vertex : vertices) {
			double distanceFromVertexToLocation = AlgorithmsHelper.distanceLatLon(vertex.getVertexData().getLat(),
					vertex.getVertexData().getLon(), location.getLatitude(), location.lon);
			if (distanceFromVertexToLocation < minDistance && graph.getNeighbors(vertex.getVertexName()).size() > 0) {
				minDistance = distanceFromVertexToLocation;
				closestRoad = vertex.getVertexName();
			}
		}

		return closestRoad;
	}

	/**
	 * @return a list of String types that is the sequence of vertex ID names
	 *         that gives the path.
	 */
	public List<String> ShortestRoute(Location fromLocation, Location toLocation) {
		List<String> shortestRoute = new ArrayList<>();
		List<Edge<EdgeData>> path = GraphAlgorithms.ShortestPath(graph, ClosestRoad(fromLocation),
				ClosestRoad(toLocation));

		// Add the index 0th vertex1 in the path, then loop from 1-end adding
		// vertex 2
		shortestRoute.add(path.get(0).getVertexName1());
		for (int i = 1; i < path.size(); i++) {
			shortestRoute.add(path.get(1).getVertexName2());
		}
		return shortestRoute;
	}

	/**
	 * @return returns a List<String> of street names from the beginning
	 *         location to the end location. Does not repeat street names that
	 *         are consecutively the same in the list
	 */
	public List<String> StreetRoute(List<String> vertexIDNames) {
		HashSet<String> usedStreetNames = new HashSet<>(); // used to prevent duplicate street names
		List<String> streetRoute = new ArrayList<>();

		for (int i = 0; i < vertexIDNames.size() - 1; i++) {
			//get the path from one location to the other
			List<Edge<EdgeData>> path = GraphAlgorithms.ShortestPath(this.graph, vertexIDNames.get(i),
					vertexIDNames.get(i + 1));
			
			//add the streets on this path to street route
			for (Edge<EdgeData> street : path) {
				String streetName = street.getEdgeData().getName();

				if (!usedStreetNames.contains(streetName)) {
					streetRoute.add(streetName);
					usedStreetNames.add(streetName);
				}
			}
		}

		return streetRoute;
	}

	public static void main2(String[] args) {
		OSMMap map = new OSMMap();
		map.LoadMap(args[1]);
		System.out.println("Total Distance= " + map.TotalDistance());
	}

	public static void main3(String[] args) {
		try {
			OSMMap map = new OSMMap();
			map.LoadMap(args[0]);

			File route = new File(args[1]);
			Scanner scan = new Scanner(route);

			List<String> locationVertexIDs = new ArrayList<>();

			while (scan.hasNextLine()) {
				String coordinatesLine = scan.nextLine();
				Location location = map.getLocationFromTextLine(coordinatesLine);
				locationVertexIDs.add(map.ClosestRoad(location));
			}

			List<String> streetRoute = map.StreetRoute(locationVertexIDs);
			for (String street : streetRoute) {
				System.out.println(street);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return a Location defined by @param coordinatesLine
	 */
	private Location getLocationFromTextLine(String coordinatesLine) {
		String[] lat_long = coordinatesLine.split(" ");
		return new Location(Double.parseDouble(lat_long[0]), Double.parseDouble(lat_long[1]));
	}

	public Graph getGraph() {
		return (Graph) this.graph;
	}

}