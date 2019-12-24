package ic.throwables;


public class AccessDenied extends Exception {



	public AccessDenied() 				{ super(); 			}
	public AccessDenied(String message) { super(message); 	}


	public static final AccessDenied ACCESS_DENIED = new AccessDenied() { @Override public synchronized Throwable fillInStackTrace() { return null; } };


	public static class Runtime extends RuntimeException {

		public Runtime()				{ super();			}
		public Runtime(String message) 	{ super(message); 	}
		public Runtime(Throwable cause) { super(cause); 	}

		public static final Runtime ACCESS_DENIED_RUNTIME = new Runtime() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

	}


	public static class Error extends java.lang.Error {

		public Error()					{ super();			}
		public Error(String message) 	{ super(message); 	}
		public Error(Throwable cause) 	{ super(cause); 	}

		public static final Runtime ACCESS_DENIED_ERROR = new Runtime() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

	}


}
