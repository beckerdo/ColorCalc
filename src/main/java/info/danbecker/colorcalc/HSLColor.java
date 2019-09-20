package info.danbecker.colorcalc;

import java.awt.Color;

/**
 * The HSLColor class provides methods to manipulate HSL (Hue, Saturation
 * Luminance) values to create a corresponding Color object using the RGB
 * ColorSpace.
 * <p>
 * The HUE is the color (0=red,60=yellow,120=green,180=cyan,240=blue,300=magenta),
 * the Saturation is the purity of the color (0=gray, 100=pure),
 * and Luminance is the brightness of the color (0=black and 100=white).
 * <p>
 * The Hue is specified as an angle between 0 - 360 degrees where red is 0,
 * green is 120 and blue is 240. In between you have the colors of the rainbow.
 * Saturation is specified as a percentage between 0 - 100 where 100 is fully
 * saturated and 0 approaches gray. Luminance is specified as a percentage
 * between 0 - 100 where 0 is black and 100 is white.
 * <p>
 * In particular the HSL color space makes it easier change the Tone or Shade of
 * a color by adjusting the luminance value.
 * <p>
 * The Color instance data rgb must be maintained by constructors
 * to ensure the hash and compareTo methods work.
 */
public class HSLColor implements Comparable<HSLColor>{

	/** Tolerance for float compares */
	public static final float TOLERANCE = 0.0001f;

	/** Positions in returned for HSLA arrays */
	public static final int HUE_POS = 0;
	public static final int SAT_POS = 1;
	public static final int LUM_POS = 2;
	public static final int ALPHA_POS = 3;
	
	private Color rgb;
	private float[] hsl;
	private float alpha;

	/**
	 * Create a HSLColor object using an RGB Color object.
	 *
	 * @param rgb the RGB Color object
	 */
	public HSLColor(Color rgb) {
		this.rgb = rgb;
		hsl = fromRGB(rgb);
		alpha = rgb.getAlpha() / 255.0f;
	}

	/**
	 * Create a HSLColor object using individual HSL values and a default alpha
	 * value of 1.0.
	 *
	 * @param h is the Hue value in degrees between 0 - 360
	 * @param s is the Saturation percentage between 0 - 100
	 * @param l is the Luminance percentage between 0 - 100
	 */
	public HSLColor(float h, float s, float l) {
		this(h, s, l, 1.0f);
	}

	/**
	 * Create a HSLColor object using individual HSL values.
	 *
	 * @param h     the Hue value in degrees between 0 - 360
	 * @param s     the Saturation percentage between 0 - 100
	 * @param l     the Luminance percentage between 0 - 100
	 * @param alpha the alpha value between 0 - 1
	 */
	public HSLColor(float h, float s, float l, float alpha) {
		hsl = new float[] { h, s, l };
		this.alpha = alpha;
		rgb = toRGB(hsl, alpha);
	}

	/**
	 * Create a HSLColor object using an an array containing the individual HSL
	 * values and with a default alpha value of 1.
	 *
	 * @param hsl array containing HSL values
	 */
	public HSLColor(float[] hsl) {
		this(hsl, 1.0f);
	}

	/**
	 * Create a HSLColor object using an an array containing the individual HSL
	 * values.
	 *
	 * @param hsl   array containing HSL values
	 * @param alpha the alpha value between 0 - 1
	 */
	public HSLColor(float[] hsl, float alpha) {
		this.hsl = hsl;
		this.alpha = alpha;
		rgb = toRGB(hsl, alpha);
	}

	/**
	 * Create a RGB Color object based on this HSLColor with a different Hue value.
	 * The degrees specified is an absolute value.
	 *
	 * @param degrees - the Hue value between 0 - 360
	 * @return the RGB Color object
	 */
	public Color adjustHue(float degrees) {
		return toRGB(degrees, hsl[SAT_POS], hsl[LUM_POS], alpha);
	}

	/**
	 * Create a RGB Color object based on this HSLColor with a different Luminance
	 * value. The percent specified is an absolute value.
	 *
	 * @param percent - the Luminance value between 0 - 100
	 * @return the RGB Color object
	 */
	public Color adjustLuminance(float percent) {
		return toRGB(hsl[HUE_POS], hsl[SAT_POS], percent, alpha);
	}

	/**
	 * Create a RGB Color object based on this HSLColor with a different Saturation
	 * value. The percent specified is an absolute value.
	 *
	 * @param percent - the Saturation value between 0 - 100
	 * @return the RGB Color object
	 */
	public Color adjustSaturation(float percent) {
		return toRGB(hsl[HUE_POS], percent, hsl[LUM_POS], alpha);
	}

	/**
	 * Create a RGB Color object based on this HSLColor with a different Shade.
	 * Changing the shade will return a darker color. The percent specified is a
	 * relative value.
	 *
	 * @param percent - the value between 0 - 100
	 * @return the RGB Color object
	 */
	public Color adjustShade(float percent) {
		float multiplier = (100.0f - percent) / 100.0f;
		float l = Math.max(0.0f, hsl[LUM_POS] * multiplier);

		return toRGB(hsl[HUE_POS], hsl[SAT_POS], l, alpha);
	}

	/**
	 * Create a RGB Color object based on this HSLColor with a different Tone.
	 * Changing the tone will return a lighter color. The percent specified is a
	 * relative value.
	 *
	 * @param percent - the value between 0 - 100
	 * @return the RGB Color object
	 */
	public Color adjustTone(float percent) {
		float multiplier = (100.0f + percent) / 100.0f;
		float l = Math.min(100.0f, hsl[LUM_POS] * multiplier);

		return toRGB(hsl[HUE_POS], hsl[SAT_POS], l, alpha);
	}

	/**
	 * Create a RGB Color object that is the complementary color of this HSLColor.
	 * This is a convenience method. The complementary color is determined by adding
	 * 180 degrees to the Hue value.
	 * 
	 * @return the RGB Color object
	 */
	public Color getComplementary() {
		float hue = (hsl[HUE_POS] + 180.0f) % 360.0f;
		return toRGB(hue, hsl[SAT_POS], hsl[LUM_POS]);
	}

	/**
	 * Get the HSL values.
	 *
	 * @return the HSL values.
	 */
	public float[] getHSL() {
		return hsl;
	}

	/**
	 * Get the HSLA values.
	 *
	 * @return the HSLA values.
	 */
	public float[] getHSLA() {
		return new float[] { hsl[HUE_POS], hsl[SAT_POS], hsl[LUM_POS], alpha };
	}

	/**
	 * Get the RGB Color object represented by this HDLColor.
	 *
	 * @return the RGB Color object.
	 */
	public Color getRGB() {
		return rgb;
	}

	/**
	 * Get the Hue value.
	 *
	 * @return the Hue value.
	 */
	public float getHue() {
		return hsl[HUE_POS];
	}

	/**
	 * Get the Luminance value.
	 *
	 * @return the Luminance value.
	 */
	public float getLuminance() {
		return hsl[LUM_POS];
	}

	/**
	 * Get the Saturation value.
	 *
	 * @return the Saturation value.
	 */
	public float getSaturation() {
		return hsl[SAT_POS];
	}

	/**
	 * Get the Alpha value.
	 *
	 * @return the Alpha value.
	 */
	public float getAlpha() {
		return alpha;
	}

	/**
	 * Convert a RGB Color to it corresponding HSL values.
	 *
	 * @return an array containing the 3 HSL values.
	 */
	public static float[] fromRGB(Color color) {
		// Get RGB values in the range 0 - 1

		float[] rgb = color.getRGBColorComponents(null);
		float r = rgb[0];
		float g = rgb[1];
		float b = rgb[2];

		// Minimum and Maximum RGB values are used in the HSL calculations
		float min = Math.min(r, Math.min(g, b));
		float max = Math.max(r, Math.max(g, b));

		// Calculate the Hue
		float h = 0;
		if (max == min)
			h = 0;
		else if (max == r)
			h = ((60 * (g - b) / (max - min)) + 360) % 360;
		else if (max == g)
			h = (60 * (b - r) / (max - min)) + 120;
		else if (max == b)
			h = (60 * (r - g) / (max - min)) + 240;

		// Calculate the Luminance
		float l = (max + min) / 2;

		// Calculate the Saturation
		float s = 0;
		if (max == min)
			s = 0;
		else if (l <= .5f)
			s = (max - min) / (max + min);
		else
			s = (max - min) / (2 - max - min);

		return new float[] { h, s * 100, l * 100 };
	}

	/**
	 * Convert HSL values to a RGB Color with a default alpha value of 1. H (Hue) is
	 * specified as degrees in the range 0 - 360. S (Saturation) is specified as a
	 * percentage in the range 1 - 100. L (Luminance) is specified as a percentage
	 * in the range 1 - 100.
	 *
	 * @param hsl an array containing the 3 HSL values
	 *
	 * @returns the RGB Color object
	 */
	public static Color toRGB(float[] hsl) {
		return toRGB(hsl, 1.0f);
	}

	/**
	 * Convert HSL values to a RGB Color. H (Hue) is specified as degrees in the
	 * range 0 - 360. S (Saturation) is specified as a percentage in the range 1 -
	 * 100. L (Luminance) is specified as a percentage in the range 1 - 100.
	 *
	 * @param hsl   an array containing the 3 HSL values
	 * @param alpha the alpha value between 0 - 1
	 *
	 * @returns the RGB Color object
	 */
	public static Color toRGB(float[] hsl, float alpha) {
		return toRGB(hsl[HUE_POS], hsl[SAT_POS], hsl[LUM_POS], alpha);
	}

	/**
	 * Convert HSL values to a RGB Color with a default alpha value of 1.
	 *
	 * @param h Hue is specified as degrees in the range 0 - 360.
	 * @param s Saturation is specified as a percentage in the range 1 - 100.
	 * @param l Lumanance is specified as a percentage in the range 1 - 100.
	 *
	 * @returns the RGB Color object
	 */
	public static Color toRGB(float h, float s, float l) {
		return toRGB(h, s, l, 1.0f);
	}

	/**
	 * Convert HSL values to a RGB Color.
	 *
	 * @param h     Hue is specified as degrees in the range 0 - 360.
	 * @param s     Saturation is specified as a percentage in the range 1 - 100.
	 * @param l     Lumanance is specified as a percentage in the range 1 - 100.
	 * @param alpha the alpha value between 0 - 1
	 *
	 * @returns the RGB Color object
	 */
	public static Color toRGB(float h, float s, float l, float alpha) {
		if (s < 0.0f || s > 100.0f) {
			String message = "Color parameter outside of expected range - Saturation";
			throw new IllegalArgumentException(message);
		}

		if (l < 0.0f || l > 100.0f) {
			String message = "Color parameter outside of expected range - Luminance";
			throw new IllegalArgumentException(message);
		}

		if (alpha < 0.0f || alpha > 1.0f) {
			String message = "Color parameter outside of expected range - Alpha";
			throw new IllegalArgumentException(message);
		}

		// Formula needs all values between 0 - 1.

		h = h % 360.0f;
		h /= 360f;
		s /= 100f;
		l /= 100f;

		float q = 0;

		if (l < 0.5)
			q = l * (1 + s);
		else
			q = (l + s) - (s * l);

		float p = 2 * l - q;

		float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
		float g = Math.max(0, HueToRGB(p, q, h));
		float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

		r = Math.min(r, 1.0f);
		g = Math.min(g, 1.0f);
		b = Math.min(b, 1.0f);

		return new Color(r, g, b, alpha);
	}

	private static float HueToRGB(float p, float q, float h) {
		if (h < 0)
			h += 1;

		if (h > 1)
			h -= 1;

		if (6 * h < 1) {
			return p + ((q - p) * 6 * h);
		}

		if (2 * h < 1) {
			return q;
		}

		if (3 * h < 2) {
			return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
		}

		return p;
	}

	/** 
	 * Parse HSL from String of the form HHHSSSLLL where
	 * HHH is hue in range 0..360
	 * SSS is saturation in range 0..100
	 * LLL is luminance in range 0..100
	 * @param nineDigits
	 * @return
	 */
	public static final HSLColor fromString(String nineDigits) {
		if ( null == nineDigits || nineDigits.length() < 9 ) {
			throw new IllegalArgumentException ( "String \"" + "\" should be of form HHHSSSLLL" );
		}
		return new HSLColor( 
			Integer.parseInt( nineDigits.substring(0,3)),
			Integer.parseInt( nineDigits.substring(3,6)),
			Integer.parseInt( nineDigits.substring(6))
		);
	}

    // Object methods
    public static int compare(final HSLColor hsl1, final HSLColor hsl2) {
    	// Problems if hsla floats bits are NaN. Avoid casts and floatToRawIntBits
    	// Compare with self
    	if ( hsl1 == hsl2 ) {
    		return 0;
    	}
    	// Compare with nulls
    	if ( null == hsl2) {
    		return 1;
    	}
    	if ( null == hsl1) {
    		return -1;
    	}
    	
    	// Always ensure constructors and method maintain rgb.    	
        return hsl1.rgb.getRGB() - hsl2.rgb.getRGB();
    }

	@Override
	public int compareTo(HSLColor hsl) {
		return compare( this, hsl );
	}

    @Override
    public int hashCode() {
    	// Always ensure constructors and method maintain rgb.
        return rgb.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        // Compare with self   
        if (obj == this) { 
            return true; 
        } 
  
        // Compare with class type
        if (!(obj instanceof HSLColor)) { 
            return false; 
        } 
          
        // Cast to same type  
        HSLColor hsl = (HSLColor) obj; 
          
        // Compare data by compare method
        return 0 == compare( this, hsl );
    }

	@Override
	public String toString() {
		String toString = "HSLColor[h=" + hsl[HUE_POS] + ",s=" + hsl[SAT_POS] + ",l=" + hsl[LUM_POS] + ",alpha=" + alpha + "]";
		return toString;
	}

	/**
	 * Convert a RGB Color to it corresponding HSL value string.
	 * The string is of the form "012345678" where:
	 * 012 = hue (0-360)
	 * 345 = sat (0-100
	 * 678 = luminance (0-100)
	 * The numbers are zero padded to always be 3 digits.
	 * 
	 * @return a string containing the 3 HSL values.
	 */
	public static String toString(Color color) {
		float [] hsl = HSLColor.fromRGB( color );
		return String.format("%03d%03d%03d", 
		   Float.valueOf(hsl[HUE_POS]).intValue(), Float.valueOf(hsl[SAT_POS]).intValue(), Float.valueOf(hsl[LUM_POS]).intValue() );
	}
}
