package cs311.hw8.graphalgorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cs311.hw8.graph.EdgeData;
import cs311.hw8.graph.Graph;
import cs311.hw8.graph.IGraph;
import cs311.hw8.graph.IGraph.Edge;

public class ShortestPathTests<V, E extends IWeight> {

	//Note, had to change the IGraph.Edge constructor to public to be able to instantiate one
	
	IGraph<V, E> graph;
	
	@Before
	public void setup(){
		graph = new Graph<>();
		graph.setDirectedGraph();
	}
	
	private void buildLargeGraph(){
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");
		graph.addVertex("D");
		graph.addVertex("E");
		graph.addVertex("F");
		graph.addVertex("G");
		graph.addVertex("H");
		graph.addVertex("I");
		
		
		graph.addEdge("A", "B", (E) new EdgeData(4));
		graph.addEdge("A", "C", (E) new EdgeData(8));	

		
		graph.addEdge("B", "C", (E) new EdgeData(11));
		graph.addEdge("B", "D", (E) new EdgeData(8));
		
		graph.addEdge("C", "E", (E) new EdgeData(7));
		graph.addEdge("C", "F", (E) new EdgeData(1));
		
		graph.addEdge("D", "E", (E) new EdgeData(2));
		graph.addEdge("D", "G", (E) new EdgeData(7));
		graph.addEdge("D", "H", (E) new EdgeData(4));
		
		graph.addEdge("E", "F", (E) new EdgeData(6));
		
		graph.addEdge("F", "H", (E) new EdgeData(2));
		
		graph.addEdge("G", "H", (E) new EdgeData(1));
		graph.addEdge("G", "I", (E) new EdgeData(9));
		
		graph.addEdge("H", "I", (E) new EdgeData(10));
	}
	
	@Test
	public void testShortestPathLargeExample1() {	
		buildLargeGraph();
		
		//test shortest path from A -> I
		
		List<Edge<E>> shortestPath = new ArrayList<>();
		shortestPath.add(new Edge("A", "C", (E) new EdgeData(8))); 
		shortestPath.add(new Edge("C", "F", (E) new EdgeData(1))); 
		shortestPath.add(new Edge("F", "H", (E) new EdgeData(2))); 
		shortestPath.add(new Edge("H", "I", (E) new EdgeData(10))); 
		
		assertEquals(shortestPath, GraphAlgorithms.ShortestPath(graph, "A", "I"));	
	}
	
	
	@Test
	public void testShortestPathLargeExample2() {	
		buildLargeGraph();
		
		//test shortest path from A -> E
		
		List<Edge<E>> shortestPath = new ArrayList<>();
		shortestPath.add(new Edge("A", "B", (E) new EdgeData(4))); 
		shortestPath.add(new Edge("B", "D", (E) new EdgeData(8))); 
		shortestPath.add(new Edge("D", "E", (E) new EdgeData(2))); 
		
		assertEquals(shortestPath, GraphAlgorithms.ShortestPath(graph, "A", "E"));	
	}
	
	@Test
	public void testShortestPathLargeExample3NoPath() {	
		buildLargeGraph();
		
		//test shortest path from C -> A, which does not exist

		//not sure if null or an empty list is "correct"
		assertEquals(null, GraphAlgorithms.ShortestPath(graph, "C", "A"));	
	}
	
	@Test
	public void testShortestPathSelfLoop() {
		graph.addVertex("A");
		graph.addEdge("A", "A");
		List<Edge<E>> shortestPath = new ArrayList<>();
		shortestPath.add(new Edge("A", "A", new EdgeData(2))); 	
		assertEquals(shortestPath, GraphAlgorithms.ShortestPath(graph, "A", "A"));
		
	}
	
	@Test
	public void testShortestPathAB() {
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addEdge("A", "B", (E) new EdgeData(2));
		List<Edge<E>> shortestPath = new ArrayList<>();
		shortestPath.add(new Edge("A", "B", new EdgeData(2))); 	
		assertEquals(shortestPath, GraphAlgorithms.ShortestPath(graph, "A", "B"));	
	}
	
	
	@Test
	public void testShortestPathABC1() {	
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");
		
		graph.addEdge("A", "B", (E) new EdgeData(10));
		graph.addEdge("A", "C", (E) new EdgeData(3));
		graph.addEdge("C", "B", (E) new EdgeData(3));
		

		List<Edge<E>> shortestPath = new ArrayList<>();
		shortestPath.add(new Edge("A", "C", new EdgeData(3))); 	
		shortestPath.add(new Edge("C", "B", new EdgeData(2))); 	
		
		assertEquals(shortestPath, GraphAlgorithms.ShortestPath(graph, "A", "B"));	
	}
	
	@Test
	public void testShortestPathABC2() {	
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");
		
		graph.addEdge("A", "B", (E) new EdgeData(3));
		graph.addEdge("A", "C", (E) new EdgeData(3));
		graph.addEdge("C", "B", (E) new EdgeData(10));
		

		List<Edge<E>> shortestPath = new ArrayList<>();
		shortestPath.add(new Edge("A", "B", new EdgeData(3))); 		
		
		assertEquals(shortestPath, GraphAlgorithms.ShortestPath(graph, "A", "B"));	
	}
	
	
	@Test
	public void testShortestPathABCD1() {	
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");
		graph.addVertex("D");
		
		graph.addEdge("A", "B", (E) new EdgeData(7));
		graph.addEdge("A", "C", (E) new EdgeData(20));
		graph.addEdge("A", "D", (E) new EdgeData(10));
		
		graph.addEdge("B", "D", (E) new EdgeData(8));
		
		graph.addEdge("C", "D", (E) new EdgeData(1));
	

		List<Edge<E>> shortestPath = new ArrayList<>();
		shortestPath.add(new Edge("A", "D", new EdgeData(10))); 		
		
		assertEquals(shortestPath, GraphAlgorithms.ShortestPath(graph, "A", "D"));	
	}
	
	
	
	@Test
	public void testShortestPathABCDE1() {	
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");
		graph.addVertex("D");
		graph.addVertex("E");
		
		//one path to B
		graph.addEdge("A", "B", (E) new EdgeData(20));
		
		//other path to B
		graph.addEdge("A", "C", (E) new EdgeData(1));	
		graph.addEdge("C", "D", (E) new EdgeData(1));
		graph.addEdge("D", "E", (E) new EdgeData(1));
		graph.addEdge("E", "B", (E) new EdgeData(1));
	

		List<Edge<E>> shortestPath = new ArrayList<>();
		shortestPath.add(new Edge("A", "C", (E) new EdgeData(1))); 
		shortestPath.add(new Edge("C", "D", (E) new EdgeData(1))); 
		shortestPath.add(new Edge("D", "E", (E) new EdgeData(1)));
		shortestPath.add(new Edge("E", "B", (E) new EdgeData(1))); 
		
		assertEquals(shortestPath, GraphAlgorithms.ShortestPath(graph, "A", "B"));	
	}
	
	@Test
	public void testShortestPathABCDE2() {	
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");
		graph.addVertex("D");
		graph.addVertex("E");
		
		
		graph.addEdge("A", "B", (E) new EdgeData(2));
		graph.addEdge("A", "C", (E) new EdgeData(3));	
		graph.addEdge("A", "E", (E) new EdgeData(7));
		
		graph.addEdge("B", "C", (E) new EdgeData(1));
		graph.addEdge("B", "E", (E) new EdgeData(4));
		
		graph.addEdge("C", "D", (E) new EdgeData(4));
		
		graph.addEdge("D", "E", (E) new EdgeData(1));
		

		List<Edge<E>> shortestPath = new ArrayList<>();
		shortestPath.add(new Edge("A", "B", (E) new EdgeData(1))); 
		shortestPath.add(new Edge("B", "E", (E) new EdgeData(1))); 
		
		assertEquals(shortestPath, GraphAlgorithms.ShortestPath(graph, "A", "E"));	
	}

}
