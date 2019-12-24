package ic.throwables;


@SuppressWarnings("serial")


public class NotEnough extends Exception {

	public NotEnough() { super(); }

	public NotEnough(String message) { super(message); }

	public NotEnough(Throwable cause) { super(cause); }

	public static final NotEnough NOT_ENOUGH = new NotEnough() {
		@Override public synchronized Throwable fillInStackTrace() { return null; }
	};

}
