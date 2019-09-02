package info.danbecker.colorcalc;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;


/**
 * HTMLUtils
 * <p>
 * Utils for writing Color data as an HTML table.
 * 
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class HTMLUtils {
	public static final String NL = System.getProperty("line.separator");
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(HTMLUtils.class);

	public static void start( Writer writer, String line ) throws IOException {
		writer.write("<!DOCTYPE html>");
		writer.write(NL);
		writer.write("<html>");
		writer.write(NL);
		writer.write("<body>");
		writer.write(NL);

		writer.write("<h2>" + line + "</h2>");
		writer.write(NL);

		writer.write("<table style=\"width:100%\">");
		writer.write(NL);
	}

	public static void comment( Writer writer, String line ) throws IOException {
		writer.write("<tr><td colspan=\"100\">" + line + "</td></tr>");
		writer.write(NL);
	}
	public static void header( Writer writer, String [] cols )  throws IOException {
		writer.write("<tr>");
		for ( String col: cols) {
			writer.write("<th style=\"text-align: left;\">" + col + "</td>");
		}
		writer.write("</tr>");
		writer.write(NL);		
	}
	
	public static void data( Writer writer, String [] cols, String [] data ) throws IOException {
		writer.write("<tr>");		
		for (int i = 0; i< cols.length; i++) {
			String cell = null;
			if ( i < data.length) cell = data[ i ]; // might have empty cols
			cell( writer, cols[ i ], cell);
		}
		writer.write("</tr>");
		writer.write(NL);		
	}

	public static void cell( Writer writer, String col, String data ) throws IOException {
	    // <td style="background-color:#f1f1c1;">Smith</td>
		if (null == data) {
			writer.write("<td></td>");
			return;
		}
		
		if (col.equals("RGB") || col.contains("-RGB")) {
			Color background = ColorUtils.toColor(data);
			// LOGGER.info(  "cell col=" + col + ", data=" + data + ", background=" + ColorUtils.toRGB( background ) );
			Color foreground =  getTextColor( background );
			String style = "style=\"color:#" + ColorUtils.toRGB( foreground ) +
				";background-color:#" + ColorUtils.toRGB( background ) + ";\"";
			writer.write("<td " + style + ">" + data + "</td>");
		} else {
			// Perhaps someday do R, G, B, HSL, H, S, L
			writer.write("<td>" + data + "</td>");
		}
	}

	/** Get a text color that goes well with background
	 * Generally white if Lum < 50 or black if Lum > 50
	 * @param background
	 * @return
	 */
	public static Color getTextColor( Color background ) {
		HSLColor hsl  = new HSLColor( background );
		if ( hsl.getLuminance() < 50.0f) {
			return Color.white;
		}
		return Color.black;
	}
	
	public static void end( Writer writer ) throws IOException {
		writer.write("</table>");
		writer.write(NL);
		writer.write("</body>");
		writer.write(NL);
		writer.write("</html>");
		writer.write(NL);
	}

}