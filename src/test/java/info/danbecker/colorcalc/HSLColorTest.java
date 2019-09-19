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

		// Some weird dependency prevents assertEquals on SFFF or FFF signatures
//		float TOLERANCE = 0.0001f;
//		float[] FLOATGRAY1 = new float[] { 0.5f, 0.5f, 0.5f, 1.0f };
//		HSLColor FLOATGRAYHSL1 = new HSLColor( FLOATGRAY1 );
//		LOGGER.info( "Float Gray HSL=" + FLOATGRAYHSL1.toString());
//		float[] FLOATGRAY1RETURNED = FLOATGRAYHSL1.getHSLA();
//		assertEquals( "equality f", 0.5f, FLOATGRAY1RETURNED[0], TOLERANCE );
//		assertEquals( "equality s", 0.5f, FLOATGRAY1RETURNED[1], TOLERANCE );
//		assertEquals( "equality l", 0.5f, FLOATGRAY1RETURNED[2], TOLERANCE );
//		assertEquals( "equality a", 1.0f, FLOATGRAY1RETURNED[3], TOLERANCE );
		
		
	}
	@Test
    public void testStrings() {
		HSLColor DANGRAY = HSLColor.fromString( "000050050" );		
		assertEquals( "to Color to String", "HSLColor[h=0.0,s=50.0,l=50.0,alpha=1.0]",  DANGRAY.toString());

	}

}
