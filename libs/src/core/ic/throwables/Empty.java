package ic.throwables;


@SuppressWarnings("serial")


public class Empty extends Exception {

	public static final Empty EMPTY = new Empty() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

	public static class Runtime extends RuntimeException {

		public Runtime() {
			super();
		}

		public Runtime(Throwable cause) {
			super(cause);
		}

	}

}
