package ic.throwables;


import org.jetbrains.annotations.Nullable;

@SuppressWarnings("serial")


public class WrapperThrowable extends Throwable {

	public final Throwable cause;

	public WrapperThrowable(String message, Throwable cause) {
		super(message, cause);
		this.cause = cause;
	}

	public WrapperThrowable(Throwable cause) {
		super(cause);
		this.cause = cause;
	}

	public static class Runtime extends RuntimeException {

		public final Throwable cause;

		public Runtime(Throwable cause) {
			super(cause);
			this.cause = cause;
		}

	}

}
