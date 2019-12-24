package ic.throwables;


@SuppressWarnings("serial")


public class End extends Throwable {

	public static End END = new End() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

}
