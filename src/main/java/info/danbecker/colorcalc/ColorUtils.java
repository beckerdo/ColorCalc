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
	public static final String ESC = new String(new byte[] { 0x1B });

	/** Return an RGB color or null */
	public static Color toColor(String colorString) {
		if (null == colorString)
			return null;
		if (colorString.startsWith("#")) {
			String rgbHex = colorString.substring("#".length());
			return new Color(Integer.parseInt(rgbHex.substring(0, 2), 16), Integer.parseInt(rgbHex.substring(2, 4), 16),
					Integer.parseInt(rgbHex.substring(4), 16), 255);
		}
		return null;
	}

	/** Return an Color as RGB hex string or "null" */
	public static String toRGB(Color color) {
		if (null == color)
			return "null";
		return String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	}

	/** Return an Color as RGBA hex string or "null" */
	public static String toRGBA(Color color) {
		if (null == color)
			return "null";
		return String.format("%s%02X", toRGB(color), color.getAlpha());
	}

	/**
	 * Return an String that will perform ANSI color change string or "null" See
	 * https://en.wikipedia.org/wiki/ANSI_escape_code#24-bit
	 */
	public static String toANSIRGB(Color color) {
		if (null == color)
			return "null";
		return String.format("%s[38;2;%d;%d;%dm", ESC, color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * Return distance between two colors.
	 * 
	 * @param colorString
	 * @return distance of RBG or Integer.MAX_VALUE for nulls
	 */
	public static double distance(Color color1, Color color2) {
		if (null == color1 || null == color2)
			return Double.MAX_VALUE;
		int rd = Math.abs(color1.getRed() - color2.getRed());
		int gd = Math.abs(color1.getGreen() - color2.getGreen());
		int bd = Math.abs(color1.getBlue() - color2.getBlue());
		return Math.sqrt(rd * rd + gd * gd + bd * bd);
	}
}