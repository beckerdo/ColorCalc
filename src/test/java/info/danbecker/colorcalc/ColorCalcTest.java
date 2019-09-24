package info.danbecker.colorcalc;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
	@Before
    public void setup() {
	}
	
	@Test
    public void testParseOptions() {
		assertTrue( "option i null", null == ColorCalc.ins);
		assertTrue( "option d null", null == ColorCalc.dicts);
		assertTrue( "option o null", null == ColorCalc.out);
		assertTrue( "option s null", null == ColorCalc.sorts);
		assertTrue( "option g null", null == ColorCalc.groups);
		assertTrue( "option c null", null == ColorCalc.cols);
		assertTrue( "option t false", false == ColorCalc.table);
		assertTrue( "option p null", null == ColorCalc.plotName);
		assertTrue( "option r null", null == ColorCalc.comment);
		assertTrue( "option v false", false == ColorCalc.visualize);
		assertTrue( "option vs 0", 0 == ColorCalc.vSteps);
		assertTrue( "option vd 0", 0 == ColorCalc.vDelay);

		try {
			String [] options = new String []{"-i","foo,bar"};			
			ColorCalc.parseGatherOptions( options );
			// LOGGER.info( "Inputs length=" + ColorCalc.ins.length);
			assertTrue( "option i", Arrays.deepEquals(ColorCalc.ins, new String []{"foo", "bar"}));

			options = new String []{"-d","bar,baz"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option d", Arrays.deepEquals(ColorCalc.dicts, new String []{"bar", "baz"}));

			options = new String []{"-o","fred"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option o", "fred".equals(ColorCalc.out));

			options = new String []{"-s","a,b,c"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option s", Arrays.deepEquals(ColorCalc.sorts, new String []{"a","b","c"}));

			options = new String []{"-g","x,y,z"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option g", Arrays.deepEquals(ColorCalc.groups, new String []{"x","y","z"}));

			options = new String []{"-c","1,2,3"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option c", Arrays.deepEquals(ColorCalc.cols, new String []{"1","2","3"}));

			options = new String []{"-t"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option t",  ColorCalc.table);

			options = new String []{"-p","plotName"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option p", "plotName".equals(ColorCalc.plotName));

			options = new String []{"-r","remark"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option r", "remark".equals(ColorCalc.comment));

			options = new String []{"-v"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option v",  ColorCalc.visualize);

			options = new String []{"-vs","10"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option vs", Integer.valueOf( 10 ).equals(ColorCalc.vSteps));

			options = new String []{"-vd","20"};			
			ColorCalc.parseGatherOptions( options );
			assertTrue( "option vd", Integer.valueOf( 20 ).equals(ColorCalc.vDelay));


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
		
		assertTrue( "output data empty", 0 == outputData.size());
		ColorCalc.populateOutputData( outputData, dictionary, cols, headers, dictionaryHeaders, data);
		assertTrue( "output data populated", 1 == outputData.size());
		// Name=Bright Red, RGB=FF0000, HSL=000100050, RGB'=FF0000, Dict-Name=[red], RGB=FF0000
		String [] returnedData = outputData.get(0);
		LOGGER.debug( "returned data=" + Arrays.deepToString(returnedData));
		for( int col = 0; col < returnedData.length; col++) {
			switch( col ) {
			case 0: assertEquals( "name", data[ 1], returnedData[ col ]); break;
			case 1: assertEquals( "rgb", data[ 0], returnedData[ col ]); break;
			case 2: assertEquals( "hsl", "000100050", returnedData[ col ]); break;
			// case 3: assertEquals( "dict name", Arrays.asList("red"), returnedData[ col ]); break;
			case 4: assertEquals( "dict rgb", "FF0000", returnedData[ col ]); break;
			}
		}
	}

	@Test
    public void testClosestColor() {
		// public static Entry<Color,List<String>> closestColor( Map<Color,List<String>> dictionaryNames, Color color ){
		assertTrue( "dictionary null", null == ColorCalc.closestColor(null, Color.GRAY));
		
		Map<Color,List<String>> dictionary = new HashMap<>();
		dictionary.put( Color.RED, Arrays.asList("red"));
		dictionary.put( Color.GREEN, Arrays.asList("green"));
		dictionary.put( Color.BLUE, Arrays.asList("blue"));
		assertTrue( "color null", null == ColorCalc.closestColor(dictionary, null));

		assertTrue( "dictionary red", 
				new AbstractMap.SimpleEntry<Color,List<String>>(Color.RED, Arrays.asList("red")).equals(ColorCalc.closestColor(dictionary, Color.RED)) 
				);
		LOGGER.debug( "Cyan closest=" + ColorCalc.closestColor(dictionary, Color.CYAN));
		assertTrue( "dictionary green", 
				new AbstractMap.SimpleEntry<Color,List<String>>(Color.GREEN, Arrays.asList("green")).equals(ColorCalc.closestColor(dictionary, Color.CYAN)) 
				);
		assertTrue( "dictionary blue", 
				new AbstractMap.SimpleEntry<Color,List<String>>(Color.BLUE, Arrays.asList("blue")).equals(ColorCalc.closestColor(dictionary, Color.BLUE)) 
				);
	}

	@Test
    public void testUtils() {
		String[] DATA = { "foo", "bar" };
		
		assertTrue( "arrayPosition found", 1 == ColorCalc.arrayPosition(DATA, "bar") );
		assertTrue( "arrayPosition not found", -1 == ColorCalc.arrayPosition(DATA, "fred") );
		assertTrue( "arrayPosition null array", -1 == ColorCalc.arrayPosition( null, "fred") );
		assertTrue( "arrayPosition null search", -1 == ColorCalc.arrayPosition( DATA, null) );

		List<String[]> listOfArray = new ArrayList<>();
		assertTrue( "longestString empty", 0 == ColorCalc.longestString(listOfArray, 0) );
		listOfArray.add( DATA );
		assertTrue( "longestString", 3 == ColorCalc.longestString(listOfArray, 1) );
	}

}
