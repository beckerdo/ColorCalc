package info.danbecker.colorcalc;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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

	// public final static java.awt.Color [] BASICS = null;
	public final static String [] BASICS = new String[] {
		"#FF0000", 
		"#FF7F00", 
		"#FFFF00", 
		"#7FFF00", 
		"#00FF00", 
		"#00FF7F", 
		"#00FFFF",
		"#007FFF", 
		"#0000FF", 
		"#7F00FF", 
		"#FF00FF",
		"#FF007F",
		"#000000",
		"#FFFFFF" 
	};
	

	public Visualize( final List<String []> data, final String [] cols ) {
		if ( null != data ) {
			this.data = data;	
			this.cols = cols;

			// Add BASICS to data
			if ( null != cols && null != BASICS ) {
				for ( int coli = 0; coli < cols.length; coli++) {
					if (cols[ coli ].equals("RGB") ) {
						for ( int basici = 0; basici < BASICS.length; basici++) {
							// java.awt.Color color = ColorUtils.toColor( BASICS[ basici] );
							List<String> fakeRow = new LinkedList<String>();
							for ( int y = 0; y < coli; y++)
								fakeRow.add( "" );
							fakeRow.add( BASICS[ basici ]);
							for ( int y = basici+1; y < cols.length; y++ )
								fakeRow.add( "" );
							data.add( fakeRow.toArray( new String[ 0 ] ) );
						}
					}					
				}
			}
		}
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
		
        // Screenshot can be done with AnalysisLauncher.openStatic, basically
        // Chart chart = demo.getChart();
        // ChartLauncher.openStaticChart(chart, new org.jzy3d.maths.Rectangle( 0, 0, 800, 800), "Testchart"); // empty

        // LOGGER.info( "TextureData size=" + tData.getWidth() + "," + tData.getHeight());
        // Data = canvas.screenshot( new File("C:\\Users\\dan\\Dropbox\\games\\ArmyPainter\\Scatter.png"));        
        ChartLauncher.screenshot(this.getChart(), "C:\\Users\\dan\\Dropbox\\games\\ArmyPainter\\Scatter.png"); // screenshot not available
	}
	
	protected void convertData() {
		if ( null != data ) {
			LOGGER.info( "Visualize data size=" + data.size());
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
						// Coord3d hsl = new Coord3d( hslColor.getHue() * Math.PI / 180.0, hslColor.getLuminance(), hslColor.getSaturation()); // x reps azimuth (radians 0..2pi), y reps elevation, and z reps range.
						// Coord3d hslp = new Coord3d( hsl.getHue() * Math.PI / 180.0, Math.atan2(hsl.getLuminance(), hsl.getSaturation()), hsl.getSaturation()); // x reps azimuth (radians 0..2pi), y reps elevation, and z reps range.
						Coord3d hslp = new Coord3d( hsl.getHue() * Math.PI / 180.0, Math.atan2(hsl.getLuminance(), hsl.getSaturation()), 
						   Math.sqrt(hsl.getLuminance()*hsl.getLuminance() + hsl.getSaturation()*hsl.getSaturation())); // x reps azimuth (radians 0..2pi), y reps elevation, and z reps range.
						points[ rowi ] = hslp.cartesian();
						// points[ rowi ] = new Coord3d( color.getRed(), color.getGreen(), color.getBlue() ); // RGB box
						colors[ rowi ] = new Color( color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f, BLOB_ALPHA);
					}
				}
	        }
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
        LOGGER.info( "ICanvas=" + canvas );
        
    }
}