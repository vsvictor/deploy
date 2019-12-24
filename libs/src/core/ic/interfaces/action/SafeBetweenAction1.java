package ic.interfaces.action;


import ic.throwables.Break;


public abstract class SafeBetweenAction1<Arg, Thrown extends Throwable> implements Safe2Action1<Arg, Break, Thrown> {


	protected abstract void implementRun(Arg arg) throws Thrown;

	protected abstract void doBetween() throws Thrown;


	private volatile boolean toCallRunBetween = false;


	@Override public synchronized void run(Arg arg) throws Thrown {

		if (toCallRunBetween) {
			doBetween();
		} else {
			toCallRunBetween = true;
		}

		implementRun(arg);

	}


}
