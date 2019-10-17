package info.danbecker.colorcalc;

import java.awt.Color;

/**
 * ColorUtils
 * <p>
 * Some color methods that Java Color does not include.
 * 
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class ColorUtils {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(ColorUtils.class);
	
	public static final String ESC = new String(new byte[] { 0x1B });

	/** Return an RGB or RGBA color or null from a given string in hexadecimal.
	 *  Examples are "#ff7f3f", "808080ff"
	 */
	public static Color toColor(String colorString) {
		if (null == colorString)
			return null;
		colorString = colorString.trim();
		if (colorString.startsWith("#")) {
			colorString = colorString.substring("#".length());
		}
		// LOGGER.info( "toColor string=" + colorString );
		if ( 6 == colorString.length()) {
			return new Color(Integer.parseInt(colorString.substring(0, 2), 16), Integer.parseInt(colorString.substring(2, 4), 16),
				Integer.parseInt(colorString.substring(4), 16), 255);
		} else if ( 8 == colorString.length()) {
			return new Color(Integer.parseInt(colorString.substring(0, 2), 16), Integer.parseInt(colorString.substring(2, 4), 16),
					Integer.parseInt(colorString.substring(4, 6), 16), Integer.parseInt(colorString.substring(6), 16));			
		} else {
			throw new IllegalArgumentException( "could not handle color string " + colorString);
		}
	}

	/** Return a hexadecimal String of RGB from a given color. Example "ff7f3f". */
	public static String toRGB(Color color) {
		if (null == color)
			return null;
		return String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	}

    /** Return a hexadecimal String of RGBA from a given color. Example "ff7f3fff". */
	public static String toRGBA(Color color) {
		if (null == color)
			return null;
		return String.format("%s%02X", toRGB(color), color.getAlpha());
	}

	/**
	 * Return an String that will perform ANSI color change (in many terminals and editors).
	 * See https://en.wikipedia.org/wiki/ANSI_escape_code#24-bit
	 * 
	 * @param color
	 * @param foreground or background change 
	 * @return ANSI color change string
	 */
	public static String toANSIRGB(Color color, boolean foreground) {
		if (null == color)
			return null;
		if ( foreground )
			return String.format("%s[38;2;%d;%d;%dm", ESC, color.getRed(), color.getGreen(), color.getBlue());
		else
			return String.format("%s[48;2;%d;%d;%dm", ESC, color.getRed(), color.getGreen(), color.getBlue());
	}

    /** Changes color to given alpha value. */
    public static Color changeAlpha( Color color, int alpha ) {
       Color newColor = new Color ( color.getRed(), color.getGreen(), color.getBlue(), alpha );
       return newColor;
    }

    /** Returns grayed value of given color. */
    public final static Color luminanceGray( HSLColor hslColor ) {
        // float mono = (0.2125f * color.getRed()) + (0.7154f * color.getGreen()) + (0.0721f * color.getBlue());
		int lum255 = Math.round(hslColor.getLuminance() * 255.0f / 100f);
        return new Color( lum255, lum255, lum255 );
    }
}