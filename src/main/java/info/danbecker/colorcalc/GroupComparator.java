package info.danbecker.colorcalc;

import java.util.Arrays;
import java.util.Comparator;

/**
 * A comparator to sort rows my broad groups such as Saturation < 020.
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
public class GroupComparator implements Comparator<String[]> {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(GroupComparator.class);
	
	protected String[] colHeadings;
	protected String[] sorts;

	protected int[] colNums;
	protected String[] comparison;
	protected String[] value;
	
	protected GroupComparator() {
	}
	
	public GroupComparator( final String [] colHeadings, final String[] sorts) {
		this.colHeadings = colHeadings;
		this.sorts = sorts;
		
		// Create structures that point to the correct columns, ascending or descending.
		colNums = new int[sorts.length];
		comparison = new String[sorts.length];
		value = new String[sorts.length];
		
		int sortIndex = 0;
		for ( String sort : sorts ) {
			// Determine sort direction
			comparison[ sortIndex ] = sort;
			if ( null != sorts[ sortIndex ]) {				
				int location;
				if (-1 != (location = sorts[sortIndex].indexOf(RelOp.LT.getSymbol()))) {
					comparison[ sortIndex ] = RelOp.LT.getSymbol();
				} else if (-1 != (location = sorts[sortIndex].indexOf(RelOp.LE.getSymbol()))) {
					comparison[ sortIndex ] = RelOp.LE.getSymbol();
				} else if (-1 != (location = sorts[sortIndex].indexOf(RelOp.GT.getSymbol()))) {
					comparison[ sortIndex ] = RelOp.GT.getSymbol();
				} else if (-1 != (location = sorts[sortIndex].indexOf(RelOp.GE.getSymbol()))) {
					comparison[ sortIndex ] = RelOp.GE.getSymbol();
				} else if (-1 != (location = sorts[sortIndex].indexOf(RelOp.EQ.getSymbol()))) {
					comparison[ sortIndex ] = RelOp.EQ.getSymbol();
				} else if (-1 != (location = sorts[sortIndex].indexOf(RelOp.NE.getSymbol()))) {
					comparison[ sortIndex ] = RelOp.NE.getSymbol();
				} 
				if ( -1 != location ) {
					value[ sortIndex ] =  sort.substring( location + 2 );
					sort = sort.substring(0, location);
				}
			}
			// Determine column index in data
			colNums[ sortIndex ]  = ColorCalc.arrayPosition( colHeadings, sort );
			if ( -1 == colNums[ sortIndex ]  ) { 
				LOGGER.warn( "Group sort (" + sortIndex + ") " + sort + " does not appear in " + Arrays.toString(colHeadings));
			}
			sortIndex++;
		}
	}
	
    @Override
    public int compare(String[] row1, String[] row2) {
       // Compare row/col data based on the previously calculated column numbers.
		for ( int sortIndex = 0; sortIndex < colNums.length; sortIndex++ ) {
			if ( -1 != colNums[ sortIndex ]) {
				boolean row1Meets = meets( row1[ colNums[ sortIndex ] ], comparison[sortIndex], value[sortIndex]);
		    	// LOGGER.info( "Row 1  data=" + row1[ colNums[ sortIndex ] ] + ", comparison=" + comparison[sortIndex] + ", value=" + value[sortIndex] + ", inGroup=" + row1Meets);
				boolean row2Meets = meets( row2[ colNums[ sortIndex ] ], comparison[sortIndex], value[sortIndex]);
		    	// LOGGER.info( "Row 2  data=" + row2[ colNums[ sortIndex ] ] + ", comparison=" + comparison[sortIndex] + ", value=" + value[sortIndex] + ", inGroup=" + row2Meets);
				if ( row1Meets && row2Meets ) {
					return 0;
				} else if ( !row1Meets && !row2Meets ) {
					return 0;					
				} else if ( !row1Meets ) {
					return -1;
				} else if ( !row2Meets ) {
					return 1;
				}
			}
		}		
	    return 0;
    }
    
    public boolean meets( String data, String comparison, String value ) {
    	if ( null == data || null == comparison ) {
    		return false;
    	}
    	
    	RelOp relOp = RelOp.fromSymbol(comparison);
    	int compare = data.compareTo( value );
    	switch ( relOp ) {
    	case LT: {
    		if ( compare < 0 ) {
    			return true;    		
    		}
    		break;
    	}
    	case LE: {
    		if ( compare <= 0 ) {
    			return true;    		
    		}
    		break;
    	}
    	case GT: {
    		if ( compare >= 0 ) {
    			return true;    		
    		}
    		break;
    	}
    	case GE: {
    		if ( compare >= 0 ) {
    			return true;    		
    		}
    		break;
    	}
    	case EQ: {
    		if ( 0 == compare ) {
    			return true;    		
    		}
    		break;
    	}
    	case NE: {
    		if ( 0 != compare ) {
    			return true;    		
    		}
    		break;
    	}
    	}
    	return false;
    }
}