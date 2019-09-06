package info.danbecker.colorcalc.demo;

import java.util.Random;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * A demonstration of a 3D scatter chart using JXY3D library.
 *  
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class ScatterDemo extends AbstractAnalysis {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(ScatterDemo.class);
	
	public static void main(String[] args) throws Exception {
		AnalysisLauncher.open(new ScatterDemo());
	}
		
	@Override
    public void init(){
        // int size = 500000;
        int dataSize = 1000;
        
        Coord3d[] points = new Coord3d[dataSize];
        Color[]   colors = new Color[dataSize];
        
        Random r = new Random();
        r.setSeed(0);
        
        for(int i=0; i<dataSize; i++){
            float x = r.nextFloat() - 0.5f;
            float y = r.nextFloat() - 0.5f;
            float z = r.nextFloat() - 0.5f;
            points[i] = new Coord3d(x, y, z);
            // a = 0.25f;
            float a = 0.75f;
            colors[i] = new Color(x, y, z, a);
        }
        
        Scatter scatter = new Scatter(points, colors);
        scatter.setWidth(8.0f); // default is 1.0
        // chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
        chart = AWTChartComponentFactory.chart(Quality.Nicest, "newt"); // awt, newt, offscreen
        View view = chart.getView();	        
        LOGGER.info( "View 1=" + view.getViewPoint().toString() );
        // When using polar mode, x reps azimuth (radians), y reps elevation, and z reps range.
        Coord3d viewPoint = new Coord3d( 2.0*Math.PI/8.0, 0.5, 1.5 );
        view.setViewPoint(viewPoint);

	    ChartScene scene = chart.getScene();
        scene.add(scatter);

        LOGGER.info( "View 2=" + view.getViewPoint().toString() );
    }
}