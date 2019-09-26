package info.danbecker.colorcalc;

import java.awt.Color;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ColorUtilsTest {	
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger( ColorUtilsTest.class);
	
	public static final double TOLERANCE = 0.01;
	
	@BeforeEach
    public void setup() {
	}
	
	@Test
    public void testStatics() {
		Color DANGRAY = ColorUtils.toColor( "#7f7f7f" );		
		assertEquals( "7F7F7F", ColorUtils.toRGB(DANGRAY));
		assertTrue( null == ColorUtils.toColor( null ) );
		assertTrue( null == ColorUtils.toRGB( null ) );
		Color DANGRAYCLEAR = ColorUtils.toColor( "#7f7f7f7f" );		
		assertEquals( "7F7F7F7F", ColorUtils.toRGBA(DANGRAYCLEAR));
		assertTrue( null == ColorUtils.toRGBA( null ) );
		
		// toANSI
		assertTrue( null == ColorUtils.toANSIRGB( null, true ) );
		String ansi = ColorUtils.toANSIRGB( ColorUtils.toColor("#7f7f7f7f"), true );
		assertTrue( ansi.contains( "[38;2;127;127;127m")  );
		ansi = ColorUtils.toANSIRGB( ColorUtils.toColor("#7f7f7f7f"), false );
		assertTrue( ansi.contains( "[48;2;127;127;127m")  );
		
		// distance weighted
		assertEquals( Double.MAX_VALUE, ColorUtils.distanceWeighted( DANGRAY, null ), TOLERANCE);
		assertEquals( Double.MAX_VALUE, ColorUtils.distanceWeighted( null, DANGRAY ), TOLERANCE);
		assertEquals( 380.92, ColorUtils.distanceWeighted( DANGRAY, Color.BLACK ), TOLERANCE);

		// distance euclidean
		assertEquals( Double.MAX_VALUE, ColorUtils.distanceEuclidean( DANGRAY, null ), TOLERANCE);
		assertEquals( Double.MAX_VALUE, ColorUtils.distanceEuclidean( null, DANGRAY ), TOLERANCE);
		assertEquals( 219.97, ColorUtils.distanceEuclidean( DANGRAY, Color.BLACK ), TOLERANCE);
		
		// alpha
		int newAlpha = 255/2;
	    Color color = ColorUtils.changeAlpha( Color.BLACK, newAlpha ); 
		assertEquals( Color.BLACK.getRed(), color.getRed() );
		assertEquals( Color.BLACK.getGreen(), color.getGreen() );
		assertEquals( Color.BLACK.getBlue(), color.getBlue() );
		assertEquals( newAlpha, color.getAlpha() );

		// gray
		HSLColor hslColor = new HSLColor( Color.WHITE );
		Color gray = ColorUtils.luminanceGray(hslColor);
		assertEquals( 0xff, gray.getRed() );
		assertEquals( 0xff, gray.getGreen() );
		assertEquals( 0xff, gray.getBlue() );
		assertEquals( 0xff, gray.getAlpha() );
		hslColor = new HSLColor( Color.RED );
		gray = ColorUtils.luminanceGray(hslColor);
		assertEquals( 0x80, gray.getRed() );
		assertEquals( 0x80, gray.getGreen() );
		assertEquals( 0x80, gray.getBlue() );
		assertEquals( 0xff, gray.getAlpha() );
	}
}
