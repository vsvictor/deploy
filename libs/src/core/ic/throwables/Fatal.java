package ic.throwables;


public class Fatal extends Exception {


	public Fatal(String message) {
		super(message);
	}

	public Fatal(Throwable cause) {
		super(cause);
	}

	public Fatal(String message, Throwable cause) {
		super(message, cause);
	}


	public static class Runtime extends RuntimeException {

		public Runtime(String message) {
			super(message);
		}

		public Runtime(Throwable cause) {
			super(cause);
		}

		public Runtime(String message, Throwable cause) {
			super(message, cause);
		}

	}


}
