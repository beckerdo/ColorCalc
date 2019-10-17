package info.danbecker.colorcalc;

import java.awt.Color;

/**
 * Return distance between two colors.
 * <p>
 * RGB distance from the formula discussed in
 * https://en.wikipedia.org/wiki/Color_difference#Euclidean
 * 
 * @param colorString
 * @return distance of RBG or Integer.MAX_VALUE for nulls
 */
public class ColorDistanceRGBWeighted implements ColorDistance {

    @Override
    public double distance(Color color1, Color color2) {
        if (null == color1 || null == color2)
            return Double.MAX_VALUE;
        double rbar = ( color1.getRed() + color2.getRed() ) / 2.0;
        // Squared distances
        int ΔR2 =  ( color1.getRed() - color2.getRed() ) * ( color1.getRed() - color2.getRed() );
        int ΔG2 =  ( color1.getGreen() - color2.getGreen() ) * ( color1.getGreen() - color2.getGreen() );
        int ΔB2 =  ( color1.getBlue() - color2.getBlue() ) * ( color1.getBlue() - color2.getBlue() );
        return Math.sqrt((2.0 + rbar/256.0) * ΔR2 + 4.0 * ΔG2 + (2.0 + (255.0-rbar)/256.0) * ΔB2 );
    }

}
