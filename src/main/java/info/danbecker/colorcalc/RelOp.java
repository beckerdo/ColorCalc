package info.danbecker.colorcalc;

/** Relational operators for making comparisons of two operands. */
public enum RelOp {	
	EQ( "equals", "==" ),
	NE( "not equals", "!=" ),
	LT( "less than", "<<" ),
	LE( "less than or equals", "<=" ),
	GT( "less than", ">>" ),
	GE( "less than or equals", ">=" );

	final String name;
	final String symbol;
	
	private RelOp(String name, String symbol ) {
    	this.name = name;
    	this.symbol = symbol;
    }
	
	public String getName() {
		return name; 
	}
	public String getSymbol() {
		return symbol; 
	}
	
	/** Returns an enum based on the given name such as "EQ" or "LT". */
	public static RelOp fromName( String name ) {
		return RelOp.valueOf( RelOp.class, name );
	}
	
	/** Returns an enum based on the given symbol such as "==" or "<=" */
	public static RelOp fromSymbol( String symbol ) {
		RelOp relOp = null;
        if ( null != symbol ) {
            switch ( symbol ) {
    		case "==": relOp = RelOp.EQ; break;
    		case "!=": relOp = RelOp.NE; break;
    		case "<<": relOp = RelOp.LT; break;
    		case "<=": relOp = RelOp.LE; break;
    		case ">>": relOp = RelOp.GT; break;
    		case ">=": relOp = RelOp.GE; break;
            }        	
        }
        return relOp;
	}
}
