package ic.git;


public class Rejected extends Exception {

	public Rejected(String message) {
		super(message);
		assert message != null;
	}

}
