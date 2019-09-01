package info.danbecker.colorcalc;

/** Common names for columns in the color world. */
public enum Col {	
	NAME( "Name", "n" ),
	RGB( "RGB", "rgb" ),
	R( "Red", "r" ),
	G( "Green", "g" ),
	B( "Blue", "g" ),
	HSL( "HSL", "hsl" ),
	H( "Hue", "h" ),
	S( "Sat", "s" ),
	L( "Light", "l" ),
	TYPE( "Type", "type" ),
	SORT( "Sort", "sort" ),
	COLS( "Cols", "cols" ),
	INPUT( "Input", "i" ),
	OUTPUT( "Output", "i" ),
	DICT( "Dict", "d" );

	String name;
	String abbreviation;
	
	private Col(String name, String abbreviation ) {
    	this.name = name;
    	this.abbreviation = abbreviation;
    }
	public String getName() {
		return name; 
	}
	public String getAbbreviation() {
		return abbreviation; 
	}
	
	/** Returns an enum based on the given shortcut or abbreviation. */
	public static Col fromAbbreviation( String typeStr ) {
		Col colName = null;
        if ( null != typeStr ) {
            switch ( typeStr.toLowerCase() ) {
        		case "n": colName = Col.NAME; break;
        		case "rgb": colName = Col.RGB; break;
        		case "r": colName = Col.R; break;
        		case "g": colName = Col.G; break;
        		case "b": colName = Col.B; break;
        		case "hsl": colName = Col.HSL; break;
        		case "h": colName = Col.H; break;
        		case "s": colName = Col.S; break;
        		case "l": colName = Col.L; break;
        		case "type": colName = Col.TYPE; break;
        		case "sort": colName = Col.SORT; break;
        		case "cols": colName = Col.COLS; break;
        		case "i": colName = Col.INPUT; break;
        		case "o": colName = Col.OUTPUT; break;
        		case "d": colName = Col.DICT; break;
            }        	
        }
        return colName;
	}
}
