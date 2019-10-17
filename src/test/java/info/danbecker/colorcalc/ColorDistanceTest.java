package info.danbecker.colorcalc;

import java.awt.Color;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorDistanceTest {	
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger( ColorDistanceTest.class);
	
	public static final double TOLERANCE = 0.01;
	
	@BeforeEach
    public void setup() {
	}
	
	@Test
    public void testDistance() {
		Color DANGRAY = ColorUtils.toColor( "#7f7f7f" );		
		
		// distance weighted
		ColorDistance weighted = new ColorDistanceRGBWeighted();
		assertEquals( Double.MAX_VALUE, weighted.distance( DANGRAY, null ), TOLERANCE);
		assertEquals( Double.MAX_VALUE, weighted.distance( null, DANGRAY ), TOLERANCE);
		assertEquals( 380.92, weighted.distance( DANGRAY, Color.BLACK ), TOLERANCE);

		// distance euclidean
        ColorDistance euclidean = new ColorDistanceRGBEuclidean();
		assertEquals( Double.MAX_VALUE, euclidean.distance( DANGRAY, null ), TOLERANCE);
		assertEquals( Double.MAX_VALUE, euclidean.distance( null, DANGRAY ), TOLERANCE);
		assertEquals( 219.97, euclidean.distance( DANGRAY, Color.BLACK ), TOLERANCE);
		
	}
}
