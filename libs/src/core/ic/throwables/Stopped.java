package ic.throwables;


public class Stopped extends Exception {


	public static final Stopped STOPPED = new Stopped() { @Override public synchronized Throwable fillInStackTrace() { return null; } };


}
