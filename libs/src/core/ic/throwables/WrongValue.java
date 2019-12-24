package ic.throwables;


import org.jetbrains.annotations.Nullable;

@SuppressWarnings("serial")


public class WrongValue extends Exception {


	public final @Nullable Object value;


	public WrongValue() {
		super();
		this.value = null;
	}

	public WrongValue(String message) {
		super(message);
		this.value = null;
	}

	public WrongValue(Throwable cause) {
		super(cause);
		this.value = null;
	}

	public WrongValue(Object value) {
		super("value: " + value);
		this.value = value;
	}


	public static final WrongValue WRONG_VALUE = new WrongValue() { @Override public synchronized Throwable fillInStackTrace() { return null; } };


	public static class Runtime extends RuntimeException {

		public Runtime(String message) {
			super(message);
		}

		public Runtime(Throwable cause) {
			super(cause);
		}

	}

	public static class Error extends java.lang.Error {

		public Error(String message) {
			super(message);
		}

	}


}
