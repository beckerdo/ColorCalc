package info.danbecker.colorcalc;

import java.awt.Color;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

public class HSLColorTest {
	
	public static final Float TOLERANCE = 0.0001f;
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger( HSLColorTest.class);
	
	@Before
    public void setup() {
	}
	
	@Test
    public void testConstructors() {
		LOGGER.info( "Gray=" + Color.GRAY.toString());
		HSLColor GRAYHSL = new HSLColor( Color.GRAY );
		LOGGER.info( "Gray HSL=" + GRAYHSL.toString());		
		assertEquals( "equality", Color.GRAY, GRAYHSL.getRGB() );

		Color DANGRAY = new Color(0x7F, 0x7F, 0x7F);
		LOGGER.info( "Dan Gray=" + DANGRAY.toString());
		HSLColor DANGRAYHSL = new HSLColor( DANGRAY );
		LOGGER.info( "Dan Gray HSL=" + DANGRAYHSL.toString());		
		assertEquals( "equality", DANGRAY, DANGRAYHSL.getRGB() );
	}

}
