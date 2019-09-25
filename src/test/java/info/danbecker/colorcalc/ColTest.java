package info.danbecker.colorcalc;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

public class ColTest {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger( ColTest.class);

	@Before
    public void setup() {
	}
	
	@Test
    public void testEnum() {
		Col[] values = Col.values();
		// System.out.println( "Col values count=" + values.length );
		for ( Col value : values ) {
			// System.out.println( "Col value=" + value + ", name=" + value.name() + ", getName=" + value.getName() + ", symbol=" + value.getAbbreviation());
			assertEquals( "fromAbbreviation", value, Col.fromAbbreviation( value.getAbbreviation() ));
		}
		
		// assertEquals( "name null", null, RelOp.fromName(null) ); // exception
		assertEquals( "fromSymbol null", null, Col.fromAbbreviation(null) );
	}

}
