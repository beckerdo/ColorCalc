package info.danbecker.colorcalc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorFieldComparatorTest {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(ColorFieldComparatorTest.class);	

	@BeforeEach
    public void setup() {
	}
	
	@Test
    public void testCompare() {
		String [] cols = {"A", "B", "C"};
		String [] sort = {"A"};
		
		// Test null cols and sorts
		ColorFieldComparator cfc = new ColorFieldComparator( null, sort );
		assertEquals( 0, cfc.compare( null, null ));
		cfc = new ColorFieldComparator( cols, null );
		assertEquals( 0, cfc.compare( null, null ));
		// Test null data
		cfc = new ColorFieldComparator( cols, sort );
		assertEquals( 0, cfc.compare( null, null ));
		assertEquals( 1, cfc.compare( null, new String[] { "1" } ));
		assertEquals( -1, cfc.compare( new String[] { "1" }, null ));

		// Test first field default
		cfc = new ColorFieldComparator( cols, new String[] {"A"} );
		assertEquals( 0, cfc.compare( new String[] { "1", "2", "3" }, new String[] { "1", "3", "4" } ));

		cfc = new ColorFieldComparator( cols, new String[] {"A++"} );
		assertEquals( 0, cfc.compare( new String[] { "1", "2", "3" }, new String[] { "1", "3", "4" } ));

		cfc = new ColorFieldComparator( cols, new String[] {"A--"} );
		assertEquals( 0, cfc.compare( new String[] { "1", "2", "3" }, new String[] { "1", "3", "4" } ));

		// Test first col
		assertEquals( 1, cfc.compare( new String[] { "1", "2", "3" }, new String[] { "2", "3", "4" } ));
		assertEquals( -1, cfc.compare( new String[] { "1", "2", "3" }, new String[] { "0", "3", "4" } ));
				
		// Test last col
		cfc = new ColorFieldComparator( cols, new String[] {"C"} );
		assertEquals( 0, cfc.compare( new String[] { "1", "2", "3" }, new String[] { "2", "3", "3" } ));
		assertEquals( -1, cfc.compare( new String[] { "1", "2", "3" }, new String[] { "2", "3", "4" } ));
		assertEquals( 1, cfc.compare( new String[] { "1", "2", "3" }, new String[] { "0", "1", "2" } ));
	}
}