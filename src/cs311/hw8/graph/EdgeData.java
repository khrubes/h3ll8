package cs311.hw8.graph;

import cs311.hw8.graphalgorithms.IWeight;

// Implementation of the E datatype inside of an Edge<E>
public class EdgeData implements IWeight {

	private double weight;
	private String name;

	public EdgeData(double weight) {
		this.weight = weight;
	}
	
	//Extra constructor added to allow weight and name in an EdgeData object
	public EdgeData(double weight, String name) {
		this.weight = weight;
		this.name = name;
	}

	@Override
	public double getWeight() {
		return this.weight;
	}
	
	public String getName(){
		return this.name;
	}

}
