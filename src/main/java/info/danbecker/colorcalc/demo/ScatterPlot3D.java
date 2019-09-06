/* ===================
 * Orson Charts - Demo
 * ===================
 * 
 * Copyright (c) 2013-2017, Object Refinery Limited.
 * All rights reserved.
 *
 * http://www.object-refinery.com/orsoncharts/index.html
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the Object Refinery Limited nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL OBJECT REFINERY LIMITED BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Note that the above terms apply to the demo source only, and not the 
 * Orson Charts library.
 * 
 */

package info.danbecker.colorcalc.demo;

import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import info.danbecker.colorcalc.ColorUtils;
import info.danbecker.colorcalc.HSLColor;

//import com.orsoncharts.demo.DemoPanel;
//import com.orsoncharts.demo.ExitOnClose;
//import com.orsoncharts.demo.OrsonChartsDemo;
//import com.orsoncharts.Chart3DPanel;
//import com.orsoncharts.Chart3D;
//import com.orsoncharts.Chart3DFactory;
//import com.orsoncharts.data.xyz.XYZDataset;
//import com.orsoncharts.data.xyz.XYZSeries;
//import com.orsoncharts.data.xyz.XYZSeriesCollection;
//import com.orsoncharts.graphics3d.Dimension3D;
//import com.orsoncharts.graphics3d.ViewPoint3D;
//import com.orsoncharts.graphics3d.swing.DisplayPanel3D;
//import com.orsoncharts.plot.XYZPlot;
//import com.orsoncharts.renderer.xyz.ScatterXYZRenderer;


/**
 * A demonstration of scatter plot Using Orson Charts and JFree
 * <p>
 * Needs the following in POM
 * 
 * 		<dependency>
 *           <groupId>com.object-refinery</groupId>
 *           <artifactId>orsoncharts</artifactId>
 *           <version>1.7</version>
 *       </dependency>
 *       <dependency>
 *   		<artifactId>jfree-demos</artifactId>
 *   		<groupId>org.jfree</groupId>
 *   		<version>1.2</version>
 *       </dependency>
 * 
 */
@SuppressWarnings("serial")
public class ScatterPlot3D extends JFrame {

    /**
     * Creates a new test app.
     *
     * @param title  the frame title.
     */
    public ScatterPlot3D(String title) {
        super(title);
//        addWindowListener(new ExitOnClose());
        getContentPane().add(createDemoPanel());
    }

    /**
     * Returns a panel containing the content for the demo.  This method is
     * used across all the individual demo applications to allow aggregation 
     * into a single "umbrella" demo (OrsonChartsDemo).
     * 
     * @return A panel containing the content for the demo.
     */
    public static JPanel createDemoPanel() {
//        DemoPanel content = new DemoPanel(new BorderLayout());
//        content.setPreferredSize(OrsonChartsDemo.DEFAULT_CONTENT_SIZE);
//        
//        XYZDataset<String> dataset = createDataset();
//        Chart3D chart = createChart(dataset);
//        Chart3DPanel chartPanel = new Chart3DPanel(chart);
//        content.setChartPanel(chartPanel);
//        chartPanel.zoomToFit(OrsonChartsDemo.DEFAULT_CONTENT_SIZE);
//        content.add(new DisplayPanel3D(chartPanel));
//        return content;
    	return null;
    }
    
//   private static Chart3D createChart(XYZDataset<String> dataset) {
//        Chart3D chart = Chart3DFactory.createScatterChart("Scatter Plot 3D", 
//                null, dataset, "Red", "Green", "Blue");
//        XYZPlot plot = (XYZPlot) chart.getPlot();
//        // plot.setDimensions(new Dimension3D(10, 5, 10));
//        plot.setDimensions(new Dimension3D(10, 10, 10));
//
//        ScatterXYZRenderer renderer = (ScatterXYZRenderer) plot.getRenderer();
//        renderer.setSize(0.2);
//        renderer.setColors(new Color(255, 128, 128), new Color(128, 255, 128));
//               
//        // chart.setViewPoint(ViewPoint3D.createAboveLeftViewPoint(40));
//        // chart.setViewPoint(ViewPoint3D.createAboveViewPoint(80));
//        return chart;
//   }

    /**
     * Creates a sample dataset (hard-coded for the purpose of keeping the
     * demo self-contained - in practice you would normally read your data
     * from a file, database or other source).
     * 
     * @return A sample dataset.
     */
//    public static XYZDataset<String> createDataset() {
//        XYZSeries<String> s1 = new XYZSeries<String>("Color");
//		Color [] basics = new Color[]{
//				ColorUtils.toColor("#FF0000"), 
//				ColorUtils.toColor("#FF7F00"), 
//				ColorUtils.toColor("#FFFF00"), 
//				ColorUtils.toColor("#7FFF00"), 
//				ColorUtils.toColor("#00FF00"), 
//				ColorUtils.toColor("#00FF7F"), 
//				ColorUtils.toColor("#00FFFF"),
//				ColorUtils.toColor("#007FFF"), 
//				ColorUtils.toColor("#0000FF"), 
//				ColorUtils.toColor("#7F00FF"), 
//				ColorUtils.toColor("#FF00FF"),
//				ColorUtils.toColor("#FF007F"),
//				ColorUtils.toColor("#000000"),
//				ColorUtils.toColor("#FFFFFF") 
//			};
//		for ( Color color : basics ) {
//			// HSLColor hslColor = new HSLColor( color );
//			s1.add(color.getRed() / 255.0, color.getGreen() / 255.0,color.getBlue() / 255.0);
//		}
//		// Collection supports multiple series.
//        XYZSeriesCollection<String> dataset = new XYZSeriesCollection<String>();
//        dataset.add(s1);
//        return dataset;
//    	return null;
//    }
   
    /**
     * Starting point for the app.
     *
     * @param args  command line arguments (ignored).
     */
    public static void main(String[] args) {
        ScatterPlot3D app = new ScatterPlot3D("ColorCalc Scatter Plot 3D");
        app.pack();
        app.setVisible(true);
    }
}