package cs311.hw8.graphalgorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import cs311.hw8.graph.EdgeData;
import cs311.hw8.graph.Graph;
import cs311.hw8.graph.IGraph;
import cs311.hw8.graph.IGraph.Edge;
import cs311.hw8.graph.IGraph.NoSuchEdgeException;
import cs311.hw8.graph.IGraph.NoSuchVertexException;
import cs311.hw8.graph.IGraph.Vertex;

public class GraphAlgorithms {

	public int distCompare() {
		return 0;
	}

	/**
	 * Determines the shortest path between to vertices @param vertexStart
	 * and @param vertexEnd in the graph @param g
	 * 
	 * @return a List<Edge<E>> representing the path from start to end, where
	 *         list[0] is some edge from (vertexStart, v) or null if a path does
	 *         not exist
	 */
	public static <V, E extends IWeight> List<Edge<E>> ShortestPath(IGraph<V, E> g, String vertexStart,
			String vertexEnd) {
		
		LinkedList<Edge<E>> shortestPath = new LinkedList<>();
		try {
			
			//edge cases 
			if (g == null || (!g.isDirectedGraph()) || vertexStart == null || vertexEnd == null) {
				return null;	
			}
					
			if(vertexStart.equals(vertexEnd)) {
				Edge<E> possibleSelfLoopEdge = g.getEdge(vertexStart, vertexEnd);
				//if this edge doesn't exist, the NoSuchEdgeException will be caught in the try/catch
				shortestPath.add(possibleSelfLoopEdge);
				return shortestPath;
			}
				
			// Get all vertices in the graph
			List<Vertex<V>> vertices = g.getVertices();

			// Maps a vertex to it's current distance
			HashMap<Vertex<V>, Double> vertexToDistanceMap = new HashMap<>();

			// Maps a vertex to it's current predecessor, the vertex it is reached from on some shortest path
			HashMap<Vertex<V>, Vertex<V>> vertexToPredecessorMap = new HashMap<>();

			// Initialize the distance of all vertices to Double.MAX_VALUE and all
			// predecessors to null.
			for (Vertex<V> vertex : vertices) {
				vertexToDistanceMap.put(vertex, Double.MAX_VALUE);
				vertexToPredecessorMap.put(vertex, null);
			}

			// PriorityQueue representing vertices in the "open" set, or vertices
			// that are not completely evaluated yet
			// Ordered such that vertices with the smallest distance are at the
			// top
			PriorityQueue<Vertex<V>> openSetPriortyQueue = new PriorityQueue<>(new Comparator<Vertex<V>>() {
				@Override
				public int compare(Vertex<V> o1, Vertex<V> o2) {
					return Double.compare(vertexToDistanceMap.get(o1), vertexToDistanceMap.get(o2));
				}
			});

			// HashSet representing vertices in the "closed" set, or vertices
			// that
			// are already evaluated
			HashSet<Vertex<V>> closedSet = new HashSet<>();

			Vertex<V> startVertex = g.getVertex(vertexStart);
			
			// Set the distance from startVertex to itself to 0
			vertexToDistanceMap.put(startVertex, (double) 0);
			openSetPriortyQueue.add(startVertex);

			// Process all verticies until dist and predecessor hashmaps are fully populated per Dijkstra's
			while (!openSetPriortyQueue.isEmpty()) {

				Vertex<V> currentVertex = openSetPriortyQueue.poll();
				closedSet.add(currentVertex);
				

				List<Vertex<V>> neighbors = g.getNeighbors(currentVertex.getVertexName());

				// Iterate over neighbors to possibly update paths to them through currentVertex
				for (Vertex<V> neighbor : neighbors) {

					// If this neighbor is still up for processing
					if (!closedSet.contains(neighbor)) {
						double distanceFromCurrentVertexToNeighbor = ((EdgeData) g.getEdgeData(currentVertex.getVertexName(), neighbor.getVertexName())).getWeight();
						double alternateDistanceThroughCurrentVertex = vertexToDistanceMap.get(currentVertex)
								+ distanceFromCurrentVertexToNeighbor;

						// If the alternate distance to neighbor through the current vertex is less than a previously calculated path to the neighbor
						if(alternateDistanceThroughCurrentVertex < vertexToDistanceMap.get(neighbor)) {
							vertexToDistanceMap.put(neighbor, alternateDistanceThroughCurrentVertex);
							vertexToPredecessorMap.put(neighbor, currentVertex);
							
							//openSetPriortyQueue.add(neighbor); ???
						}
						openSetPriortyQueue.add(neighbor);
					}

				}

			} // while (!openSetPriortyQueue.isEmpty())
		
			Vertex<V> currentVertex = g.getVertex(vertexEnd);
			Vertex<V> predecessorVertex = vertexToPredecessorMap.get(currentVertex);
			
			if (predecessorVertex == null) {
				// If there is no predecessor to vertexEnd, it was never found during
				// Dijstrkas starting at startVertex and therefore no path exists
				return null;
			}	
			
			//Add edges from predecessorVertex -> currentVertex to shortestPath, backtracking until current vertex is the start vertex
			while(currentVertex !=  startVertex) {
				Edge<E> edgeFromPredecessorToCurrent = g.getEdge(predecessorVertex.getVertexName(), currentVertex.getVertexName());
				shortestPath.add(0, edgeFromPredecessorToCurrent);
				currentVertex = predecessorVertex;
				predecessorVertex = vertexToPredecessorMap.get(predecessorVertex);
			}
			
			
		} catch (NoSuchVertexException e1) {			
			e1.printStackTrace();
			return null;
		} catch (NoSuchEdgeException e1) {		
			e1.printStackTrace();
			return null;
		}

		return shortestPath;
	}

	public static <V, E> List<Vertex<V>> TopologicalSort(IGraph<V, E> g) {
		List<Vertex<V>> result = new LinkedList<Vertex<V>>();

		if (!g.isDirectedGraph()) {
			System.out.println("Cant run topological sort on an undirected graph");
			return result;
		}
		AlgorithmsHelper algorithmsHelper = new AlgorithmsHelper();

		// Check for a cycle before attempting topological sort
		if (algorithmsHelper.hasCycle(g)) {
			System.out.println("Cant run topological sort on a graph with a cycle");
			return result;
		}

		// Get starting vertices with zero indegree
		Queue<Vertex<V>> startingVerticesQueue = new LinkedList<Vertex<V>>();
		startingVerticesQueue.addAll(algorithmsHelper.getVertex0Indegrees(g));

		// Get an indegree count for all vertices
		HashMap<Vertex<V>, Integer> indegreesOfGraph = algorithmsHelper.getVertexIndegreesOfGraph(g);

		while (!startingVerticesQueue.isEmpty()) {
			Vertex<V> currentVertex = startingVerticesQueue.poll();
			indegreesOfGraph.remove(currentVertex);

			// update the indegree count after "removing" currentVertex
			algorithmsHelper.decrementIndegreeFromVertex(g, currentVertex, indegreesOfGraph);

			// enqueue vertices that now have 0 indegree after "removing"
			// currentVertex
			startingVerticesQueue.addAll(algorithmsHelper.getVertex0Indegrees(indegreesOfGraph));

			// add currentVertex to our list of topologically sorted vertices
			result.add(currentVertex);
		}

		return result;
	}

	public static <V, E> List<List<Vertex<V>>> AllTopologicalSort(IGraph<V, E> g) {
		List<List<Vertex<V>>> allTopologicalSorts = new LinkedList<List<Vertex<V>>>();

		if (!g.isDirectedGraph()) {
			System.out.println("Cant run topological sort on an undirected graph");
			return allTopologicalSorts;
		}
		AlgorithmsHelper algorithmsHelper = new AlgorithmsHelper();

		// Check for a cycle before attempting topological sort
		if (algorithmsHelper.hasCycle(g)) {
			System.out.println("Cant run topological sort on a graph with a cycle");
			return allTopologicalSorts;
		}

		// Get 0 indegree count
		HashSet<Vertex<V>> zeroIndegreesOfGraph = algorithmsHelper.getVertex0Indegrees(g);

		// Get an indegree count for all vertices
		HashMap<Vertex<V>, Integer> indegreesOfGraph = algorithmsHelper.getVertexIndegreesOfGraph(g);

		for (Vertex<V> zeroIndegreeVertex : zeroIndegreesOfGraph) {
			ArrayList<Vertex<V>> vertexList = new ArrayList<Vertex<V>>();
			vertexList.add(zeroIndegreeVertex);

			HashMap<Vertex<V>, Integer> indegreesOfGraphCopy = new HashMap<>(indegreesOfGraph);
			indegreesOfGraphCopy.remove(zeroIndegreeVertex);
			algorithmsHelper.allTopologicalSortRec(zeroIndegreeVertex, g, indegreesOfGraphCopy, allTopologicalSorts,
					vertexList);
		}

		return allTopologicalSorts;
	}

	// assuming graph is made of connected components
	public static <V, E extends IWeight> IGraph<V, E> Kruscal(IGraph<V, E> g) {

		// create priority queue where the root is the edge of minimum weight
		PriorityQueue<Edge<E>> pq = new PriorityQueue<>(new Comparator<Edge<E>>() {
			@Override
			public int compare(Edge<E> o1, Edge<E> o2) {
				if (o1.getEdgeData().getWeight() == o2.getEdgeData().getWeight())
					return 0;
				else if (o1.getEdgeData().getWeight() > o2.getEdgeData().getWeight())
					return 1;
				else
					return -1;
			}
		});

		IGraph<V, E> mst = new Graph<V, E>();

		List<Vertex<V>> vertices = g.getVertices();

		// Initialize every vertex starts as its own component
		HashMap<String, Integer> components = new HashMap<String, Integer>();
		int componentNumber = 0;

		// Add each vertex to it's own component
		for (Vertex<V> vertex : vertices) {
			components.put(vertex.getVertexName(), componentNumber);
			mst.addVertex(vertex.getVertexName(), vertex.getVertexData());
			componentNumber += 1;
		}

		List<Edge<E>> edges = g.getEdges();

		// add all edges to the priority queue, where the minimum edge will be
		// the root.
		for (Edge<E> edge : edges) {
			pq.add(edge);
		}

		// While the tree is not totally spanning
		while (!pq.isEmpty()) {
			// Get the edge of minimum weight
			Edge<E> minEdge = pq.remove();
			if (!components.get(minEdge.getVertexName1()).equals(components.get(minEdge.getVertexName2()))) {
				// the edges connecting the minimum edge are in separate
				// components and should be combined to the same component.
				AlgorithmsHelper.connectComponents(components, minEdge.getVertexName1(), minEdge.getVertexName2());
				// Add edge
				mst.addEdge(minEdge.getVertexName1(), minEdge.getVertexName2(), minEdge.getEdgeData());
			}
		}
		// all edges have been processed
		return mst;
	}

}