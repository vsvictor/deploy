package ic.event;


import ic.interfaces.action.Action1;


public class Event1<Arg> {


	protected final java.util.Set<Action1<Arg>> listeners = new java.util.HashSet<>();


	public void watch(Action1<Arg> listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void forget(Action1<Arg> listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}


	private Event1() {}


	public static class Trigger<Arg> extends Event1<Arg> implements Action1<Arg> {

		@Override public void run(Arg arg) {
			synchronized (listeners) {
				for (Action1<Arg> listener : listeners) listener.run(arg);
			}
		}

	}


}
