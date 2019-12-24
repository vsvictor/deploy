package ic;


import ic.annotations.CallOnce;
import ic.annotations.Default;
import ic.annotations.Same;
import ic.throwables.CalledTwiceTwice;
import ic.throwables.NotNeeded;
import ic.throwables.WrongState;


public abstract class Service {


	public static final byte STATE_INITIAL 	= 0;
	public static final byte STATE_STARTED 	= 1;
	public static final byte STATE_FINISHED = 2;
	public static final byte STATE_STOPPED	= 3;

	private volatile byte state = STATE_INITIAL;

	public synchronized byte getState() { return state; }


	public final boolean isRunning() { return getState() == STATE_STARTED; }

	protected final boolean toStop() { return getState() == STATE_STOPPED; }


	@Default @Same protected boolean isReusable() { return false; }


	protected final synchronized void notifyEnded() {
		if (state == STATE_STARTED) {
			state = STATE_FINISHED;
		} else
		if (state == STATE_STOPPED) {
			return;
		}
		else throw new WrongState.Runtime("state: " + state);
	}


	protected abstract void implementStart();


	@CallOnce("per instance")
	public final void start() throws CalledTwiceTwice.Runtime {

		synchronized (this) {
			if (isReusable()) {
				if (state == STATE_STARTED) throw new NotNeeded.Runtime();
			} else {
				if (state != STATE_INITIAL) throw new CalledTwiceTwice.Runtime("This service get not reusable");
			}
			state = STATE_STARTED;
		}

		implementStart();

	}

	protected abstract void implementStop();

	public final void stop() {

		synchronized (this) {
			state = STATE_STOPPED;
		}

		implementStop();

	}


}
