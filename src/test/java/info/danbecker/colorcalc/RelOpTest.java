package info.danbecker.colorcalc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RelOpTest {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger( RelOpTest.class);

	@BeforeEach
    public void setup() {
	}
	
	@Test
    public void testEnum() {
		RelOp[] values = RelOp.values();
		for ( RelOp value : values ) {
			// LOGGER.info( "RelOp value=" + value + ", name=" + value.name() + ", getName=" + value.getName() + ", symbol=" + value.getSymbol());
			assertEquals( value, RelOp.fromName( value.name()) );
			assertEquals( value, RelOp.fromSymbol( value.getSymbol() ));
		}
		
		// assertEquals( "name null", null, RelOp.fromName(null) ); // exception
		assertEquals( null, RelOp.fromSymbol(null) );
	}

}
