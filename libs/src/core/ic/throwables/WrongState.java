package ic.throwables;


@SuppressWarnings("serial")


public class WrongState extends Exception {


	public WrongState() { super(); }

	public WrongState(String message) { super(message); }


	public static final WrongState WRONG_STATE = new WrongState() { @Override public synchronized Throwable fillInStackTrace() { return null; } };


	public static class Runtime extends RuntimeException {

		public Runtime(String message) 	{ super(message); 	}
		public Runtime(Throwable cause) { super(cause); 	}

	}

	public static class Error extends java.lang.Error {

		public Error(String message) 	{ super(message); 	}
		public Error(Throwable cause) 	{ super(cause); 	}

	}


}