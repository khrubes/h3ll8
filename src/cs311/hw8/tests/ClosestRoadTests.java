package cs311.hw8.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import cs311.hw8.graphalgorithms.OSMMap;
import cs311.hw8.graphalgorithms.OSMMap.Location;

public class ClosestRoadTests {

	OSMMap map;
	
	
	public void setupSmallMap(){
		map = new OSMMap("AmesMapSmall.txt");
	}
	
	
	//<node id="JeffsPizza" lat="42.022368" lon="-93.648518"
	@Test
	public void testClosestRoadExactJeffs() {
		setupSmallMap();		
		String closestRoadId = map.ClosestRoad(new Location(42.022368, -93.648518));		
		assertEquals("JeffsPizza", closestRoadId);
	}
	
	
	@Test
	public void testClosestRoadEsTasJeffs() {
		setupSmallMap();		
		String closestRoadId = map.ClosestRoad(new Location(42.020890, -93.648489));		
		assertEquals("JeffsPizza", closestRoadId);
	}
	
	
	//<node id="West HyVee" lat="42.022559" lon="-93.668774"
	@Test
	public void testClosestRoadExactWestHyvee() {
		setupSmallMap();		
		String closestRoadId = map.ClosestRoad(new Location(42.022559, -93.668774));		
		assertEquals("West HyVee", closestRoadId);
	}
	
	
	
	//<node id="Cemetery" lat="42.028425" lon="-93.601912"
	@Test
	public void testClosestRoadFartherCemetery() {
		setupSmallMap();		
		String closestRoadId = map.ClosestRoad(new Location(42.028425, -93.601912));		
		assertEquals("Cemetery", closestRoadId);
	}
	

}
