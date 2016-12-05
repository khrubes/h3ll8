package cs311.hw8.graphalgorithms.datastructures;

import cs311.hw8.graph.IGraph.Vertex;

public class VertexDistanceWrapper<V> implements Comparable {

	private Vertex<V> vertex;
	private double distance;

	public VertexDistanceWrapper(Vertex<V> vertex, double distance){
		this.vertex = vertex;
		this.distance = distance;
	}

	@Override
	public int compareTo(Object o) {	
		VertexDistanceWrapper<V> other = (VertexDistanceWrapper<V>) o;		
		return Double.compare(this.distance, other.distance);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vertex == null) ? 0 : vertex.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VertexDistanceWrapper))
			return false;
		VertexDistanceWrapper other = (VertexDistanceWrapper) obj;
		if (vertex == null) {
			if (other.vertex != null)
				return false;
		} else if (!vertex.equals(other.vertex))
			return false;
		return true;
	}
}
