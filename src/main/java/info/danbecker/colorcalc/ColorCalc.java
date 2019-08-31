package info.danbecker.colorcalc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import java.awt.Color;

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
 * Example command line "java ColorCalc -i file1.txt,C:\\Users\\dan\\file2.txt -o output.txt"
 * -i  C:\\Users\\dan\\ArmyPainterDictionary.txt
 * -o C:\\Users\\dan\\output.txt
 * -d C:\\Users\\dan\\PrimarySecondaryTertiaryDictionary.txt
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
	public static final String ESC = new String( new byte [] { 0x1B } );
    
	// input options
    protected static String[] ins;
    protected static String out;
    protected static String[] dicts;
    protected static String[] sorts;
    protected static String[] headers;

    // program data
    protected static Map<Color,List<String>> colorNames = new HashMap<>(); 
    protected static String[] dictionaryHeaders;
    protected static BufferedWriter writer;

	public static void main(String [] args) throws Exception {
		LOGGER.info("ColorCalc args=" + Arrays.toString(args));
		// Parse command line options
		parseGatherOptions(args);
		
		// Open output file.
		if ( null != out) {
			LOGGER.info( "output=" + out);
			writer =  new BufferedWriter(new FileWriter(out));
		}
		
		// Add colors to dictionary 
		if ( null != dicts) {
			for ( String dict: dicts) {
				LOGGER.info( "dictionary=" + Paths.get(dict).toString());
				try (Stream<String> stream = Files.lines(Paths.get(dict))) {
					stream.forEach(line-> {
						addToDictionary( colorNames, dictionaryHeaders, line);
					});
				}
				LOGGER.info( "dictionary size=" + colorNames.size());
			}
		}
		
		// Iterate over given input files.
		if ( null != ins ) {
			for ( String in: ins) {
				LOGGER.info( "input=" + Paths.get(in).toString());
				try (Stream<String> stream = Files.lines(Paths.get(in))) {
					stream.forEach(line-> {
						try {
							if ( line.startsWith("#")) {
								LOGGER.debug("comment=" + line);
								writer.write( line + NL);
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
									LOGGER.debug("data=" + Arrays.toString(data));
									Color color = ColorUtils.toColor( data[ arrayPosition( headers, Col.RGB.getName() )]);
									if ( null != color ) {
										Entry<Color,List<String>> closest = closestColor(colorNames, color );
										if ( null != closest ) {
											String name = data[ arrayPosition( headers, Col.NAME.getName() )];
											LOGGER.info( "Color " + name + ", color=" + ColorUtils.toRGB( color ) + " is closest to color " 
											   + closest.getValue().toString() + ", color=" + ColorUtils.toRGB( closest.getKey()));											
										}
									}
								}
								scanner.close();
							}
						} catch( IOException e) {
							LOGGER.error( "write exception", e);
						}
					});
				}
			}
		}
		
		if ( null != writer ) {
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
	}
	
	/** Create dictionary from given files */
	public static void addToDictionary(Map<Color, List<String>> colorNames, String[] localDictionaryHeaders, String line) {
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
					if ( null != colorNames && null != name) {
						List<String> names = colorNames.get(color);
						if ( null == names ) {
							names = new LinkedList<>();
							names.add(  name );
						} else {
							if ( !names.contains(name)) {
								names.add(  name );
							}
						}
						colorNames.put(color,names);
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
	 * @param colorNames
	 * @param color
	 * @return dictionary entry or null for none found.
	 */
	public static Entry<Color,List<String>> closestColor( Map<Color,List<String>> colorNames, Color color ){
		if ( null == colorNames || null == color) 
			return null;
		
		Set<Entry<Color,List<String>>> colorEntries = colorNames.entrySet();
		double minDist = Double.MAX_VALUE;
		Entry<Color,List<String>>  closest = null;

		for ( Entry<Color,List<String>> entry: colorEntries) {
			Color colorEntry = entry.getKey();
			double distance = ColorUtils.distance( color, colorEntry);
			if ( distance < minDist) {
				minDist = distance;
				closest = entry;
			}			
		}		
		return closest;
	}

}