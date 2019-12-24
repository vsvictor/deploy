package ic.throwables;


@SuppressWarnings("serial")


public class WrongArgument extends Exception {


	public WrongArgument() { super(); }

	public WrongArgument(String message) { super(message); }

	public WrongArgument(Throwable cause) { super(cause); }


	public static class Runtime extends RuntimeException {

		public Runtime(String message) {
			super(message);
		}

		public Runtime(Throwable cause) {
			super(cause);
		}

	}

	public static class Error extends java.lang.Error {

		public Error(String message) {
			super(message);
		}

	}


}
