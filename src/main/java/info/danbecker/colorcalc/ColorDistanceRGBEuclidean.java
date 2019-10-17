package info.danbecker.colorcalc;

import java.awt.Color;

/**
 * Return distance between two colors.
 * <p>
 * RGB distance from the formula discussed in
 * https://en.wikipedia.org/wiki/Color_difference#Euclidean
 */
public class ColorDistanceRGBEuclidean implements ColorDistance {

    @Override
    public double distance(Color color1, Color color2) {
        if (null == color1 || null == color2)
            return Double.MAX_VALUE;
        // Squared distances
        int ΔR2 =  ( color1.getRed() - color2.getRed() ) * ( color1.getRed() - color2.getRed() );
        int ΔG2 =  ( color1.getGreen() - color2.getGreen() ) * ( color1.getGreen() - color2.getGreen() );
        int ΔB2 =  ( color1.getBlue() - color2.getBlue() ) * ( color1.getBlue() - color2.getBlue() );
        return Math.sqrt( ΔR2 + ΔG2 + ΔB2 );
    }

}
