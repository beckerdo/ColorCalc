package info.danbecker.colorcalc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColTest {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger( ColTest.class);

	@BeforeEach
    public void setup() {
	}
	
	@Test
    public void testEnum() {
		Col[] values = Col.values();
		// System.out.println( "Col values count=" + values.length );
		for ( Col value : values ) {
			// System.out.println( "Col value=" + value + ", name=" + value.name() + ", getName=" + value.getName() + ", symbol=" + value.getAbbreviation());
			assertEquals( value, Col.fromAbbreviation( value.getAbbreviation() ), "fromAbbreviation");
		}
		
		// assertEquals( "name null", null, RelOp.fromName(null) ); // exception
		assertEquals( null, Col.fromAbbreviation(null), "fromSymbol null");
	}

}
