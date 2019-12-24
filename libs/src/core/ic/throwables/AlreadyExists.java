package ic.throwables;


import org.jetbrains.annotations.Nullable;

@SuppressWarnings("serial")


public class AlreadyExists extends Exception {


	public final @Nullable Object existing;

	public AlreadyExists() {
		super();
		this.existing = null;
	}

	public AlreadyExists(String message) {
		super(message);
		this.existing = null;
	}

	public AlreadyExists(Object existing) {
		super();
		this.existing = existing;
	}


	public static final AlreadyExists ALREADY_EXISTS = new AlreadyExists() { @Override public synchronized Throwable fillInStackTrace() { return null; } };


	public static class Runtime extends RuntimeException {

		public Runtime() 					{ super();			}
		public Runtime(String message) 		{ super(message); 	}
		public Runtime(Throwable throwable) { super(throwable); }

		public static final Runtime ALREADY_EXISTS_RUNTIME = new Runtime() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

	}


	public static class Error extends java.lang.Error {

		public Error(String message) 	{ super(message); 	}
		public Error(Throwable cause) 	{ super(cause); 	}

	}


}
