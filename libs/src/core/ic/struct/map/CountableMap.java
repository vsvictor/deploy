package ic.struct.map;


import ic.interfaces.action.Action1;
import ic.struct.collection.Collection;
import ic.struct.set.CountableSet;
import ic.throwables.Break;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import static ic.util.Arrays.javaListToArray;


public interface CountableMap<Value, Key> extends Map<Value, Key>, Collection<Value> {


	@NotNull CountableSet<Key> getKeys();

	default java.util.Map<Key, Value> toJavaMap() {
		final java.util.Map<Key, Value> map = new java.util.HashMap<>();
		getKeys().forEach(key -> {
			map.put(key, get(key));
		});
		return map;
	}

	default @Override int getCount() { return getKeys().getCount(); }

	default @Override void implementForEach(@NotNull Action1<Value> action) {
		synchronized (this) {
			getKeys().forEach(key -> action.run(get(key)));
		}
	}

	class Default<Value, Key> implements CountableMap<Value, Key> {

		private final Object[] keysAndValues;

		@NotNull @Override public CountableSet<Key> getKeys() {
			return new CountableSet<>() {
				@Override public void implementForEach(@NotNull Action1<Key> action) throws Break {
					for (int i = 0; i < keysAndValues.length; i += 2) {
						@SuppressWarnings("unchecked")
						final Key key = (Key) keysAndValues[i];
						action.run(key);
					}
				}
				@Override public boolean contains(@NotNull Key key) {
					for (int i = 0; i < keysAndValues.length; i += 2) {
						@SuppressWarnings("unchecked")
						final @NotNull Key iKey = (Key) keysAndValues[i];
						assert iKey != null;
						if (iKey.equals(key)) return true;
					} return false;
				}
			};
		}

		@Override public Value get(@NotNull Key key) {
			for (int i = 0; i < keysAndValues.length; i += 2) {
				@SuppressWarnings("unchecked")
				final @NotNull Key iKey = (Key) keysAndValues[i];
				assert iKey != null;
				if (iKey.equals(key)) {
					@SuppressWarnings("unchecked")
					final Value value = (Value) keysAndValues[i + 1];
					return value;
				}
			} return null;
		}

		public Default(@NotNull Object... keysAndValues) {
			assert keysAndValues.length % 2 == 0 : "Arguments count should be even";
			this.keysAndValues = keysAndValues;
		}

		public Default(@NotNull final CountableMap<Value, Key> countableMap) {
			final Collection<Key> keys = countableMap.getKeys();
			final java.util.List<Object> keysAndValues = new java.util.ArrayList<>();
			keys.forEach(key -> {
				keysAndValues.add(key);
				keysAndValues.add(countableMap.get(key));
			});
			this.keysAndValues = javaListToArray(keysAndValues, Object.class);
		}

		public Default(@NotNull Collection<Pair<Key, Value>> entries) {
			final java.util.List<Object> keysAndValues = new java.util.ArrayList<>();
			entries.forEach(entry -> {
				keysAndValues.add(entry.getFirst());
				keysAndValues.add(entry.getSecond());
			});
			this.keysAndValues = javaListToArray(keysAndValues, Object.class);
		}

		public Default(@NotNull java.util.Map<Key, Value> javaMap) {
			final java.util.List<Object> keysAndValues = new java.util.ArrayList<>();
			for (Key key : javaMap.keySet()) {
				keysAndValues.add(key);
				keysAndValues.add(javaMap.get(key));
			}
			this.keysAndValues = javaListToArray(keysAndValues, Object.class);
		}

	}


}
