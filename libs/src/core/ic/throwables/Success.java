package ic.throwables;


@SuppressWarnings("serial")


public class Success extends Throwable {

	public Success() { super(); }

	public static final Success SUCCESS = new Success();

}
