package cs311.hw8.graph;

import cs311.hw8.graphalgorithms.IWeight;

// Implementation of the E datatype inside of an Edge<E>
public class EdgeData implements IWeight {

	private double weight;

	public EdgeData(double weight) {
		this.weight = weight;
	}

	@Override
	public double getWeight() {
		return this.weight;
	}

}
