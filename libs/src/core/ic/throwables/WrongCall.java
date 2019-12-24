package ic.throwables;


@SuppressWarnings("serial")


public class WrongCall extends Exception {

	public WrongCall() 					{ super(); 			}
	public WrongCall(String message) 	{ super(message); 	}

	public static class Runtime extends RuntimeException {

		public Runtime() 				{ super(); 			}
		public Runtime(String message) 	{ super(message); 	}

	}

	public static class Error extends java.lang.Error {

		public Error(String message) 	{ super(message); 	}
		public Error(Throwable cause) 	{ super(cause); 	}

	}

}
