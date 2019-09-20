package info.danbecker.colorcalc;

import static info.danbecker.colorcalc.HSLColor.HUE_POS;
import static info.danbecker.colorcalc.HSLColor.SAT_POS;
import static info.danbecker.colorcalc.HSLColor.LUM_POS;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * An interactive panel that shows/animates a 3D scatter chart using JXY3D library.
 * <p>
 * The panel can be written to an animated GIF using animateGIF
 *  
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class Visualize extends AbstractAnalysis {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(Visualize.class);
	
	public static final float BLOB_SIZE = 15.0f; // Size of scatter plot blob
	public static final float BLOB_ALPHA = 0.75f; // Transparency of blob
	
	protected List<String[]> data;
	protected String[] cols;
	
    protected Coord3d[] points;
    protected Color[] colors;

	public Visualize( final List<String []> data, final String [] cols ) {
		if ( null != data ) {
			this.data = data;	
			this.cols = cols;
			LOGGER.info( "Visualize data size=" + data.size() );
			LOGGER.info( "Visualize warning: initialization takes time" );
		}
	}

	@Override
    public void init() throws IOException{
		convertData();
        Scatter scatter = new Scatter(points, colors);
        scatter.setWidth(BLOB_SIZE); // default is 1.0
        // chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
        chart = AWTChartComponentFactory.chart(Quality.Nicest, "newt"); // awt, newt, offscreen
	    ChartScene scene = chart.getScene();
        scene.add(scatter);

        IAxeLayout axeLayout = chart.getAxeLayout();
        axeLayout.setXAxeLabel( "Hue/Sat" );
        axeLayout.setYAxeLabel( "Hue/Sat" );
        axeLayout.setZAxeLabel( "Lumi" );

        View view = chart.getView();	        
        // When using polar mode, x reps azimuth (radians), y reps elevation, and z reps range.
        Coord3d viewPoint = new Coord3d( 5.0*Math.PI/4.0, 0.5, 1.5 ); // 0..2PI
        view.setViewPoint(viewPoint);
        
        // Save screenshot
        ICanvas canvas = chart.getCanvas();
        LOGGER.debug( "ICanvas=" + canvas ); // likely org.jzy3d.plot3d.rendering.canvas.CanvasNewtAwt        
    }
	
	/**
	 * Launch panel interactive or staticly with optional size
	 * @param interactive
	 * @param loc
	 * @throws Exception
	 */
	public void launch( boolean interactive, Rectangle loc ) throws Exception {
		org.jzy3d.maths.Rectangle rect = null;
		if ( null != loc ) {
			rect = new org.jzy3d.maths.Rectangle( loc.x, loc.y, loc.width, loc.height );
		}
		
		if ( interactive ) {
			if (null == rect )
				AnalysisLauncher.open(this);
			else 
				AnalysisLauncher.open(this, rect);
		} else {
			// static launch
			if ( null == rect ) 
			    AnalysisLauncher.openStatic(this);
			else 
			    AnalysisLauncher.openStatic(this, rect);			
		}
	}
	
	/**
	 * Move data from ColorCalc domain to jzy3d Scatter chart domain. 
	 */
	protected void convertData() {
		if ( null != data ) {
	        points = new Coord3d[data.size()];
	        colors = new Color[data.size()];

	        for ( int rowi = 0; rowi < data.size(); rowi++ ) {
	        	String[] row = data.get(rowi);
	        	for (int coli = 0; coli< cols.length; coli++) {
					String cell = null;
					if ( coli < row.length) cell = row[ coli ]; // might have empty cols
					
					if (cols[ coli ].equals("RGB") ) {
						java.awt.Color color = ColorUtils.toColor( cell );
						HSLColor hsl = new HSLColor(color); // hue=0.360,sat=0..100,lum-0..100
						// Scale our HSL hue range of 0..360 to 0..2pi
						// Scale our HSL saturation range of 0.0..100.0 to 0.0..1.0
						// Scale our HSL luminance range of 0.0..100.0 to -0.5..0.5
						float hslf[] = hsl.getHSL(); // HSLColor domain
						float hslpf[] = new float[] { hslf[HUE_POS] * (float)Math.PI / 180.0f, 
							hslf[SAT_POS]/100.0f/2.0f, hslf[LUM_POS]/100.0f-0.5f, }; // Jzy3d domain
						// LOGGER.info ("Vis hsl=" + String.format( "%f,%f,%f", hslf[HUE],hslf[SAT],hslf[LUM]));
						// x reps azimuth (radians 0..2pi), y reps elevation angle, and z reps range.
						Coord3d hslp = new Coord3d( hslpf[HUE_POS], Math.atan2(hslpf[LUM_POS], hslpf[SAT_POS]), 
							Math.sqrt(hslpf[LUM_POS]*hslpf[LUM_POS] + hslpf[SAT_POS]*hslpf[SAT_POS])); 
						points[ rowi ] = hslp.cartesian();
						// points[ rowi ] = new Coord3d( color.getRed(), color.getGreen(), color.getBlue() ); // RGB box
						colors[ rowi ] = new Color( color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f, BLOB_ALPHA);
					}
				}
	        }
		}
	}

	/**
	 * Creates an animated GIF of the color scatter plot to the given file name.
	 * @param outputFileName
	 * @param frameCount
	 * @param frameDelay
	 * @param loopContinuously
	 * @throws IOException
	 */
	public void animateGIF( String outputFileName, int frameCount, int frameDelay, boolean loopContinuously ) throws IOException {

		// create a new BufferedOutputStream with the last argument
		ImageOutputStream output = new FileImageOutputStream(new File( outputFileName ));

		// create a gif sequence with the type of the first image, X ms between framps, loop
		GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, frameDelay, loopContinuously);

        // Screenshot can be done with AnalysisLauncher.openStatic, basically
        // Chart chart = demo.getChart();
        // ChartLauncher.openStaticChart(chart, new org.jzy3d.maths.Rectangle( 0, 0, 800, 800), "Testchart"); // empty
        View view = chart.getView();	        
        Coord3d viewPoint;
        		
        // String outputName = "C:\\Users\\dan\\Dropbox\\games\\ArmyPainter\\Scatter%d.png";
        for( int i = 0; i < frameCount; i++ ) {
            viewPoint = new Coord3d( i*2.0*Math.PI/frameCount + Math.PI/4.0, 0.5, 1.5 ); // 0..2PI
            view.setViewPoint(viewPoint);
                        
            // Create a temp file to use the ChartLauncher.screenshot API.
            // The ChartLauncher, Chart, NewtCanvasAWT, TextureWriter write methods
            // were researched and found to be overly complicated.
            File tempFile = File.createTempFile("ColorCalc-", ".png"); // ChartLauncher.screenshot needs png ext.
            tempFile.deleteOnExit();
            LOGGER.info( "Screenshot " + i + " saved in temp filename=" + tempFile.getCanonicalPath());
            
            // Write screenshot to temp file
            ChartLauncher.screenshot(this.getChart(), tempFile.getCanonicalPath());
            
    		// grab the output image type from the first image in the sequence
    		BufferedImage image = ImageIO.read( new File(tempFile.getCanonicalPath()));

    		// write out the image to our sequence
    		writer.writeToSequence(image);
        }

		if ( null != output) {
			output.close();
			// writer.close(); // throws exceptions when using Temp files
		}
	}
}