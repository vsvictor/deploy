package ic.event;


import ic.interfaces.action.Action;
import ic.throwables.AlreadyExists;


public class Event {


	protected final java.util.Set<Action> listeners = new java.util.HashSet<Action>();


	public void watch(Action listener, boolean runAtOnce) {
		synchronized (listeners) {
			listeners.add(listener);
		}
		if (runAtOnce) listener.run();
	}

	public void watch(Action listener) throws AlreadyExists.Runtime {
		watch(listener, false);
	}

	public void forget(Action listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}


	private Event() {}


	public static class Trigger extends Event implements Action {

		@Override public void run() {
			synchronized (listeners) {
				for (Action listener : listeners.toArray(new Action[listeners.size()])) listener.run();
			}
		}

	}


}
