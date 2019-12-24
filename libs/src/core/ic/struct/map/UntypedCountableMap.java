package ic.struct.map;


import ic.interfaces.action.Action1;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import ic.annotations.Redirect;
import ic.interfaces.action.SafeAction1;
import ic.interfaces.countable.Countable;
import ic.interfaces.getter.UntypedGetter1;
import ic.struct.collection.Collection;
import ic.struct.set.CountableSet;
import ic.throwables.Break;

import static ic.util.Arrays.javaListToArray;


public interface UntypedCountableMap<Key> extends UntypedGetter1<Key>, Countable {


	@NotNull CountableSet<Key> getKeys();

	java.util.Map<Key, Object> toJavaMap();


	abstract class BaseUntypedCountableMap<Key> implements UntypedCountableMap<Key> {


		// Countable implementation:

		@Override public int getCount() { return getKeys().getCount(); }

		@Override public java.util.Map<Key, Object> toJavaMap() {
			final java.util.Map<Key, Object> javaMap = new java.util.HashMap<>();
			getKeys().forEach(key -> javaMap.put(key, get(key)));
			return javaMap;
		}


	}


	class FromCountableMap<Key> extends BaseUntypedCountableMap<Key> {

		private final CountableMap<Object, Key> countableMap;

		@Override public CountableSet<Key> getKeys() { return countableMap.getKeys(); }

		@Override public Object getObject(Key key) { return countableMap.get(key); }

		@SuppressWarnings("unchecked")
		public <Value> FromCountableMap(CountableMap<Value, Key> countableMap) {
			this.countableMap = (CountableMap<Object, Key>) countableMap;
		}

	}


	class Default<Key> extends BaseUntypedCountableMap<Key> {

		private final Object[] keysAndValues;

		@Override public CountableSet<Key> getKeys() {
			return new CountableSet<Key>() {
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

		@Override public Object getObject(Key key) {
			assert key != null;
			for (int i = 0; i < keysAndValues.length; i += 2) {
				@SuppressWarnings("unchecked")
				final @NotNull Key iKey = (Key) keysAndValues[i];
				assert iKey != null;
				if (iKey.equals(key)) {
					final Object value = keysAndValues[i + 1];
					return value;
				}
			} return null;
		}

		public Default(@NotNull Object... keysAndValues) {
			assert keysAndValues.length % 2 == 0 : "Arguments count should be even";
			this.keysAndValues = keysAndValues;
		}

		public Default(@NotNull final UntypedCountableMap<Key> countableMap) {
			final Collection<Key> keys = countableMap.getKeys();
			final java.util.List<Object> keysAndValues = new java.util.ArrayList<Object>();
			keys.forEach(key -> {
				keysAndValues.add(key);
				keysAndValues.add(countableMap.get(key));
			});
			this.keysAndValues = javaListToArray(keysAndValues, Object.class);
		}

		public <Value> Default(@NotNull Collection<Pair<Key, Value>> entries) {
			final java.util.List<Object> keysAndValues = new java.util.ArrayList<>();
			entries.forEach(entry -> {
				keysAndValues.add(entry.getFirst());
				keysAndValues.add(entry.getSecond());
			});
			this.keysAndValues = javaListToArray(keysAndValues, Object.class);
		}

	}


}
