package ic.interfaces.action;


public abstract class SafeBetweenAction2<Arg1, Arg2, Thrown extends Throwable> implements SafeAction2<Arg1, Arg2, Thrown> {


	protected abstract void implementRun(Arg1 arg1, Arg2 arg2) throws Thrown;

	protected abstract void doBetween() throws Thrown;


	private volatile boolean toCallRunBetween = false;


	@Override public synchronized void run(Arg1 arg1, Arg2 arg2) throws Thrown {

		if (toCallRunBetween) {
			doBetween();
		} else {
			toCallRunBetween = true;
		}

		implementRun(arg1, arg2);

	}


}
