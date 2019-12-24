package ic.throwables;


public class InvalidSyntax extends Exception {


	public InvalidSyntax() 									{ super(); 					}
	public InvalidSyntax(String message) 					{ super(message); 			}
	public InvalidSyntax(String message, Throwable cause) 	{ super(message, cause); 	}


	public static final InvalidSyntax INVALID_SYNTAX = new InvalidSyntax() { @Override public synchronized Throwable fillInStackTrace() { return null; } };


}
