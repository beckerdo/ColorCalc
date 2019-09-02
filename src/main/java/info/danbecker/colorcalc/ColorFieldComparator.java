package info.danbecker.colorcalc;

import java.util.Arrays;
import java.util.Comparator;

/**
 * A comparator to sort rows organized as String arrays.
 * <p>
 * On construction of this comparator, the constructor is handed
 * <ul>
 * <li>String [] colHeadings - The names of columns in the data rows 
 * <li>String [] sorts - The names of columns  to sort on (with optional ascending and descending suffixes 
 * </ul>
 * <p>
 * When compare is called, the sort
 *  
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class ColorFieldComparator implements Comparator<String[]> {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(ColorFieldComparator.class);
	
	public final static String SORT_ASCENDING = "++";
	public final static String SORT_DESCENDING = "--";

	protected String[] colHeadings;
	protected String[] sorts;

	protected int[] colNums;
	protected boolean[] ascending;
	
	protected ColorFieldComparator() {
	}
	
	public ColorFieldComparator( final String [] colHeadings, final String[] sorts) {
		this.colHeadings = colHeadings;
		this.sorts = sorts;
		
		// Create structures that point to the correct columns, ascending or descending.
		colNums = new int[sorts.length];
		ascending = new boolean[sorts.length];
		
		int colIndex = 0;
		for ( String sort : sorts ) {
			// Determine sort direction
			ascending[ colIndex ] = true;
			if ( null != sorts[ colIndex ]) {				
				int ascendingLocation = sorts[colIndex].indexOf(SORT_ASCENDING);
				if (-1 != ascendingLocation ) {
					ascending[ colIndex ] = true;
					sort = sort.substring(0, ascendingLocation);
				} 
				int descendingLocation = sorts[colIndex].indexOf(SORT_DESCENDING);
				if (-1 != descendingLocation ) {
					ascending[ colIndex ] = false;
					sort = sort.substring(0, descendingLocation);
				} 
			}
			// Determine column index in data
			colNums[ colIndex ]  = ColorCalc.arrayPosition( colHeadings, sort );
			if ( -1 == colNums[ colIndex ]  ) { 
				LOGGER.warn( "Sort column (" + colIndex + ") " + sort + " does not appear in " + Arrays.toString(colHeadings));
			}
			colIndex++;
		}
	}
	
    @Override
    public int compare(String[] row1, String[] row2) {
       // Compare row/col data based on the previously calculated column numbers.
		for ( int colIndex = 0; colIndex < colNums.length; colIndex++ ) {
			if ( -1 != colNums[ colIndex ]) {
				if ( null != row1[ colNums[ colIndex ]  ]) {
					int result = 0;
					if ( ascending[ colIndex ]) {
						result = row1[ colNums[ colIndex ]  ].compareTo( row2[ colNums[ colIndex ]  ]);
					} else {
						result = -row1[ colNums[ colIndex ]  ].compareTo( row2[ colNums[ colIndex ]  ]);
					}
					if ( 0 != result ) {
						return result; // no need to compare subsequent columns
					}
				}
			}
		}
		
	    return 0;
    }
}