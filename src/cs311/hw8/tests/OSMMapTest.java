package cs311.hw8.tests;

import cs311.hw8.graph.Graph;
import cs311.hw8.graphalgorithms.AlgorithmsHelper;
import cs311.hw8.graphalgorithms.OSMMap;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Keane on 12/3/2016.
 */
public class OSMMapTest {
    // Change these paths to match your setup. For example, my text files are in the outer package directory
    private static final String routefile = "routes2.txt";
    private static final String mapfile = "AmesMap.txt";
    
    private static OSMMap m;

    @BeforeClass
    public static void setup() {
        m = new OSMMap();
        long starttime = System.nanoTime();
        m.LoadMap(mapfile);
        long time = System.nanoTime() - starttime;
        System.out.println("Load took " + time / 1000000 + " ms");
    }

    @Test
    public void testLoad() {
        Graph g = m.getGraph();
        boolean isDirected = g.isDirectedGraph();
        int numVertices =  g.getVertices().size();
        int numEdges = g.getEdges().size();
        assertTrue(isDirected);
        assertEquals(121648, numVertices);
        assertEquals(34785, numEdges);
    }

    @Test
    public void testClosestRoad() {
        String v = m.ClosestRoad(new OSMMap.Location(42.0492620, -93.7442400));
        assertEquals("159018339", v);
    }

    @Test
    public void testAlmostClosestRoad() {
        String v = m.ClosestRoad(new OSMMap.Location(42.0492621, -93.7442499));
        assertEquals("159018339", v);
    }

    @Test
    public void testNotSoClosestRoad() {
        String v = m.ClosestRoad(new OSMMap.Location(42.0492600, -93.7442450));
        assertEquals("159018339", v);
    }

    @Test
    public void testmain3() {
        /*
        Here's a hack that makes sure the method prints the right thing.
        http://stackoverflow.com/a/8708357
        */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        String args[] = {mapfile, routefile};
        OSMMap.main3(args);
        // On Windows, use \r\n, on *nix, change to \n. Sorry for not writing portable code...
        assertEquals("Marshall Avenue\r\n" +
                "Story Street\r\n" +
                "West Street\r\n" +
                "Union Drive\r\n" +
                "Bissell Road\r\n" +
                "Osborn Drive\r\n", baos.toString());
    }

//    @Test
//    public void testmain2() {
//        /*
//        Here's a hack that makes sure the method prints the right thing.
//        http://stackoverflow.com/a/8708357
//        */
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PrintStream ps = new PrintStream(baos);
//        System.setOut(ps);
//        OSMMap.main2(null);
//        assertEquals(763.012, Double.valueOf(baos.toString()), .1); // delta might be too strict
//    }

    @Test
    public void testgetDistance() {
        assertEquals(0.0, AlgorithmsHelper.distanceLatLon(42.990967, -71.463767, 42.990967, -71.463767));
        // Increase delta if needed
        assertEquals(5.52, AlgorithmsHelper.distanceLatLon(42.910970, -71.463767, 42.990967, -71.463767), 0.1);
    }
}