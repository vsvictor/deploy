package ic.throwables;


@SuppressWarnings("serial")


public class CalledTwiceTwice extends WrongCall {

	public CalledTwiceTwice(String message) { super(message); }

	public static class Runtime extends WrongCall.Runtime {

		public Runtime(String message) { super(message); }

	}

	public static class Error extends WrongCall.Error {

		public Error(String message) 	{ super(message); 	}
		public Error(Throwable cause) 	{ super(cause); 	}

	}

}
