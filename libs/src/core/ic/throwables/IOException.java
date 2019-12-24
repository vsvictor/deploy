package ic.throwables;



public class IOException extends java.io.IOException {

	public IOException(String message)	{ super(message); 	}
	public IOException(Throwable cause) { super(cause); 	}

	public static class Runtime extends RuntimeException {

		public Runtime(String message) { super(message); }

		public Runtime(Throwable cause) { super(cause); }

	}

}
