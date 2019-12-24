package ic.throwables;


@SuppressWarnings("serial")


public class NotNeeded extends Exception {

	public static NotNeeded NOT_NEEDED = new NotNeeded() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

	public static class Runtime extends RuntimeException {

	}

}
