package info.danbecker.colorcalc;

import java.awt.Color;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ColorUtilsTest {	
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger( ColorUtilsTest.class);
	
	public static final double TOLERANCE = 0.01;
	
	@Before
    public void setup() {
	}
	
	@Test
    public void testStatics() {
		Color DANGRAY = ColorUtils.toColor( "#7f7f7f" );		
		assertEquals( "to Color to String", "7F7F7F", ColorUtils.toRGB(DANGRAY));
		assertTrue( "to Color null", null == ColorUtils.toColor( null ) );
		assertTrue( "to RGB null", null == ColorUtils.toRGB( null ) );
		Color DANGRAYCLEAR = ColorUtils.toColor( "#7f7f7f7f" );		
		assertEquals( "to Color to String alpha", "7F7F7F7F", ColorUtils.toRGBA(DANGRAYCLEAR));
		assertTrue( "to RGBA null", null == ColorUtils.toRGBA( null ) );
		
		// toANSI
		assertTrue( "toANSIRGB null", null == ColorUtils.toANSIRGB( null, true ) );
		String ansi = ColorUtils.toANSIRGB( ColorUtils.toColor("#7f7f7f7f"), true );
		assertTrue( "toANSIRGB fore", ansi.contains( "[38;2;127;127;127m")  );
		ansi = ColorUtils.toANSIRGB( ColorUtils.toColor("#7f7f7f7f"), false );
		assertTrue( "toANSIRGB bacl", ansi.contains( "[48;2;127;127;127m")  );
		
		// distance weighted
		assertEquals( "distance weighted null", Double.MAX_VALUE, ColorUtils.distanceWeighted( DANGRAY, null ), TOLERANCE);
		assertEquals( "distance weighted null", Double.MAX_VALUE, ColorUtils.distanceWeighted( null, DANGRAY ), TOLERANCE);
		assertEquals( "distance weighted", 380.92, ColorUtils.distanceWeighted( DANGRAY, Color.BLACK ), TOLERANCE);

		// distance euclidean
		assertEquals( "distance euclidean null", Double.MAX_VALUE, ColorUtils.distanceEuclidean( DANGRAY, null ), TOLERANCE);
		assertEquals( "distance euclidean null", Double.MAX_VALUE, ColorUtils.distanceEuclidean( null, DANGRAY ), TOLERANCE);
		assertEquals( "distance euclidean", 219.97, ColorUtils.distanceEuclidean( DANGRAY, Color.BLACK ), TOLERANCE);
		
		// alpha
		int newAlpha = 255/2;
	    Color color = ColorUtils.changeAlpha( Color.BLACK, newAlpha ); 
		assertEquals( "alpha r", Color.BLACK.getRed(), color.getRed() );
		assertEquals( "alpha g", Color.BLACK.getGreen(), color.getGreen() );
		assertEquals( "alpha b", Color.BLACK.getBlue(), color.getBlue() );
		assertEquals( "alpha a", newAlpha, color.getAlpha() );

		// gray
		HSLColor hslColor = new HSLColor( Color.WHITE );
		Color gray = ColorUtils.luminanceGray(hslColor);
		assertEquals( "gray", 0xff, gray.getRed() );
		assertEquals( "gray", 0xff, gray.getGreen() );
		assertEquals( "gray", 0xff, gray.getBlue() );
		assertEquals( "gray", 0xff, gray.getAlpha() );
		hslColor = new HSLColor( Color.RED );
		gray = ColorUtils.luminanceGray(hslColor);
		assertEquals( "gray", 0x80, gray.getRed() );
		assertEquals( "gray", 0x80, gray.getGreen() );
		assertEquals( "gray", 0x80, gray.getBlue() );
		assertEquals( "gray", 0xff, gray.getAlpha() );
	}
}
