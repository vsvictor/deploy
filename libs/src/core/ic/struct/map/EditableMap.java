package ic.struct.map;


import ic.interfaces.setter.GetterSetter1;
import ic.struct.set.CountableSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface EditableMap<Value, Key> extends CountableMap<Value, Key>, GetterSetter1<Value, Key> {


	class FromJavaMap<Value, Key> implements EditableMap<Value, Key> {

		private final java.util.Map<Key, Value> javaMap;

		@Override public synchronized int getCount() {
			return javaMap.size();
		}

		@Override public synchronized boolean isEmpty() {
			return javaMap.isEmpty();
		}

		@NotNull @Override public synchronized CountableSet<Key> getKeys() {
			return new CountableSet.Default<Key>(javaMap.keySet());
		}

		@Override public synchronized Value get(@NotNull Key key) {
			return javaMap.get(key);
		}

		@Override public synchronized void set(@NotNull Key key, @Nullable Value value) {
			javaMap.put(key, value);
		}

		public FromJavaMap(@NotNull java.util.Map<Key, Value> javaMap) {
			this.javaMap = javaMap;
		}

	}


	class Default<Value, Key> extends FromJavaMap<Value, Key> {

		public Default() {
			super(new java.util.HashMap<Key, Value>());
		}

		public Default(@NotNull final CountableMap<Value, Key> countableMap) {
			this();
			countableMap.getKeys().forEach(key -> set(key, countableMap.get(key)));
		}

	}


}
