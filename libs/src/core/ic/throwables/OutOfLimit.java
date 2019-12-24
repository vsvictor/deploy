package ic.throwables;


@SuppressWarnings("serial")


public class OutOfLimit extends Exception {

	public static final OutOfLimit OUT_OF_LIMIT = new OutOfLimit() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

	public OutOfLimit() { super(); }

	public OutOfLimit(String message) { super(message); }

}
