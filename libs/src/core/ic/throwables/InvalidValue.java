package ic.throwables;


@SuppressWarnings("serial")


public class InvalidValue extends Exception {


	public InvalidValue() 				{ super(); 			}
	public InvalidValue(String message) { super(message); 	}


	public static final InvalidValue INVALID_VALUE = new InvalidValue() { @Override public synchronized Throwable fillInStackTrace() { return null; } };


	// Subclasses:

	public static class Runtime extends RuntimeException {
		
		public Runtime(String message) 	{ super(message); 	}
		public Runtime(Throwable cause) { super(cause); 	}
		
	}

	public static class Error extends RuntimeException {

		public Error(String message) 	{ super(message); 	}
		public Error(Throwable cause)	{ super(cause); 	}

	}


}
