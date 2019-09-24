package info.danbecker.colorcalc;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorCalcTest {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(ColorCalcTest.class);	
	@Before
    public void setup() {
	}
	
	@Test
    public void testParseOptions() {
//        options.addOption("s", "sorts", true, "column sort fields (followed by + or - for ascending, descending)");
//        options.addOption("g", "groups", true, "column sort fields ");
//        options.addOption("c", "cols", true, "column output fields");
//        options.addOption("t", "table", false, "output a colorful HTML table"); // switch option
//        options.addOption("p", "plot", true, "output a plot of the colors to the given file");
//        options.addOption("r", "comment", true, "output file comment (remark)");
//        options.addOption("v", "visualize", false, "open interactive window with 3D plot"); // switch option
//        options.addOption("vs", " visual steps", true, "number of steps in visual animation");
//        options.addOption("vd", " visual delay", true, "millisecond delay between animation steps (0 for none)");

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
