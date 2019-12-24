package ic.struct.collection;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.interfaces.action.SafeAction1;
import ic.interfaces.condition.Condition1;
import ic.throwables.Break;


public @Degenerate class Collections { @Hide private Collections() {}


	public static int minInt(final @NotNull Collection<Integer> collection) {
		assert collection != null;
		class Worker implements SafeAction1<Integer, Break> {
			int minimum = Integer.MAX_VALUE;
			@Override public synchronized void run(@NotNull Integer integer) {
				assert integer != null;
				if (integer < minimum) minimum = integer;
			}
		}
		final Worker worker = new Worker();
		collection.forEach(worker);
		return worker.minimum;
	}

	public static int maxInt(final @NotNull Collection<Integer> collection) {
		assert collection != null;
		class Worker implements SafeAction1<Integer, Break> {
			int maximum = Integer.MIN_VALUE;
			@Override public synchronized void run(@NotNull Integer integer) {
				assert integer != null;
				if (integer > maximum) maximum = integer;
			}
		}
		final Worker worker = new Worker();
		collection.forEach(worker);
		return worker.maximum;
	}

	public static float maxFloat(final @NotNull Collection<Float> collection) {
		assert collection != null;
		class Worker implements SafeAction1<Float, Break> {
			float maximum = Float.MIN_VALUE;
			@Override public synchronized void run(@NotNull Float f) {
				assert f != null;
				if (f > maximum) maximum = f;
			}
		}
		final Worker worker = new Worker();
		collection.forEach(worker);
		return worker.maximum;
	}

	public static int sumInt(final @NotNull Collection<Integer> collection) {
		assert collection != null;
		class Worker implements SafeAction1<Integer, Break> {
			int sum = 0;
			@Override public synchronized void run(@NotNull Integer integer) {
				assert integer != null;
				sum += integer;
			}
		}
		final Worker worker = new Worker();
		collection.forEach(worker);
		return worker.sum;
	}


	public static long maxLong(final @NotNull Collection<Long> collection) {
		assert collection != null;
		class Worker implements SafeAction1<Long, Break> {
			long maximum = Long.MIN_VALUE;
			@Override public synchronized void run(@NotNull Long l) {
				assert l != null;
				if (l > maximum) maximum = l;
			}
		}
		final Worker worker = new Worker();
		collection.forEach(worker);
		return worker.maximum;
	}


	private static class Done extends RuntimeException {
		static final Done DONE = new Done() {
			@Override public synchronized Throwable fillInStackTrace() { return null; }
		};
	}

	public static <Arg> boolean and(final @NotNull Collection<Condition1<Arg>> collection, Arg arg) {
		try {
			collection.forEach(condition -> {
				if (!condition.is(arg)) throw Done.DONE;
			});
			return true;
		} catch (Done notNeeded) { return false; }
	}


}
