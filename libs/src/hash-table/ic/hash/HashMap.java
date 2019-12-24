package ic.hash;


import ic.interfaces.action.Action1;
import ic.struct.map.EditableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ic.interfaces.condition.Condition1;
import ic.interfaces.condition.Condition2;
import ic.interfaces.condition.EqualityCondition2;
import ic.throwables.NotExists;
import ic.struct.set.CountableSet;


public class HashMap<Value, Key> implements EditableMap<Value, Key> {


	private final IntHasher<Key> keyHasher;

	private final Condition2<Key, Key> keysEqualityCondition;


	private final IntHasher<Entry> entryHasher;


	private class Entry {
		final Key key;
		final Value value;
		Entry(Key key, Value value) {
			this.key = key;
			this.value = value;
		}
	}


	private final HashTable<Entry> hashTable;


	private class EntrySearcher implements Condition1<Entry> {
		final Key key;
		@Override public boolean is(Entry entry) {
			return keysEqualityCondition.is(key, entry.key);
		}
		public EntrySearcher(Key key) {
			this.key = key;
		}
	}


	@Override public Value get(final @NotNull Key key) {
		try {
			return hashTable.get(
				keyHasher.get(key),
				new EntrySearcher(key)
			).value;
		} catch (NotExists notExists) {
			return null;
		}
	}

	@Override public void set(final @NotNull Key key, @Nullable Value value) {
		final int hash = keyHasher.get(key);
		final EntrySearcher entrySearcher = new EntrySearcher(key);
		if (value == null) {
			try {
				hashTable.remove(hash, entrySearcher);
			} catch (NotExists notExists) {}
		} else {
			hashTable.set(hash, entrySearcher, new Entry(key, value));
		}
	}

	final CountableSet<Key> keys = new CountableSet<Key>() {
		@Override public void implementForEach(final Action1<Key> action) {
			hashTable.forEach(entry -> action.run(entry.key));
		}
		@Override public boolean contains(Key key) {
			return hashTable.contains(
				keyHasher.get(key),
				new EntrySearcher(key)
			);
		}
	}; @NotNull @Override public CountableSet<Key> getKeys() { return keys; }


	// Constructors:

	public HashMap(@NotNull IntHasher<Key> keyHasher, @NotNull Condition2<Key, Key> keysEqualityCondition) {
		this.keyHasher = keyHasher;
		this.keysEqualityCondition = keysEqualityCondition;
		this.entryHasher = new IntHasher<Entry>() { @Override public int get(Entry entry) {
			return entry.key.hashCode();
		} };
		this.hashTable = new HashTable<Entry>(entryHasher);
	}

	public HashMap() {
		this(
			new IntHasher.Default<Key>(),
			new EqualityCondition2.Default<Key>()
		);
	}


}
