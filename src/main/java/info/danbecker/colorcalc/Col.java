package info.danbecker.colorcalc;

/** Common names for columns in the color world. */
public enum Col {	
	NAME( "Name", "n" ),
	RGB( "RGB", "rgb" ),
	HSL( "HSL", "hsl" ),
	TYPE( "Type", "type" ),
	INPUT( "Input", "i" ),
	OUTPUT( "Output", "i" ),
	DICTIONARY( "Dictionary", "d" );

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
        		case "rbg": colName = Col.RGB; break;
        		case "hsl": colName = Col.HSL; break;
        		case "type": colName = Col.TYPE; break;
        		case "i": colName = Col.INPUT; break;
        		case "o": colName = Col.OUTPUT; break;
        		case "d": colName = Col.DICTIONARY; break;
            }        	
        }
        return colName;
	}
}
