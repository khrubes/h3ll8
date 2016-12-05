package cs311.hw8.graphalgorithms.datastructures;

import java.util.ArrayList;

/*
 * Custom heap/priority queue to be used in Djkstras, which supports re-heapifying if ..... hmm maybe
 * */
public class Heap<V> {

	private ArrayList<VertexDistanceWrapper<V>> heap;
	
	public Heap(){
		this.heap = new ArrayList<>();
	}
	
	private int parent(int index) {
        return (index - 1) / 2;
    }

	private int left(int index) {
        return 2 * index + 1;
    }

	private int right(int index) {
        return 2 * index + 2;
    }
	
//	public boolean add(VertexDistanceWrapper<V> vertex) {
//		
//	}

	
}
