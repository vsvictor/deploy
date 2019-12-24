package ic.throwables;


@SuppressWarnings("serial")


public class Equal extends Exception {

	public static final Equal EQUAL = new Equal() { @Override public synchronized Throwable fillInStackTrace() { return null; } };

}
