package ic.throwables;


public class Conflict extends Exception {


	public Conflict() {
		super();
	}

	public Conflict(String message) {
		super(message);
		assert message != null;
	}


	public static final Conflict CONFLICT = new Conflict() { @Override public synchronized Throwable fillInStackTrace() { return null; } };


}
