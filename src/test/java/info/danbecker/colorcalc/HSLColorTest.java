package info.danbecker.colorcalc;

import java.awt.Color;
import java.util.Arrays;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

		Color DANRED = new Color(0xBF, 0x40, 0x40); // strange HSL to RGB values
		HSLColor DANREDFLOAT = new HSLColor( 0.0f, 50.0f, 50.0f ); // Use 360/100/100 convention
		assertEquals( "equality float", DANRED, DANREDFLOAT.getRGB() );

		DANREDFLOAT = new HSLColor( new float[] { 0.0f, 50.0f, 50.0f }); // Use 360/100/100 convention
		assertEquals( "equality float", DANRED, DANREDFLOAT.getRGB() );
}
	
	@Test
    public void testStrings() {
		HSLColor DANGRAYHSL = HSLColor.fromString( "000050050" );		
		assertEquals( "to String", "HSLColor[h=0.0,s=50.0,l=50.0,alpha=1.0]",  DANGRAYHSL.toString());

		Color DANGRAY = new Color(0x7F, 0x7F, 0x7F, 0xFF);
		LOGGER.info( "Dan Gray=" + DANGRAY.toString());
		assertEquals( "from Color to String", "000000049",  HSLColor.toString(DANGRAY));

		Color DANRED = new Color(0xBF, 0x40, 0x40); // strange HSL to RGB values
		HSLColor DANREDFLOAT = HSLColor.fromString( "000050050" ); // Use 360/100/100 convention
		assertEquals( "equality float", DANRED, DANREDFLOAT.getRGB() );
	}

	@Test
    public void testGettersSetters() {
		HSLColor DANREDFLOAT = new HSLColor( 0.0f, 50.0f, 50.0f ); // Use 360/100/100 convention
		// assertEquals( "equality float", DANRED, DANREDFLOAT.getRGB() );
		assertEquals( "get hue", 0.0f, DANREDFLOAT.getHue(), HSLColor.TOLERANCE );
		assertEquals( "get sat", 50.0f, DANREDFLOAT.getSaturation(), HSLColor.TOLERANCE );
		assertEquals( "get lum", 50.0f, DANREDFLOAT.getLuminance(), HSLColor.TOLERANCE );
		assertEquals( "get alpha", 1.0f, DANREDFLOAT.getAlpha(), HSLColor.TOLERANCE );

		assertTrue( "get hsl", Arrays.equals( new float[] {0.0f, 50.0f, 50.0f }, DANREDFLOAT.getHSL()) );
		assertTrue( "get hsla", Arrays.equals( new float[] {0.0f, 50.0f, 50.0f, 1.0f }, DANREDFLOAT.getHSLA()) );
		
		Color DANRED = new Color(0xBF, 0x40, 0x40); // strange HSL to RGB values
		assertEquals( "toRGB", DANRED, HSLColor.toRGB(0.0f, 50.0f, 50.0f) );
		assertEquals( "toRGB", DANRED, HSLColor.toRGB( new float[] { 0.0f, 50.0f, 50.0f}) );
		assertEquals( "toRGB", DANRED, HSLColor.toRGB(0.0f, 50.0f, 50.0f, 1.0f) );
		assertEquals( "toRGB", DANRED, HSLColor.toRGB( new float[] { 0.0f, 50.0f, 50.0f, 1.0f}) );
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
    public void testCompareEqualsHash() {
		Color RED = new Color(0xFF, 0x00, 0x00, 0xFF);
		HSLColor REDHSL = new HSLColor( RED );
		Color REDTRANS = new Color(0xFF, 0x00, 0x00, 0x7F);
		HSLColor REDTRANSHSL = new HSLColor( REDTRANS );
		Color YELLOW = new Color(0xFF, 0xFF, 0x00, 0xFF);
		HSLColor YELLOWHSL = new HSLColor( YELLOW );

		// Equals
		assertTrue( "equals null", false == REDHSL.equals( null ));
		assertTrue( "equals non", false == REDHSL.equals( RED ));
		assertTrue( "equals self", true == REDHSL.equals( REDHSL ));
		assertTrue( "equals alpha", false == REDHSL.equals( REDTRANSHSL ));
		assertTrue( "equals other", false == REDHSL.equals( YELLOWHSL ));
		
		// Compare
		assertTrue( "compare null", 0 < HSLColor.compare( REDHSL, null ));
		assertTrue( "compare null", 0 > HSLColor.compare( null, REDHSL ));
		assertTrue( "compare self", 0 == HSLColor.compare( REDHSL, REDHSL ));
		// LOGGER.info( "compare alpha=" + HSLColor.compare( REDHSL, REDTRANSHSL ));
		assertTrue( "compare alpha", 0 > HSLColor.compare( REDHSL, REDTRANSHSL ));
		assertTrue( "compare other", 0 > HSLColor.compare( REDHSL, YELLOWHSL ));
		assertTrue( "compare compareTo", 0 > REDHSL.compareTo( YELLOWHSL ));
		
		// Hash
		assertTrue( "hash non zero", 0 != REDHSL.hashCode());
		assertTrue( "hash compare", REDHSL.hashCode() != REDTRANSHSL.hashCode());
	}
}	
