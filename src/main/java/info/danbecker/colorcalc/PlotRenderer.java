package info.danbecker.colorcalc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Renders graphical representation color data on circular HSL chart
 *  
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class PlotRenderer { 
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(PlotRenderer.class);

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	/** Used for changine color alpha values. */
	public static final int NOT_GHOSTED = -1;
	
	public static final Shape upArrow = createArrow( 8, 8, 30, 270.0, 1.0 ); // 0 right, 90 down, 180 left, 270 up
	public static final Shape downArrow = createArrow( 8, 8, 30, 90.0, 1.0 ); // 0 right, 90 down, 180 left, 270 up
	public static final Shape leftArrow = createArrow( 8, 8, 30, 0.0, 1.0 ); // 0 right, 90 down, 180 left, 270 up
	public static final Shape rightArrow = createArrow( 8, 8, 30, 180.0, 1.0 ); // 0 right, 90 down, 180 left, 270 up
 
	protected BufferedImage bufferedImage;

	public PlotRenderer( Dimension size ) {
		setSize( size );
	}
	
	public void setSize( Dimension size ) {
		LOGGER.info( "Plot size=" + size);
	    bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);		
	}

    /** Writes the image to the given fileName. */
	public static String writeImage(BufferedImage image, String fileName ) 
		throws IOException {
		ImageIO.write(image, "png", new File( fileName  ));
		return fileName ;
	}

	// Returns a generated image.
	public static BufferedImage renderImage(Dimension size, List<String[]> data, String[] colNames) {
		if (( null == size) || (size.width < 1) || (size.height < 1))
			return null;
    	// System.out.println( "RasterRenderer.renderImage size=" + size );

		// Init graphics
		PlotRenderer rr = new PlotRenderer( size );
	    Graphics2D g2d = rr.bufferedImage.createGraphics();
	    g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
	    g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
	    
	    // Make image background.
	    Color transparent = new Color(0x00FFFF00, true);
	    g2d.setColor( transparent ); // can have alpha
	    g2d.fillRect(0, 0, size.width, size.height);
	    
	    // Make plot background.
	    int borderWidth = 4;
	    g2d.setStroke( new BasicStroke ( borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
	    Color plotBackground = ColorUtils.changeAlpha( Color.BLACK, 2 * 255 / 4 ); // ghosted gray 
    	g2d.setColor( plotBackground );
		g2d.fillOval( borderWidth/2, borderWidth/2, size.width - borderWidth, size.height - borderWidth); // x, y, width, height
	    Color boundary = Color.BLACK;
    	g2d.setColor( boundary );
		g2d.drawOval( borderWidth/2, borderWidth/2, size.width - borderWidth, size.height - borderWidth); // x, y, width, height

		// Draw line (center to noon)
		// g2d.drawLine( size.width/2, 0, size.width/2, size.height/2);

		// Draw text
		// String text = "Hello";
		// Font font = new Font( "SansSerif", Font.BOLD, 36 );
		// g2d.setFont(font);
		// TextLayout layout = new TextLayout( text, font, g2d.getFontRenderContext() );
		// Rectangle2D stringBounds = layout.getBounds();
		// g2d.drawString( text, size.width/2 - (int) stringBounds.getWidth()/2, size.height/2 + (int)stringBounds.getHeight()/2);
	    
		// Draw colors
	    g2d.setStroke( new BasicStroke ( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
		// int lineWidth = 2;
		int sampleWidth = size.width / 50;
		// int namePosition = ColorCalc.arrayPosition(colNames, Col.NAME.getName());
		int hslPosition = ColorCalc.arrayPosition(colNames, Col.HSL.getName());
		if ( -1 == hslPosition ) {
			LOGGER.warn( "No HSL column in " + colNames );
			return null;
		}
		Color [] basics = new Color[]{
			ColorUtils.toColor("#FF0000"), 
			ColorUtils.toColor("#FF7F00"), 
			ColorUtils.toColor("#FFFF00"), 
			ColorUtils.toColor("#7FFF00"), 
			ColorUtils.toColor("#00FF00"), 
			ColorUtils.toColor("#00FF7F"), 
			ColorUtils.toColor("#00FFFF"),
			ColorUtils.toColor("#007FFF"), 
			ColorUtils.toColor("#0000FF"), 
			ColorUtils.toColor("#7F00FF"), 
			ColorUtils.toColor("#FF00FF"),
			ColorUtils.toColor("#FF007F"),
			ColorUtils.toColor("#000000"),
			ColorUtils.toColor("#FFFFFF") 
		};
		for ( Color color : basics ) {
			HSLColor hslColor = new HSLColor( color );
			Point point = pointFromHSL( size, borderWidth, hslColor );
			// LOGGER.debug( "HSL=" + hslColor.toString() + ", rgb=" + hslColor.getRGB() +  ", xy=" + point);
			g2d.setColor(hslColor.getRGB());
			g2d.fillOval( point.x, point.y, sampleWidth, sampleWidth); // x, y, width, height
			g2d.setColor( ColorUtils.luminanceGray(hslColor) );
			g2d.drawOval( point.x, point.y, sampleWidth, sampleWidth); // x, y, width, height
		}
		
		for ( String[] row: data ) {
			for ( String colName: colNames ) {
				switch ( colName ) {
				case "HSL": {
					HSLColor hslColor = HSLColor.fromString( row[ hslPosition ] );
					// LOGGER.info(  "rowString=" + row[hslPosition]+ ", HSL=" + hslColor.toString() );
					Point point = pointFromHSL( size, borderWidth, hslColor );					
					g2d.setColor(hslColor.getRGB());
					g2d.fillOval( point.x, point.y, sampleWidth, sampleWidth); // x, y, width, height
					g2d.setColor( ColorUtils.luminanceGray(hslColor) );
					g2d.drawOval( point.x, point.y, sampleWidth, sampleWidth); // x, y, width, height
					break;
				}
				}
			}			
		}
		
	    // Graphics context no longer needed so dispose it
	    g2d.dispose();

	    return rr.bufferedImage;
	}    

	public final static Point pointFromHSL( Dimension size, int borderWidth, HSLColor hslColor ) {
		if ( null == size ) 
			throw new IllegalArgumentException( "Size=" + size );
		if ( null == hslColor) 
			throw new IllegalArgumentException( "HSL=" +  hslColor );
			
		int min = Math.min( size.width, size.height);
		int radius = min/2 - borderWidth;
		Point center = new Point( size.width/2, size.height/2);
		// Cartesian based on 0-360,0-100
		double [] cart = toCartesian(hslColor);
		return new Point( (int) Math.round((cart[0] * radius / 100.0) + center.x - 2*borderWidth), (int)Math.round((cart[ 1 ] * radius / 100.0) + center.y - 2*borderWidth));
	}

	/** Converts a cartesian point to polar point. */
    public static final Point toPolar( Point cartesian ) {
    	double r     = Math.sqrt(cartesian.x*cartesian.x + cartesian.y*cartesian.y);
    	double theta = Math.atan2(cartesian.y, cartesian.x);
    	return new Point( (int) Math.round(r), (int) Math.round(theta) );
    }
    
    /** Convert a polar HSL hue (angle) and saturation (radius) to Cartesian. */
    public final static double [] toCartesian( HSLColor hslColor ) {
        double radiant = hslColor.getHue() * (Math.PI/180);
        
        return new double[] { 
        	hslColor.getSaturation() * Math.cos(radiant),
        	hslColor.getSaturation() * Math.sin(radiant)
        };
    }
	
    public final static Shape translate( Shape shape, int x, int y ) {
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		return at.createTransformedShape(shape);
    }
    
    /** Draw a fancy gradient color bar from one of the colorSets. */
    public final static void paintGradient( Graphics2D g2, boolean vertical, boolean right,
    	final Point fretMinStringMin, final Point fretMaxStringMax, final Color [] colors ) {

        if ( vertical ) {
            int h = fretMaxStringMax.y - fretMinStringMin.y;
        	// Draw a number of gradients equal to the number of colors in the set.
            int w = fretMaxStringMax.x - fretMinStringMin.x;
        	float sliceWidth = w / (colors.length - 1);        

        	if ( right ) {
        		for( int i = 0; i < colors.length - 1; i++ ) {
        			g2.setPaint( new GradientPaint( fretMinStringMin.x + sliceWidth * i, 		0, colors[ i ], 
        											fretMinStringMin.x + sliceWidth * ( i + 1 ), 0, colors[ i + 1 ]));
        			g2.fillRect( fretMinStringMin.x + ((int) sliceWidth * i),   	  fretMinStringMin.y,  
        						 (int) sliceWidth,                                    h );        	
        		}        		
        	}
        }
    }
        
	/** Create an arrow shape. */
    public static Shape createArrow( int length, int barb, double barbAngleDegrees, double rotateDegrees, double scale ) {
        double barbAngle = Math.toRadians( barbAngleDegrees );
        Path2D.Double path = new Path2D.Double();
        path.moveTo(-length/2, 0);
        path.lineTo(length/2, 0);
        double x = length/2 - barb*Math.cos(barbAngle);
        double y = barb*Math.sin(barbAngle);
        path.lineTo(x, y);
        x = length/2 - barb*Math.cos(-barbAngle);
        y = barb*Math.sin(-barbAngle);
        path.moveTo(length/2, 0);
        path.lineTo(x, y);

        // Rotate and scale
        double rotateRadians = Math.toRadians( rotateDegrees );
		// AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		AffineTransform at = AffineTransform.getRotateInstance(rotateRadians);  
		at.scale( scale, scale );
		Shape shape = at.createTransformedShape(path);
        return shape;
    }    
}