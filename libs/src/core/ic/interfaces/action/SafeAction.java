package ic.interfaces.action;


import ic.annotations.DoesNothing;

/**
 * Does something and eventually throws {@link Thrown} in the process.
 */
public interface SafeAction<Thrown extends Throwable> extends Safe2Action<Thrown, RuntimeException> {

	void run() throws Thrown;

	class DoNothing<Thrown extends Throwable> implements SafeAction<Thrown> {
		@DoesNothing @Override public void run() throws Thrown {}
	}

}
