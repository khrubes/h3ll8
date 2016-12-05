package cs311.hw8.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Graph<V, E> implements IGraph<V, E> {
	//Maps the string "A" to the corresponding Vertex with a name of  "A"
	private HashMap<String, Vertex<V>> nameToVertexMap;
	
	//Maps a Vertex to a map with String keys representing the name of it's neighbors, 
	//and EdgeWrapper values containing the edge object and boolean indicating which edge was added first.
	private HashMap<Vertex<V>, HashMap<String, EdgeWrapper<E>>> vertexToEdgesMap;
	
	private boolean isDirected;

	public Graph() {
		this.nameToVertexMap = new HashMap<>();
		this.vertexToEdgesMap = new HashMap<>();
		this.isDirected = false;
	}

	/**
	 * Set the graph to be a directed graph. Edge (x, y) is different than edge
	 * (y, x)
	 */
	@Override
	public void setDirectedGraph() {
		//iterate over vertices
		List<Vertex<V>> vertices = this.getVertices();
		for (Vertex<V> vertex : vertices) {

			//Get the neighbors of this vertex from vertexToEdgesMap
			HashMap<String, EdgeWrapper<E>> neighbors = vertexToEdgesMap.get(vertex);

			//iterate over neighbors of this vertex
			for (Map.Entry<String, EdgeWrapper<E>> pair : neighbors.entrySet()) {
				
				String neighborName = pair.getKey();
				//edgeWrapper contains the Edge object from vertex -> neighbor
				EdgeWrapper<E> edgeWrapper = pair.getValue();

				//Check if the edge from vertex -> neighbor was added first 
				//Remove the edge added last. For example if (A,B), (B,A) are edges in the undirected graph, and (A,B) was added first, (B,A) will be removed
				if (edgeWrapper.wasSetFirst()) {
					// remove the edge from vertex associated with neighborName -> vertex
					Vertex<V> neighborVertex = getVertex(neighborName);
					HashMap<String, EdgeWrapper<E>> neighborsOfNeighborVertex = vertexToEdgesMap.get(neighborVertex);
					
					removeEdge(neighborsOfNeighborVertex, vertex.getVertexName());

				} else {
					// remove the edge from vertex -> neighborName
					removeEdge(neighbors, neighborName);
				}
			}
		}

		this.isDirected = true;
	}
	
	/**
	 * Helper method that removes @param toRemove, a string representing the Vertex name, 
	 * from @param mapToRemoveFrom, a vertexToEdgesMap that belongs to some Vertex of which toRemove is a neighbor.
	 */
	private void removeEdge(HashMap<String, EdgeWrapper<E>> mapToRemoveFrom, String toRemove) {
		mapToRemoveFrom.remove(toRemove);
	}

	/**
	 * Set the graph to be an undirected graph. Edge (x, y) is in the graph if
	 * and only if edge (y, x) is in the graph. Note that when implementing this
	 * and there are already edges defined in the graph, care must be taken to
	 * resolve conflicts and inconsistencies in the overall implementation.
	 */
	@Override
	public void setUndirectedGraph() {
		//iterate over vertices
		List<Vertex<V>> vertices = this.getVertices();
		for (Vertex<V> vertex : vertices) {

			//Get the neighbors of this vertex from vertexToEdgesMap
			HashMap<String, EdgeWrapper<E>> neighbors = vertexToEdgesMap.get(vertex);

			//iterate over neighbors of this vertex
			for (Map.Entry<String, EdgeWrapper<E>> pair : neighbors.entrySet()) {
				
				//edgeWrapper contains the Edge object from vertex -> neighbor
				String neighborName = pair.getKey();
				EdgeWrapper<E> edgeWrapper = pair.getValue();

				Edge<E> reverseEdge;
				try {
					//because of unhandled exception, see description for getEdge
					reverseEdge = getEdge(neighborName, vertex.getVertexName());
				} catch (cs311.hw8.graph.IGraph.NoSuchVertexException e) {
					e.printStackTrace();
					return;
				} catch (cs311.hw8.graph.IGraph.NoSuchEdgeException e) {
					e.printStackTrace();
					return;
				}
				if (reverseEdge != null) {
					// there is already an edge from neighbor -> vertex, we don't need to add one in reverse
					continue;
				}			
				
				//add an edge in the reverse direction, from neighbor -> vertex with the same data
				addEdge(neighborName, vertex.getVertexName(), edgeWrapper.getEdge().getEdgeData());

			}

		}

		this.isDirected = false;
	}


	/**
	 * 
	 * @return true if the graph is directed.
	 */
	@Override
	public boolean isDirectedGraph() {
		return this.isDirected;
	}

	/**
	 * Adds a vertex to the graph with name given by the vertexName.
	 * vertexNames, must be unique in the graph.
	 * 
	 * @param vertexName
	 *            The unique name of the vertex.
	 * 
	 * @throws cs311.hw6.graph.IGraph.DuplicateVertexException
	 */
	@Override
	public void addVertex(String vertexName) throws cs311.hw8.graph.IGraph.DuplicateVertexException {
		addVertex(vertexName, null);
	}

	/**
	 * Adds a vertex to the graph with name given by the vertexName.
	 * vertexNames, must be unique in the graph. The vertexData of generic type
	 * is associated with this vertex.
	 * 
	 * @param vertexName
	 * @param vertexData
	 * @throws cs311.hw6.graph.IGraph.DuplicateVertexException
	 */
	@Override
	public void addVertex(String vertexName, V vertexData) throws cs311.hw8.graph.IGraph.DuplicateVertexException {
		if (nameToVertexMap.containsKey(vertexName)) {
			throw new DuplicateVertexException();
		}

		Vertex<V> toAdd = new Vertex<V>(vertexName, vertexData);
		//associate the string vertexName with it's Vertex object
		nameToVertexMap.put(vertexName, toAdd);
		
		//associate the new Vertex with a map of edges
		vertexToEdgesMap.put(toAdd, new HashMap<String, EdgeWrapper<E>>());
	}

	/**
	 * Adds an edge to the graph by specifying the two vertices that comprise
	 * the edge. If the graph is undirected then edge (x, y) or edge (y, x) may
	 * be used to add the single edge. If the graph is undirected and edge (x,y)
	 * is added followed by a subsequent edge (y, x), the later add would throw
	 * a DuplicateEdgeException.
	 * 
	 * @param vertex1
	 *            The first vertex in the edge.
	 * @param vertex2
	 *            The second vertex in the edge.
	 * 
	 * @throws cs311.hw6.graph.IGraph.DuplicateEdgeException
	 * @throws cs311.hw6.graph.IGraph.NoSuchVertexException
	 */
	@Override
	public void addEdge(String vertex1, String vertex2)
			throws cs311.hw8.graph.IGraph.DuplicateEdgeException, cs311.hw8.graph.IGraph.NoSuchVertexException {
		addEdge(vertex1, vertex2, null);
	}

	/**
	 * Adds an edge to the graph by specifying the two vertices that comprise
	 * the edge. If the graph is undirected then edge (x, y) or edge (y, x) may
	 * be used to add the single edge. If the graph is undirected and edge (x,y)
	 * is added followed by a subsequent edge (y, x), the later add would throw
	 * a DuplicateEdgeException. The edgeDaa parameter is used to associate
	 * generic edge data with the edge.
	 * 
	 * @param vertex1
	 *            The first vertex in the edge.
	 * @param vertex2
	 *            The second vertex in the edge.
	 * @param edgeData
	 *            Thegeneric edge data.
	 * 
	 * @throws cs311.hw6.graph.IGraph.DuplicateEdgeException
	 * @throws cs311.hw6.graph.IGraph.NoSuchVertexException
	 */
	@Override
	public void addEdge(String vertex1, String vertex2, E edgeData)
			throws cs311.hw8.graph.IGraph.DuplicateEdgeException, cs311.hw8.graph.IGraph.NoSuchVertexException {

		if (!nameToVertexMap.containsKey(vertex1) || !nameToVertexMap.containsKey(vertex2)) {
			// vertex 1 or vertex 2 does not exist in graph
			throw new NoSuchVertexException();
		}

		addEdgeHelper(vertex1, vertex2, edgeData, true);

		if (!this.isDirected) {
			// add the other edge from vertex2 -> vertex1
			addEdgeHelper(vertex2, vertex1, edgeData, false);
		}

	}

	/**
	 * Performs the actual edge addition. This logic is abstracted out so two
	 * subsequent calls to addEdgeHelper can be made in addEdge, without a
	 * DuplicateEdgeException.
	 * 
	 * @param wasSetFirst
	 *            a boolean value to be added to the {@link EdgeWrapper} object
	 *            to be mapped to this edge. The logical use of wasSetFirst is
	 *            detailed in {@link EdgeWrapper}
	 */
	private void addEdgeHelper(String vertex1, String vertex2, E edgeData, boolean wasSetFirst)
			throws cs311.hw8.graph.IGraph.DuplicateEdgeException {
		Vertex<V> vertexKey = nameToVertexMap.get(vertex1);
		HashMap<String, EdgeWrapper<E>> edgesOfVertexKeyMap = vertexToEdgesMap.get(vertexKey);

		if (edgesOfVertexKeyMap.containsKey(vertex2)) {
			// there is already an edge from vertex1 to vertex2
			throw new DuplicateEdgeException();
		}

		Edge<E> edgeToAdd = new Edge<E>(vertex1, vertex2, edgeData);
		EdgeWrapper<E> edgeWrapper = new EdgeWrapper<>(edgeToAdd, wasSetFirst);
		edgesOfVertexKeyMap.put(vertex2, edgeWrapper);
	}

	/**
	 * Returns the generic vertex data associated with the specified vertex. If
	 * no vertex data is associated with the vertex, then null is returned.
	 * 
	 * @param vertexName
	 *            Name of vertex to get data for
	 * 
	 * @return The generic vertex data
	 * 
	 * @throws cs311.hw6.graph.IGraph.NoSuchVertexException
	 */
	@Override
	public V getVertexData(String vertexName) throws cs311.hw8.graph.IGraph.NoSuchVertexException {
		if (!nameToVertexMap.containsKey(vertexName)) {
			// no vertex exists in the graph with this name
			throw new NoSuchVertexException();
		}
		Vertex v = nameToVertexMap.get(vertexName);
		return (V) v.getVertexData();
	}

	/**
	 * Sets the generic vertex data of the specified vertex.
	 * 
	 * @param vertexName
	 *            The name of the vertex.
	 * 
	 * @param vertexData
	 *            The generic vertex data.
	 * 
	 * @throws cs311.hw6.graph.IGraph.NoSuchVertexException
	 */
	@Override
	public void setVertexData(String vertexName, V vertexData) throws cs311.hw8.graph.IGraph.NoSuchVertexException {
		if (!nameToVertexMap.containsKey(vertexName)) {
			// no vertex exists in the graph with this name
			throw new NoSuchVertexException();
		}
		Vertex<V> oldVertexWithVertexName = nameToVertexMap.get(vertexName);
		
		Vertex<V> toReplaceV = new Vertex<V>(vertexName, vertexData);
		
		//because there's no public setter, we replace the vertex object		
		nameToVertexMap.put(vertexName, toReplaceV);
		
		HashMap<String, EdgeWrapper<E>> valueToAddBackToHashMap = vertexToEdgesMap.get(oldVertexWithVertexName);
		
		//remove old value in vertexToEdgesMap
		vertexToEdgesMap.remove(vertexName);
		
		//add new value in vertexToEdgesMap
		vertexToEdgesMap.put(toReplaceV, valueToAddBackToHashMap);
	}

	/**
	 * Returns the generic edge data associated with the specified edge. If no
	 * edge data is associated with the edge, then null is returned.
	 * 
	 * @param vertex1
	 *            Vertex one of the edge.
	 * @param vertex2
	 *            Vertex two of the edge.
	 * 
	 * @return The generic edge data.
	 * 
	 * @throws cs311.hw6.graph.IGraph.NoSuchVertexException
	 * @throws cs311.hw6.graph.IGraph.NoSuchEdgeException
	 */
	@Override
	public E getEdgeData(String vertex1, String vertex2)
			throws cs311.hw8.graph.IGraph.NoSuchVertexException, cs311.hw8.graph.IGraph.NoSuchEdgeException {
		Edge edge = getEdge(vertex1, vertex2);
		return (E) edge.getEdgeData();
	}

	/**
	 * Sets the generic edge data of the specified edge.
	 * 
	 * @param vertex1
	 *            Vertex one of the edge.
	 * @param vertex2
	 *            Vertex two of the edge.
	 * 
	 * @param edgeData
	 *            The generic edge data.
	 * 
	 * @throws cs311.hw6.graph.IGraph.NoSuchVertexException
	 * @throws cs311.hw6.graph.IGraph.NoSuchEdgeException
	 */
	@Override
	public void setEdgeData(String vertex1, String vertex2, E edgeData)
			throws cs311.hw8.graph.IGraph.NoSuchVertexException, cs311.hw8.graph.IGraph.NoSuchEdgeException {
		//if this edge does not exist or one of the vertices doesn't it will throw the exception
		Edge<E> edgeToSet = this.getEdge(vertex1, vertex2);
		
		Vertex<V> vertex1AsVertex = nameToVertexMap.get(vertex1);
		HashMap<String, EdgeWrapper<E>> edgesOfV1 = vertexToEdgesMap.get(vertex1AsVertex);
		EdgeWrapper<E> edgeWrapperForEdge1to2 = edgesOfV1.get(vertex2);
		
		Edge<E> newEdgeToReplaceWithOld = new Edge<E>(vertex1, vertex2, edgeData);
		
		edgeWrapperForEdge1to2.setEdge(newEdgeToReplaceWithOld);
	}

	/**
	 * Returns an encapsulated Vertex data type based on the vertex name
	 * 
	 * @param VertexName
	 *            The name of the vertex.
	 * 
	 * @return The encapsulated vertex.
	 */
	@Override
	public cs311.hw8.graph.IGraph.Vertex<V> getVertex(String VertexName) {
		return nameToVertexMap.get(VertexName);
	}

	/**
	 * Returns an encapsulated Edge data type based on the specified edge.
	 * 
	 * @param vertexName1
	 *            Vertex one of edge.
	 * @param vertexName2
	 *            Vertex two of edge.
	 * 
	 * @return Encapsulated edge.
	 * 
	 * 
	 * 
	 * 
	 * Jim told me, in person, I can throw these exceptions here.
	 */
	@Override
	public cs311.hw8.graph.IGraph.Edge<E> getEdge(String vertexName1, String vertexName2) throws NoSuchVertexException, NoSuchEdgeException{
		if (!nameToVertexMap.containsKey(vertexName1) || !nameToVertexMap.containsKey(vertexName2)) {
			// vertex1 or 2 is not in the graph, so it isn't possible for an
			// edge to exist between them.
			throw new NoSuchVertexException();
		}

		Vertex keyVertex = nameToVertexMap.get(vertexName1);
		HashMap<String, EdgeWrapper<E>> edgesOfKeyVertex = vertexToEdgesMap.get(keyVertex);

		if (!edgesOfKeyVertex.containsKey(vertexName2)) {
			// there is no edge from vertex1 -> vertex 2
			throw new NoSuchEdgeException();
		}

		EdgeWrapper<E> edgeWrapper = edgesOfKeyVertex.get(vertexName2);
		return edgeWrapper.getEdge();
	}

	/**
	 * Returns a list of all the vertices in the graph.
	 * 
	 * @return The List<Vertex> of vertices.
	 */
	@Override
	public List<cs311.hw8.graph.IGraph.Vertex<V>> getVertices() {
		Collection<cs311.hw8.graph.IGraph.Vertex<V>> collectionOfVertices = nameToVertexMap.values();
		return new ArrayList<>(collectionOfVertices);
	}

	/**
	 * Returns all the edges in the graph.
	 * 
	 * @return The List<Edge<E>> of edges.
	 */
	@Override
	public List<cs311.hw8.graph.IGraph.Edge<E>> getEdges() {
		List<Edge<E>> edges = new ArrayList<>();

		// create a collection of the vertices in the graph.
		Collection<Vertex<V>> vertices = nameToVertexMap.values();
		Iterator<Vertex<V>> vertexIter = vertices.iterator();

		// iterate over vertices
		while (vertexIter.hasNext()) {

			// pick a vertex
			Vertex<V> vertex = vertexIter.next();

			// get the edges associated with it
			Collection edgeWrappersOfVertex = (vertexToEdgesMap.get(vertex)).values();
			Iterator<EdgeWrapper<E>> edgeIterator = edgeWrappersOfVertex.iterator();

			// iterate over this vertex's edges
			while (edgeIterator.hasNext()) {

				// pick an edge and add it to the graph
				EdgeWrapper<E> edgeWrapper = edgeIterator.next();
				
				if(edgeWrapper.wasSetFirst()) {
					//if wasSetFirst were false, that would mean for an edge from A->B, an edge B->A was added first, 
					//and in an undirected graph we only need to count one edge to represent this relationship.
					edges.add(edgeWrapper.getEdge());
				}
				
			}
		}

		return edges;
	}

	/**
	 * Returns all the neighbors of a specified vertex.
	 * 
	 * @param vertex
	 *            The vertex to return neighbors for.
	 * 
	 * @return The list of vertices that are the neighbors of the specified
	 *         vertex.
	 */
	@Override
	public List<cs311.hw8.graph.IGraph.Vertex<V>> getNeighbors(String vertex) {
		Vertex<V> key = getVertex(vertex);

		// get edges associated with this vertex
		HashMap<String, EdgeWrapper<E>> edgesOfKey = vertexToEdgesMap.get(key);

		// iterate over the vertex names mapped to this vertex
		Collection<String> vertexNames = edgesOfKey.keySet();
		Iterator<String> namesIter = vertexNames.iterator();

		ArrayList<Vertex<V>> neighbors = new ArrayList<>();

		// iterate over neighbors of this vertex and get the Vertex object
		// associated with their names.
		while (namesIter.hasNext()) {
			Vertex<V> v = nameToVertexMap.get(namesIter.next());
			neighbors.add(v);
		}

		return neighbors;
	}
	

}
