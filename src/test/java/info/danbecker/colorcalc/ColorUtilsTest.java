package info.danbecker.colorcalc;

import java.awt.Color;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

public class ColorUtilsTest {
	
	public static final Float TOLERANCE = 0.0001f;
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger( ColorUtilsTest.class);
	
	@Before
    public void setup() {
	}
	
	@Test
    public void testConstructors() {
	}
	
	@Test
    public void testStrings() {
		
		Color DANGRAY = ColorUtils.toColor( "#7f7f7fff" );		
		assertEquals( "to Color to String", "7F7F7FFF", ColorUtils.toRGBA( DANGRAY));
	}

}
