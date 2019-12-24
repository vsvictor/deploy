package ic.throwables;


public class NoConnection extends IOException {

	public NoConnection(String message) { super(message); }

	public NoConnection(Throwable cause) { super(cause); }

}
