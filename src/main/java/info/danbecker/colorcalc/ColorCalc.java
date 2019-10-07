package info.danbecker.colorcalc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * ColorCalc
 * <p>
 * A tool for calculating color values, sorting, grouping, and naming colors
 * <p>
 * <pre>
 * Example command line "java ColorCalc -i file1.txt,C:\\Users\\dan\\file2.txt -o output.txt"
 * -i BasicTones.txt,BasicGrays.txt
 * -o output.txt
 * -d BasicSats.txt
 * -c Name,RGB,HSL,S,Dict-Name,Dict-RGB,Dict-HSL
 * -s Dict-H--,Name
 * -t
 * -g S<<013 
 * -r "Colors grouped by major color, low saturation moved to end"
 * -p C:\\colorPlot.png
 * -v
 * -vn colorVisualization.gif
 * -vs 64
 * -vd 0
 * </pre>
 * <p>
 * The sort will sort colors by dictionary hues (descending) and color name (ascending).
 * The low saturation colors (S<<013) are grouped to the end.
 * <P>
 * TODO
 * Multiple input files with different column positioning will fail.
 * 
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class ColorCalc {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(ColorCalc.class);

    public static final String CMD_DELIM = "\\s*,\\s*"; // 0* whitespace, comma, 0* whitespace
	public static final String NL = System.getProperty("line.separator");
	// Delimit data by two or more white space, tabs, commons.
	public static final String WORD_DELIM = "[\\s]{2,}|\t|,";
	public static final String PREFIX_DELIM = "-";
    
	// input options
    protected static String[] ins;
    protected static String out;
    protected static String[] dicts;
    protected static String[] sorts;
    protected static String[] groups;
    protected static String[] cols;
    protected static boolean table;
    protected static String comment;
    protected static String plotName;
    protected static boolean visualize;
    protected static String visualizationName;
    protected static int vSteps = 0;
    protected static int vDelay = 0;

    // program data
    protected static Map<Color,List<String>> dictionaryNames = new HashMap<>(); 
    protected static String[] headers;
    protected static String[] dictionaryHeaders;
    protected static BufferedWriter writer;
    protected static List<String[]> outputData = new LinkedList<>(); 

	public static void main(String [] args) throws Exception {
		LOGGER.info("ColorCalc args=" + Arrays.toString(args));
		// Parse command line options
		parseGatherOptions(args);
		
		// Open output file.
		if ( null != out) {
			if ( table ) {
				if ( out.endsWith(".txt")) {
					out = out.substring(0, out.length() - 4 );
					out = out + ".html";
				}
			}
			LOGGER.info( "output=" + out);
			writer =  new BufferedWriter(new FileWriter(out));
		}
		
		// Add colors to dictionary 
		if ( null != dicts) {
			for ( String dict: dicts) {
				LOGGER.info( "dictionary=" + Path.of(dict).toAbsolutePath().toString()); // Path.of preferred to Paths.get
				try (Stream<String> stream = Files.lines(Path.of(dict).toAbsolutePath())) {
					stream.forEach(line-> {
						addToDictionary( dictionaryNames, dictionaryHeaders, line);
					});
				}
				LOGGER.info( "dictionary size=" + dictionaryNames.size());
			}
		}
		
		// Iterate over given input files.
		final int[] inputLineCount = new int[] {0}; // Use final for anonymous scope.
		if ( null != ins ) {
			for ( String in: ins) {
				LOGGER.info( "input=" + Path.of(in).toAbsolutePath().toString()); // Path.of preferred to Paths.get
				try (Stream<String> stream = Files.lines(Path.of(in).toAbsolutePath())) {
					stream.forEach(line-> {
						try {
							if ( line.startsWith("#")) {
								LOGGER.debug("comment=" + line);
								if (!table) {
									writer.write( line + NL);
									if ( 0 == inputLineCount[0] ) {
										writer.write( "# " + comment + NL);
									}
								} else {
									if ( 0 == inputLineCount[0] ) {
										HTMLUtils.start( writer, line );
										if ( null != comment ) {
											HTMLUtils.comment(writer, comment);											
										}
									} else {
										HTMLUtils.comment(writer, line);
									}
								}
							} else {
								// Process line
								@SuppressWarnings("resource")
								Scanner scanner = new Scanner(line).useDelimiter(WORD_DELIM);
								if ( null == headers ) {
									headers = scanner.tokens().toArray(String[]::new);
									LOGGER.info("header=" + Arrays.toString(headers));
								} else {
									// data line
									String [] data = scanner.tokens().toArray(String[]::new);
									if ( -1 != arrayPosition( data, "Name") || -1 != arrayPosition( data, "RGB" ) ) {
										// This is likely a subsequent "header" line in input file 2..n
										LOGGER.debug("discarded data=" + Arrays.toString(data));
									} else {
										LOGGER.debug("data=" + Arrays.toString(data));
										populateOutputData(outputData, dictionaryNames, cols, headers, dictionaryHeaders, data);										
									}
								}
								scanner.close();
							}
							inputLineCount[0]++;
						} catch( IOException e) {
							LOGGER.error( "write exception", e);
						}
					});
				}
			}
		}
		
		// Sort output data
		if ( null != sorts ) {
			sortData( outputData, new ColorFieldComparator( cols, sorts ) );
		}
		// Move groups to end of data.
		if ( null != groups ) {
			sortData( outputData, new GroupComparator( cols, groups ) );
		}

		// Draw a pretty picture
		if ( null != plotName ) {
			PlotRenderer.writeImage( PlotRenderer.renderImage( new Dimension( 800, 800 ), outputData, cols ), plotName );
		}

		// Put all output data to file
		outputData( outputData, cols, writer );

		// An interactive panel that shows/animates a 3D scatter chart using JXY3D library.
		if ( visualize ) {
			visualizeData( outputData, cols, visualizationName, vSteps, vDelay );
		}
		
		if ( null != writer ) {
			if ( table ) {
				HTMLUtils.end( writer );
			}
			writer.close();
		}
	}

	/** Command line options for this application. */
	public static void parseGatherOptions(String[] args) throws ParseException, IOException {
		// Parse the command line arguments
		Options options = new Options();
		// Use dash with shortcut (-h) or -- with name (--help).
        options.addOption("h", "help", false, "print the command line options");
        options.addOption("i", "ins", true, "list of comma-separated input files");
        options.addOption("d", "dicts", true, "list of comma-separated dictionary files for comparisons");
        options.addOption("o", "out", true, "generated output file with results");
        options.addOption("s", "sorts", true, "column sort fields (followed by + or - for ascending, descending)");
        options.addOption("g", "groups", true, "column sort fields ");
        options.addOption("c", "cols", true, "column output fields");
        options.addOption("t", "table", false, "output a colorful HTML table"); // switch option
        options.addOption("p", "plot", true, "output a plot of the colors to the given file");
        options.addOption("r", "comment", true, "output file comment (remark)");
        options.addOption("v", "visualize", false, "open interactive window with 3D plot"); // switch option
        options.addOption("vn", " visual name", true, "output file name of visual animation");
        options.addOption("vs", " visual steps", true, "number of steps in visual animation");
        options.addOption("vd", " visual delay", true, "millisecond delay between animation steps (0 for none)");

		CommandLineParser cliParser = new DefaultParser();
		CommandLine line = cliParser.parse(options, args);

		// Gather command line arguments for execution
		if (line.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar colorcalc.jar <options> info.danbecker.colorcalc.ColorCalc",
					options);
			System.exit(0);
		}
        // Gather command line arguments for execution
        if (line.hasOption("i")) {
            String option = line.getOptionValue("ins");
            ins = option.split(CMD_DELIM);
            LOGGER.info("ins=" + Arrays.toString( ins ));
        }
        if (line.hasOption("o")) {
            out = line.getOptionValue("out");
            LOGGER.info("out=" + out );
        }
        if (line.hasOption("d")) {
            String option = line.getOptionValue("dicts");
            dicts = option.split(CMD_DELIM);
            LOGGER.info("dicts=" + Arrays.toString( dicts ));
        }
        if (line.hasOption("s")) {
            String option = line.getOptionValue("sorts");
            sorts = option.split(CMD_DELIM);
            LOGGER.info("sorts=" + Arrays.toString( sorts ));
        }
        if (line.hasOption("g")) {
            String option = line.getOptionValue("groups");
            groups = option.split(CMD_DELIM);
            LOGGER.info("groups=" + Arrays.toString( groups ));
        }
        if (line.hasOption("c")) {
            String option = line.getOptionValue("cols");
            cols = option.split(CMD_DELIM);
            LOGGER.info("cols=" + Arrays.toString( cols ));
        }
        if (line.hasOption("t")) {
            table = true;
            LOGGER.info("table=" + table );
        }
        if (line.hasOption("r")) {
            comment = line.getOptionValue("comment");
            LOGGER.info("comment=" + comment );
        }
        if (line.hasOption("p")) {
            plotName = line.getOptionValue("plot");
            LOGGER.info("plot=" + plotName );
        }
        if (line.hasOption("v")) {
            visualize = true;
            LOGGER.info("visualize=" + visualize );
        }
        if (line.hasOption("vn")) {
            visualizationName = line.getOptionValue("vn");
            LOGGER.info("vis name=" + visualizationName );
        }
        if (line.hasOption("vs")) {
            vSteps = Integer.parseInt(line.getOptionValue("vs"));
            LOGGER.info("vis steps=" + vSteps );
        }
        if (line.hasOption("vd")) {
            vDelay = Integer.parseInt(line.getOptionValue("vd"));
            LOGGER.info("vis delay=" + vDelay );
        }
	}
	
	/** Add dictionary line as a header or data line. */
	public static void addToDictionary(Map<Color, List<String>> dictionaryNames, String[] localDictionaryHeaders, String line) {
		if (line.startsWith("#")) {
			LOGGER.debug("dictionary comment=" + line);
		} else {
			// Process line
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(line).useDelimiter(WORD_DELIM);
			if (null == dictionaryHeaders) {
				dictionaryHeaders = scanner.tokens().toArray(String[]::new);
				LOGGER.info("dictionary header=" + Arrays.toString(dictionaryHeaders));
			} else {
				// data line
				String[] data = scanner.tokens().toArray(String[]::new);
				LOGGER.debug( "dictionary data=" + Arrays.toString(data) );
				int colorIndex = arrayPosition( dictionaryHeaders, Col.RGB.getName() );
				int nameIndex = arrayPosition( dictionaryHeaders, Col.NAME.getName() );
				if (-1 != colorIndex &&  -1 != nameIndex) {
					Color color = ColorUtils.toColor( data[ colorIndex ]);
					String name = data[ nameIndex ];
					LOGGER.debug( "dictionary color=" + color.toString() + ", name=" + name );
					if ( null != dictionaryNames && null != name) {
						List<String> names = dictionaryNames.get(color);
						if ( null == names ) {
							names = new LinkedList<>();
							names.add( name );
						} else {
							if ( !names.contains(name)) {
								names.add( name );
							}
						}
						dictionaryNames.put(color,names);
					}
				}
			}
			scanner.close();
		}
	}
	
	/** Returns position of searchString in strings
	 * @param strings
	 * @param searchString
	 * @return array position or -1 if not found
	 */
	public static int arrayPosition( String [] strings, String searchString ) {
		if ( null == strings) 
			return -1;
		int index = 0;
		for ( String test : strings) {
			if ( test.equals( searchString )) 
				return index;
			index++;
		}
		return -1;
	}
	
	/**
	 * Return the closest color in the dictionary.
	 * @param dictionaryNames
	 * @param color
	 * @return dictionary entry or null for none found.
	 */
	public static Entry<Color,List<String>> closestColor( Map<Color,List<String>> dictionaryNames, Color color ){
		if ( null == dictionaryNames || null == color) 
			return null;
		
		Set<Entry<Color,List<String>>> colorEntries = dictionaryNames.entrySet();
		double minDist = Double.MAX_VALUE;
		Entry<Color,List<String>>  closest = null;

		for ( Entry<Color,List<String>> entry: colorEntries) {
			Color colorEntry = entry.getKey();
			double distance = ColorUtils.distanceEuclidean( color, colorEntry);
			// double distance = ColorUtils.distanceWeighted( color, colorEntry);
			if ( distance < minDist) {
				minDist = distance;
				closest = entry;
			}			
		}		
		return closest;
	}

	/** Take the given data line and populate the columns of the output file
	 *  from the dictionary and calculations 
	 *  Example column names  "Name,RGB,HSL,Dict-Name,Dict-RGB,Dict-HSL"
*/
	public static void populateOutputData(List<String[]> outputData, Map<Color,List<String>> dictionaryNames,
			String[] cols, String[] headers, String[] dictionaryHeaders, String[] data) {

		// Need to have basic info to make an output line.
		Color color = ColorUtils.toColor( data[ arrayPosition( headers, Col.RGB.getName() )]);
		if ( null == color ) {
			throw new IllegalArgumentException( "missing color on data row " + Arrays.deepToString( data ));
		}
		String name = data[arrayPosition(headers, Col.NAME.getName())];		
		
		String[] outputRow = new String[cols.length];		
		int colIndex = 0;
		StringBuilder loggerInfo = new StringBuilder();
		for ( String col : cols ) {
			String prefix = "";
			int delimLoc = col.indexOf(PREFIX_DELIM);
			if ( -1 != delimLoc ) {
				// Column name has a prefix;
				prefix = col.substring( 0, delimLoc );
				col = col.substring( delimLoc + 1);
				// LOGGER.debug( "Col name=" + col + ", delimLoc=" + delimLoc + ", prefix=" + prefix);
			}
			switch ( col ) {
				case "Name" : {
					if ( "".equals( prefix ) || "Input".equals( prefix )) {
						outputRow[ colIndex ] = name;
					} else if ( "Dict".equals( prefix )) {
						Entry<Color, List<String>> closest = closestColor(dictionaryNames, color);
						if (null != closest) {
							outputRow[ colIndex ] = closest.getValue().toString();
						}
					}
					if ( loggerInfo.length() > 0) loggerInfo.append( ", " );
					if ( "".equals( prefix ))
						loggerInfo.append( col + "=" + outputRow[ colIndex ]);
					else
						loggerInfo.append( prefix + PREFIX_DELIM + col + "=" + outputRow[ colIndex ]);
					break;					
				}			
				case "RGB" : 
				case "R": case "G": case "B": {					
					if ( "".equals( prefix ) || "Input".equals( prefix )) {
						// Normalize output (might get rid of #)
						outputRow[ colIndex ] = ColorUtils.toRGB(ColorUtils.toColor(data[arrayPosition(headers, Col.RGB.getName())]));
					} else if ( "Dict".equals( prefix )) {
						Entry<Color, List<String>> closest = closestColor(dictionaryNames, color);
						if (null != closest) {
							outputRow[ colIndex ] = ColorUtils.toRGB(closest.getKey());
						}
					}
					switch ( col ) {
						case "R": outputRow[ colIndex ] = outputRow[ colIndex ].substring(0,2); break;
						case "G": outputRow[ colIndex ] = outputRow[ colIndex ].substring(2,4); break;
						case "B": outputRow[ colIndex ] = outputRow[ colIndex ].substring(4); break;
					}
					if ( loggerInfo.length() > 0) loggerInfo.append( ", " );
					loggerInfo.append( col + "=" + outputRow[ colIndex ]);
					break;					
				}
				case "HSL" : 
				case "H" :	case "S" :	case "L" : { 
					if ( "".equals( prefix ) || "Input".equals( prefix )) {
						int position = arrayPosition(headers, Col.HSL.getName());
						if ( -1 != position ) {
							// Should normalize output, but what is the String representation of HSL?
							outputRow[ colIndex ] = data[ position ];							
							// LOGGER.info( "HSL in=" + outputRow[ colIndex ]);
						} else {
							position = arrayPosition(headers, Col.RGB.getName());
							if ( -1 != position ) {
								// HSL calculated from RGB
								outputRow[ colIndex ] = HSLColor.toString( ColorUtils.toColor(data[arrayPosition(headers, Col.RGB.getName())]) );
							}
							// LOGGER.info( "HSL using RGB=" + outputRow[ colIndex ]);
						}
					} else if ( "Dict".equals( prefix )) {
						Entry<Color, List<String>> closest = closestColor(dictionaryNames, color);
						if (null != closest) {
							outputRow[ colIndex ] = HSLColor.toString(closest.getKey());
						}
						// LOGGER.info( "HSL using dictionary=" + outputRow[ colIndex ]);
					}
						
					switch ( col ) {
						case "H": outputRow[ colIndex ] = outputRow[ colIndex ].substring(0,3); break;
						case "S": outputRow[ colIndex ] = outputRow[ colIndex ].substring(3,6); break;
						case "L": outputRow[ colIndex ] = outputRow[ colIndex ].substring(6); break;
					}
					// if ( col.equals("H") || col.equals("S") || col.equals("L") )
					// 	LOGGER.info( "HSL using H S or L=" + outputRow[ colIndex ] );
					if ( loggerInfo.length() > 0) loggerInfo.append( ", " );
					if ( col.equals("H") || col.equals("S") || col.equals("L") ) {
						loggerInfo.append( col + "=" + outputRow[ colIndex ] ); 						
					} else {
						loggerInfo.append( col + "=" + outputRow[ colIndex ] + 
							", RGB'=" + ColorUtils.toRGB(HSLColor.fromString(outputRow[ colIndex ]).getRGB())); // append derived RGB						
					}
					break;					
				}
				default: {
					// Copy other columns without processing
					int position = arrayPosition(headers, col );
					if ( -1 != position ) {
						if ( position >= data.length ) {
							// Might be the last column with no data.
							outputRow[ colIndex ] = null;
						} else {
							outputRow[ colIndex ] = data[ position ];
						}
					} else {
						LOGGER.error( "Unknown column name " + col );
					}
				}
			}
            colIndex++;
		}
		outputData.add( outputRow );
		LOGGER.info( loggerInfo.toString() );
	}

	// Sort data according to the names columns
	public static void sortData( List<String[]> outputData, Comparator<String[]> colorFieldComparator) {
		 Collections.sort(outputData,  colorFieldComparator );
	}
	
	/** Output data to file. 
	 * TODO: Support spacing/tabbing to make nice columns in text file
	 */
	public static void outputData( List<String[]> outputData, String[] cols, BufferedWriter writer) throws IOException{
//		int nameCol = arrayPosition(cols, Col.NAME.getName());
//		int longestName = -1;
//		if ( -1 != nameCol ) {
//			longestName = longestString( outputData, nameCol );
//		}
//		int dictNameCol = arrayPosition(cols, Col.DICT + PREFIX_DELIM + Col.NAME.getName());
//		int longestDictName = -1;
//		if ( -1 != dictNameCol ) {
//			longestDictName = longestString( outputData, dictNameCol );
//		}
//
//		// Build a format string
//		String format = "%-40s%s%s%s";
		// Output column names
		if (!table) {
			for (int i = 0; i < cols.length; i++) {
				if (i > 0) writer.write(",");
				writer.write(cols[i]);
			}
			writer.write(NL);
		} else {
			HTMLUtils.header(writer, cols);
		}
		// Output column data
		if (!table) {
			for (String[] data : outputData) {
				for (int i = 0; i < data.length; i++) {
					if (i > 0)	writer.write(",");
					if (null != data[i]) {
						writer.write(data[i]);
					}
				}
				writer.write(NL);
			}
		} else {
			for (String[] data : outputData) {
				HTMLUtils.data(writer, cols, data);
			}
		}
	}
	
	/** Given rows of String data, determine the longest string in the given column */
	public static int longestString( List<String[]> outputData, int col ) {
		int longest = 0;
		for ( String[] row : outputData ) {
			String data = row[ col ];
			if ( null != data && data.length() > longest)
				longest = data.length();
		}
		return longest;
	}
	
	/**
	 * An interactive panel that shows/animates a 3D scatter chart using JXY3D library.
	 * 
	 * @param outputData
	 * @param cols
	 */
	public static void visualizeData( final List<String[]> data, final String [] cols, String vName, int vSteps, int vDelay ) throws Exception {
		Visualize visualize = new Visualize( data, cols );
		visualize.launch( true, new Rectangle( 200, 200, 1000, 800) ); // launch interactive or static with given size
		
		// Save animation to file
		if (null != vName) {
			LOGGER.info( "Visualization animation name=" + vName + ", steps=" + vSteps + ", delay=" + vDelay);
					
			// Multiple fileName#.png made into fileName.gif
			visualize.animateGIF( vName, vSteps, vDelay, true );			
			LOGGER.info( "Visualization animation name=" + vName + ", steps=" + vSteps + ", completed." );
		}
	}
}