package ic.network;


public class IncompleteRequest extends Exception {

	public IncompleteRequest(String explanation) {
		super(explanation);
	}

	public IncompleteRequest(Throwable cause) {
		super(cause);
	}

}
