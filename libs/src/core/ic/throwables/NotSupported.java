package ic.throwables;


@SuppressWarnings("serial")


public class NotSupported extends Exception {


	public NotSupported() { super(); }

	public NotSupported(String message) { super(message); }

	public NotSupported(Throwable cause) { super(cause); }


	public static final NotSupported NOT_SUPPORTED = new NotSupported() { @Override public synchronized Throwable fillInStackTrace() { return null; } };


	public static class Runtime extends RuntimeException {

		public Runtime() { super(); }

		public Runtime(String message) { super(message); }

		public Runtime(Throwable cause) { super(cause); }

		public static final NotSupported.Runtime NOT_SUPPORTED_RUNTIME = new NotSupported.Runtime() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

	}


	public static class Error extends java.lang.Error {

		public Error() 					{ super(); 			}
		public Error(String message) 	{ super(message); 	}
		public Error(Throwable cause) 	{ super(cause); 	}

	}


}
