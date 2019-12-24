package ic.throwables;


public class Skip extends RuntimeException {

	public static final Skip SKIP = new Skip() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

}
