package info.danbecker.colorcalc;

import java.awt.Color;

/**
 * Return distance between two colors.
 * <p>
 * Various distance formulas are discussed in
 * https://en.wikipedia.org/wiki/Color_difference
 * 
 */
public interface ColorDistance {

    /**
     * Distance between two colors.
     * @param color1
     * @param color2
     * @return
     */
    public double distance(Color color1, Color color2);
}
