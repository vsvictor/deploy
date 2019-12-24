package ic.throwables;


public class InconsistentData extends Error {

	public InconsistentData(String message) {
		super(message);
	}

	public InconsistentData(Throwable cause) {
		super(cause);
	}

	public InconsistentData(String message, Throwable cause) {
		super(message, cause);
	}

}
