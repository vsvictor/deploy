package ic.throwables;


public class UnableToParse extends Exception {


	public UnableToParse(String message) 					{ super(message); 			}
	public UnableToParse(Throwable cause) 					{ super(cause); 			}
	public UnableToParse(String message, Throwable cause) 	{ super(message, cause); 	}


	public static class Runtime extends RuntimeException {

		public Runtime(String message) { super(message); }

		public Runtime(Throwable cause) { super(cause); }

		public Runtime(String message, Throwable cause) { super(message, cause); }

	}


}
