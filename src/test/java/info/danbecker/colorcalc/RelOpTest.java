package info.danbecker.colorcalc;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

public class RelOpTest {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger( RelOpTest.class);

	@Before
    public void setup() {
	}
	
	@Test
    public void testEnum() {
		RelOp[] values = RelOp.values();
		for ( RelOp value : values ) {
			// LOGGER.info( "RelOp value=" + value + ", name=" + value.name() + ", getName=" + value.getName() + ", symbol=" + value.getSymbol());
			assertEquals( "name", value, RelOp.fromName( value.name()) );
			assertEquals( "fromSymbol", value, RelOp.fromSymbol( value.getSymbol() ));
		}
		
		// assertEquals( "name null", null, RelOp.fromName(null) ); // exception
		assertEquals( "fromSymbol null", null, RelOp.fromSymbol(null) );
	}

}
