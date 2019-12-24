package ic.throwables;


public class NotExists extends Exception {


	// Constructors:

	public NotExists() 									{ super(); 					}
	public NotExists(String message) 					{ super(message); 			}
	public NotExists(Throwable cause) 					{ super(cause); 			}
	public NotExists(String message, Throwable cause) 	{ super(message, cause); 	}


	// Static:

	public static final NotExists NOT_EXISTS = new NotExists() { @Override public Throwable fillInStackTrace() { return null; } };


	public static class Runtime extends RuntimeException {

		public Runtime() 								{ super(); 					}
		public Runtime(String message) 					{ super(message); 			}
		public Runtime(Throwable cause) 				{ super(cause); 			}
		public Runtime(String message, Throwable cause) { super(message, cause); 	}

	}


	public static class Error extends java.lang.Error {

		public Error(String message) 	{ super(message); 	}
		public Error(Throwable cause) 	{ super(cause); 	}

	}


}
