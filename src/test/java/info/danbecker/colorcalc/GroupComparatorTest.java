package info.danbecker.colorcalc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupComparatorTest {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(GroupComparatorTest.class);	

	@BeforeEach
    public void setup() {
	}
	
	@Test
    public void testCompare() {
		String [] cols = {"A", "B", "C"};
		String [] sort = {"A"};
		
		// Test null cols and sorts
		GroupComparator gc = new GroupComparator( null, sort );
		assertEquals( 0, gc.compare( null, null ));
		gc = new GroupComparator( cols, null );
		assertEquals( 0, gc.compare( null, null ));
		// Test null data
		gc = new GroupComparator( cols, sort );
		assertEquals( 0, gc.compare( null, null ));
		assertEquals( 1, gc.compare( null, new String[] { "1" } ));
		assertEquals( -1, gc.compare( new String[] { "1" }, null ));

		// Test first field default
		gc = new GroupComparator( cols, new String[] {"A<<0"} );
		assertEquals( 0, gc.compare( new String[] { "1", "2", "3" }, new String[] { "1", "3", "4" } ));

		gc = new GroupComparator( cols, new String[] {"A<<10"} );
		assertEquals( 1, gc.compare( new String[] { "01", "2", "3" }, new String[] { "11", "3", "4" } ));

		gc = new GroupComparator( cols, new String[] {"A<<10"} );
		assertEquals( -1, gc.compare( new String[] { "11", "2", "3" }, new String[] { "01", "3", "4" } ));

		// Test first col
		assertEquals( 1, gc.compare( new String[] { "01", "2", "3" }, new String[] { "12", "3", "4" } ));
		assertEquals( -1, gc.compare( new String[] { "11", "2", "3" }, new String[] { "00", "3", "4" } ));
				
		// Test last col
		gc = new GroupComparator( cols, new String[] {"C>>10"} );
		assertEquals( 0, gc.compare( new String[] { "0", "2", "3" }, new String[] { "0", "3", "3" } ));
		assertEquals( 1, gc.compare( new String[] { "0", "2", "30" }, new String[] { "0", "3", "04" } ));
		assertEquals( -1, gc.compare( new String[] { "1", "2", "03" }, new String[] { "0", "1", "20" } ));

		// Test various comparators
		gc = new GroupComparator( cols, new String[] {"C>=10"} );
		assertEquals( 0, gc.compare( new String[] { "0", "2", "10" }, new String[] { "0", "3", "10" } ));
		gc = new GroupComparator( cols, new String[] {"C<=10"} );
		assertEquals( 0, gc.compare( new String[] { "0", "2", "10" }, new String[] { "0", "3", "10" } ));
		gc = new GroupComparator( cols, new String[] {"C==10"} );
		assertEquals( 0, gc.compare( new String[] { "0", "2", "10" }, new String[] { "0", "3", "10" } ));
		gc = new GroupComparator( cols, new String[] {"C!=10"} );
		assertEquals( 0, gc.compare( new String[] { "0", "2", "10" }, new String[] { "0", "3", "10" } ));
		assertEquals( 0, gc.compare( new String[] { "0", "2", "11" }, new String[] { "0", "3", "11" } ));
	}
}