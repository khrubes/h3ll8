package cs311.hw8.graphalgorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cs311.hw8.graph.IGraph;
import cs311.hw8.graph.IGraph.Vertex;

public class AlgorithmsHelper {

	// Latitude / Longitude distance calculator from http://www.geodatasource.com/developers/java
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::                                                                         :*/
	/*::  This routine calculates the distance between two points (given the     :*/
	/*::  latitude/longitude of those points). It is being used to calculate     :*/
	/*::  the distance between two locations using GeoDataSource (TM) prodducts  :*/
	/*::                                                                         :*/
	/*::  Definitions:                                                           :*/
	/*::    South latitudes are negative, east longitudes are positive           :*/
	/*::                                                                         :*/
	/*::  Passed to function:                                                    :*/
	/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
	/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	
	public static double distanceLatLon(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		return (dist);
	}
	
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
	
	/**
	 * @return a HashMap<Vertex<V>, Integer> of the graph @param g that has the
	 *         indegrees of all vertices calculated.
	 */
	public <V, E> HashMap<Vertex<V>, Integer> getVertexIndegreesOfGraph(IGraph<V, E> g) {

		// Maps a Vertex to the number of edges coming in to it
		HashMap<Vertex<V>, Integer> indegreeCounter = new HashMap<>();

		List<Vertex<V>> vertices = g.getVertices();

		// initialize indegreeCounter to 0 for all vertices
		for (Vertex<V> vertex : vertices) {
			indegreeCounter.put(vertex, 0);
		}

		// iterate over vertices and update the indegree of the neighbors
		for (Vertex<V> vertex : vertices) {

			List<Vertex<V>> neighbors = g.getNeighbors(vertex.getVertexName());

			// iterate over the neighbors of vertex and increment the indegree of them by 1
			// to account for the edge from vertex -> neighbor
			for (Vertex<V> neighbor : neighbors) {
				Integer indegreeOfNeighBor = indegreeCounter.get(neighbor);
				indegreeCounter.put(neighbor, indegreeOfNeighBor.intValue() + 1);
			}
		}
		return indegreeCounter;
	}

	/**
	 * @return a Vertex<V> in the graph @param g that has 0 indegree
	 */
	public <V, E> HashSet<Vertex<V>> getVertex0Indegrees(IGraph<V, E> g) {
		HashMap<Vertex<V>, Integer> allIndegrees = this.getVertexIndegreesOfGraph(g);
		return getVertex0Indegrees(allIndegrees);
	}

	/**
	 * @return a Vertex<V> in the indegreeSet @param indegreesSet that has 0
	 *         indegree
	 */
	public <V, E> HashSet<Vertex<V>> getVertex0Indegrees(HashMap<Vertex<V>, Integer> indegreesSet) {

		// intialize a test variable for 0
		Integer zero = new Integer(0);

		HashSet<Vertex<V>> zeroIndegreeSet = new HashSet<>();

		// iterate over the values in the result of all computed indegrees and
		// add them to zeroIndegreeSet
		for (Map.Entry<Vertex<V>, Integer> pair : indegreesSet.entrySet()) {
			if (pair.getValue().equals(zero)) {
				zeroIndegreeSet.add(pair.getKey());
			}
		}
		return zeroIndegreeSet;
	}

	/**
	 * Remove the count of outgoing edges from the @param vertex in @param
	 * indegreesOfGraph
	 */
	public static <V, E> void decrementIndegreeFromVertex(IGraph<V, E> g, Vertex<V> vertex,
			HashMap<Vertex<V>, Integer> indegreesOfGraph) {
		List<Vertex<V>> neighbors = g.getNeighbors(vertex.getVertexName());

		// iterate over neighbors of vertex
		for (Vertex<V> neighbor : neighbors) {
			if (indegreesOfGraph.containsKey(neighbor)) {
				Integer indegree = indegreesOfGraph.get(neighbor);

				// Decrement the indegree count of this neighbor to account for
				// "removing" vertex
				indegree = new Integer(indegree.intValue() - 1);

				indegreesOfGraph.put(neighbor, indegree);
			}
		}
	}

	/**
	 * Connects the component @param vertexName1 is in with the
	 * compontent @param vertexName2 is in to create one component Used for
	 * Kruskals
	 */
	public static void connectComponents(HashMap<String, Integer> components, String vertexName1, String vertexName2) {

		int vertex1ComponentNumber = components.get(vertexName1);
		int vertex2ComponentNumber = components.get(vertexName2);

		for (String vertexName : components.keySet()) {
			if (components.get(vertexName).equals(vertex1ComponentNumber)) {
				components.put(vertexName, vertex2ComponentNumber);
			}
		}
	}

	/**
	 * @return True if g has a cycle
	 */
	public <V, E> boolean hasCycle(IGraph<V, E> graph) {
		HashSet<Vertex<V>> visitedVertices = new HashSet<Vertex<V>>();

		HashSet<Vertex<V>> zeroInDegreeVertices = this.getVertex0Indegrees(graph);

		for (Vertex<V> vertex : zeroInDegreeVertices) {
			if (hasCycleRec(graph, visitedVertices, vertex)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Recursive helper for hasCycle
	 */
	private <V, E> boolean hasCycleRec(IGraph<V, E> graph, HashSet<Vertex<V>> visitedVertices,
			Vertex<V> currentVertex) {
		// make a new HashSet from the old to prevent modifying the set passed
		// back up the recursion tree
		HashSet<Vertex<V>> visitedCopy = new HashSet<Vertex<V>>(visitedVertices);

		if (visitedCopy.contains(currentVertex)) {
			// Found a back edge => cycle in this graph
			return true;
		} else {
			visitedCopy.add(currentVertex);
		}

		List<Vertex<V>> neighbors = graph.getNeighbors(currentVertex.getVertexName());

		// Check for a cycle in all of the neighbors of currenVer
		for (Vertex<V> neighbor : neighbors) {
			if (hasCycleRec(graph, visitedCopy, neighbor)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Recursive method used by AllTopologicalSort in GraphAlgorithms
	 */
	public <V, E> void allTopologicalSortRec(Vertex<V> zeroIndegreeVertex, IGraph<V, E> g,
			HashMap<Vertex<V>, Integer> indegreesOfGraph, List<List<Vertex<V>>> allTopologicalSorts,
			ArrayList<Vertex<V>> vertexList) {

		// decrememt indegrees of neighbors of zeroIndegreeVertex
		decrementIndegreeFromVertex(g, zeroIndegreeVertex, indegreesOfGraph);

		// get the new set of zeroIndegree vertices after decrementing
		HashSet<Vertex<V>> updatedZeroIndegreeVertices = this.getVertex0Indegrees(indegreesOfGraph);

		// base case, return
		if (updatedZeroIndegreeVertices.size() == 0) {
			allTopologicalSorts.add(vertexList);
			return;
		}

		for (Vertex<V> vertex : updatedZeroIndegreeVertices) {
			ArrayList<Vertex<V>> vertexListCopy = new ArrayList<Vertex<V>>(vertexList);
			vertexListCopy.add(vertex);
			HashMap<Vertex<V>, Integer> indegreesOfGraphCopy = new HashMap<>(indegreesOfGraph);
			indegreesOfGraphCopy.remove(vertex);
			this.allTopologicalSortRec(vertex, g, indegreesOfGraphCopy, allTopologicalSorts, vertexListCopy);
		}
	}

}
