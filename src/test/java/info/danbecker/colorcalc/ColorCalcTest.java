package info.danbecker.colorcalc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ColorCalcTest {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(ColorCalcTest.class);	
	@BeforeEach
    public void setup() {
	}
	
	@Test
    public void testParseOptions() {
		assertTrue( null == ColorCalc.ins, "option i null");
		assertTrue( null == ColorCalc.dicts, "option d null");
		assertTrue( null == ColorCalc.out, "option o null");
		assertTrue( null == ColorCalc.sorts, "option s null");
		assertTrue( null == ColorCalc.groups, "option g null");
		assertTrue( null == ColorCalc.cols, "option c null");
		assertTrue( false == ColorCalc.table, "option t false");
		assertTrue( null == ColorCalc.plotName, "option p null");
		assertTrue( null == ColorCalc.comment, "option r null");
		assertTrue( false == ColorCalc.visualize, "option v false");
		assertTrue( 0 == ColorCalc.vSteps,"option vs 0");
		assertTrue( 0 == ColorCalc.vDelay, "option vd 0");

		try {
			String [] options = new String []{"-i","foo,bar"};			
			ColorCalc.parseGatherOptions( options );
			// LOGGER.info( "Inputs length=" + ColorCalc.ins.length);
			assertTrue( Arrays.deepEquals(ColorCalc.ins, new String []{"foo", "bar"}), "option i");

			options = new String []{"-d","bar,baz"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( Arrays.deepEquals(ColorCalc.dicts, new String []{"bar", "baz"}), "option d");

			options = new String []{"-o","fred"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "fred".equals(ColorCalc.out), "option o");

			options = new String []{"-s","a,b,c"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( Arrays.deepEquals(ColorCalc.sorts, new String []{"a","b","c"}), "option s");

			options = new String []{"-g","x,y,z"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( Arrays.deepEquals(ColorCalc.groups, new String []{"x","y","z"}), "option g");

			options = new String []{"-c","1,2,3"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( Arrays.deepEquals(ColorCalc.cols, new String []{"1","2","3"}),"option c");

			options = new String []{"-t"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( ColorCalc.table, "option t");

			options = new String []{"-p","plotName"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "plotName".equals(ColorCalc.plotName), "option p");

			options = new String []{"-r","remark"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "remark".equals(ColorCalc.comment), "option r");

			options = new String []{"-v"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( ColorCalc.visualize, "option v");

			options = new String []{"-vs","10"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( Integer.valueOf( 10 ).equals(ColorCalc.vSteps), "option vs");

			options = new String []{"-vd","20"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( Integer.valueOf( 20 ).equals(ColorCalc.vDelay), "option vd");


		} catch (Exception e) {
			LOGGER.error( "parseOptions", e);
		}
	}

	@Test
    public void testPopulateOutputData() {
		/** Take the given data line and populate the columns of the output file
		 *  from the dictionary and calculations 
		 *  Example column names  "Name,RGB,HSL,Dict-Name,Dict-RGB,Dict-HSL"
		 */
		// public static void populateOutputData(List<String[]> outputData, Map<Color,List<String>> dictionaryNames,
		// 		String[] cols, String[] headers, String[] dictionaryHeaders, String[] data) {
		List<String[]> outputData = new LinkedList<>();
		Map<Color,List<String>> dictionary = new HashMap<>();
		dictionary.put( Color.RED, Arrays.asList("red"));
		dictionary.put( Color.GREEN, Arrays.asList("green"));
		dictionary.put( Color.BLUE, Arrays.asList("blue"));
		
		String [] cols = new String[] {"Name","RGB","HSL","Dict-Name","Dict-RGB"};
		String [] headers = new String[] {"RGB","Name","Owner" };
		String [] dictionaryHeaders = new String[] {"Dict-Name", "Dict-RGB" };
		String [] data = new String[] { "FF0000", "Bright Red", "Fred"};
		
		assertTrue( 0 == outputData.size(), "output data empty");
		ColorCalc.populateOutputData( outputData, dictionary, cols, headers, dictionaryHeaders, data);
		assertTrue( 1 == outputData.size(), "output data populated");
		// Name=Bright Red, RGB=FF0000, HSL=000100050, RGB'=FF0000, Dict-Name=[red], RGB=FF0000
		String [] returnedData = outputData.get(0);
		LOGGER.debug( "returned data=" + Arrays.deepToString(returnedData));
		for( int col = 0; col < returnedData.length; col++) {
			switch( col ) {
			case 0: assertEquals( data[ 1], returnedData[ col ], "name"); break;
			case 1: assertEquals( data[ 0], returnedData[ col ], "rgb"); break;
			case 2: assertEquals( "000100050", returnedData[ col ], "hsl"); break;
			// case 3: assertEquals( "dict name", Arrays.asList("red"), returnedData[ col ]); break;
			case 4: assertEquals( "FF0000", returnedData[ col ], "dict rgb"); break;
			}
		}
	}

	@Test
    public void testClosestColor() {
		// public static Entry<Color,List<String>> closestColor( Map<Color,List<String>> dictionaryNames, Color color ){
		assertTrue( null == ColorCalc.closestColor(null, Color.GRAY), "dictionary null");
		
		Map<Color,List<String>> dictionary = new HashMap<>();
		dictionary.put( Color.RED, Arrays.asList("red"));
		dictionary.put( Color.GREEN, Arrays.asList("green"));
		dictionary.put( Color.BLUE, Arrays.asList("blue"));
		assertTrue( null == ColorCalc.closestColor(dictionary, null), "color null");

		assertTrue(  
				new AbstractMap.SimpleEntry<Color,List<String>>(Color.RED, Arrays.asList("red")).equals(ColorCalc.closestColor(dictionary, Color.RED)),
				"dictionary red"
				);
		LOGGER.debug( "Cyan closest=" + ColorCalc.closestColor(dictionary, Color.CYAN));
		assertTrue(  
				new AbstractMap.SimpleEntry<Color,List<String>>(Color.GREEN, Arrays.asList("green")).equals(ColorCalc.closestColor(dictionary, Color.CYAN)),
				"dictionary green"
				);
		assertTrue(  
				new AbstractMap.SimpleEntry<Color,List<String>>(Color.BLUE, Arrays.asList("blue")).equals(ColorCalc.closestColor(dictionary, Color.BLUE)),
				"dictionary blue"
				);
	}

	@Test
    public void testUtils() {
		String[] DATA = { "foo", "bar" };
		
		assertTrue( 1 == ColorCalc.arrayPosition(DATA, "bar"), "arrayPosition found");
		assertTrue( -1 == ColorCalc.arrayPosition(DATA, "fred"), "arrayPosition not found");
		assertTrue( -1 == ColorCalc.arrayPosition( null, "fred"), "arrayPosition null array");
		assertTrue( -1 == ColorCalc.arrayPosition( DATA, null), "arrayPosition null search");

		List<String[]> listOfArray = new ArrayList<>();
		assertTrue( 0 == ColorCalc.longestString(listOfArray, 0), "longestString empty");
		listOfArray.add( DATA );
		assertTrue( 3 == ColorCalc.longestString(listOfArray, 1), "longestString");
	}

	@Test
    public void testAddToDictionary() {
	    // protected static Map<Color,List<String>> dictionaryNames = new HashMap<>(); 

		// public static void addToDictionary(Map<Color, List<String>> dictionaryNames, String[] localDictionaryHeaders, String line) {
		
		// Test nulls
		assertThrows(NumberFormatException.class, () -> { Integer.parseInt("One"); } , "expected exception");
	}
}
