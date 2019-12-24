package ic.throwables;


public class Stop extends Exception {

	public static final Stop STOP = new Stop() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

	public static class Runtime extends RuntimeException {

		public static final Runtime STOP_RUNTIME = new Runtime() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

	}

}
