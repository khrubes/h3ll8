package cs311.hw8.graph;

import cs311.hw8.graph.IGraph.Edge;

public class EdgeWrapper<E> {

	/**
	 * True if edge (A , B) is added to the graph, and (B , A) is not. Used to break
	 * ties on which edge to remove for a call to setUndirected
	 */
	private boolean wasSetFirst;
	private Edge<E> edge;

	public EdgeWrapper(Edge<E> e, boolean wasSetFirst) {
		this.wasSetFirst = wasSetFirst;
		this.edge = e;
	}

	public Edge<E> getEdge() {
		return this.edge;
	}

	public void setEdge(Edge<E> e) {
		this.edge = e;
	}

	public boolean wasSetFirst() {
		return this.wasSetFirst;
	}
}
