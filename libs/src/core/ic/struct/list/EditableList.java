package ic.struct.list;


import ic.annotations.Positive;
import ic.annotations.Valid;
import ic.interfaces.condition.Condition1;
import ic.interfaces.emptiable.Emptiable;
import ic.throwables.NotExists;
import ic.interfaces.adder.Adder;
import ic.struct.collection.Collection;
import org.jetbrains.annotations.NotNull;

import static ic.util.Arrays.arrayToJavaList;


public interface EditableList<Item> extends Array<Item>, Adder<Item>, Emptiable {


	void implementInsert(@Valid @Positive int index, Item item);

	default void insertOrThrow(int index, Item item) throws IndexOutOfBounds {
		synchronized (this) {
			if (index < 0) 			throw new IndexOutOfBounds(getCount(), index);
			if (index > getCount()) throw new IndexOutOfBounds(getCount(), index);
			implementInsert(index, item);
		}
	}

	default void insert(int index, Item item) throws IndexOutOfBounds.Runtime {
		synchronized (this) {
			try {
				insertOrThrow(index, item);
			} catch (IndexOutOfBounds indexOutOfBounds) { throw new IndexOutOfBounds.Runtime(indexOutOfBounds); }
		}
	}


	void implementRemove(@Valid @Positive int index);

	default void removeOrThrow(int index) throws IndexOutOfBounds {
		synchronized (this) {
			if (!isIndexValid(getCount(), index)) throw new IndexOutOfBounds(getCount(), index);
			implementRemove(index);
		}
	}

	default void remove(int index) throws IndexOutOfBounds.Runtime {
		try {
			removeOrThrow(index);
		} catch (IndexOutOfBounds indexOutOfBounds) { throw new IndexOutOfBounds.Runtime(indexOutOfBounds); }
	}


	default void remove(@NotNull Condition1<Item> searcher) throws NotExists {
		synchronized (this) {
			final int count = getCount();
			for (int i = 0; i < count; i++) { final Item item = get(i);
				if (searcher.is(item)) {
					remove(i);
					return;
				}
			} throw new NotExists();
		}
	}

	default void removeAll(@NotNull Condition1<Item> searcher) {
		synchronized (this) {
			int index = 0;
			while (index < getCount()) {
				final Item item = get(index);
				if (searcher.is(item)) {
					remove(index);
				} else {
					index++;
				}
			}
		}
	}


	// Adder<Item> implementation:

	default void add(Item item) {
		insert(getCount(), item);
	}

	default void clip(int newSize) {
		synchronized (this) {
			final int size = getCount();
			if (size == newSize) return;
			if (size < newSize) {
				int difference = newSize - size;
				for (int i = 0; i < difference; i++) {
					add(null);
				}
			} else
			if (size > newSize) {
				int difference = size - newSize;
				for (int i = 0; i < difference; i++) {
					remove(size - i - 1);
				}
			}
			else throw new AssertionError();
		}
	}


	// BaseEditableIndex implementation:

	default void empty() {
		synchronized (this) {
			final int count = getCount();
			for (int i = 0; i < count; i++) {
				set(i, null);
			}
		}
	}


	class FromJavaList<Item> implements EditableList<Item> {

		private final @NotNull java.util.List<Item> javaList;

		@Override public synchronized int getLength() {
			return javaList.size();
		}

		@Override public synchronized Item implementGet(@Valid int index) {
			return javaList.get(index);
		}

		@Override public void implementSet(int index, Item value) {
			javaList.set(index, value);
		}

		@Override public void implementInsert(int index, Item item) {
			javaList.add(index, item);
		}

		@Override public void implementRemove(int index) {
			javaList.remove(index);
		}

		@Override public void empty() {
			javaList.clear();
		}

		// Constructors:

		@SuppressWarnings({"unchecked", "RedundantSuppression"}) // For some reasons javac lints this row ¯\_(ツ)_/¯
		public FromJavaList(@NotNull java.util.List<Item> javaList) {
			this.javaList = javaList;
		}

	}


	class Default<Item> extends FromJavaList<Item> {

		public Default() {
			super(new java.util.ArrayList<>());
		}

		public Default(Iterable<Item> collection) {
			this();
			collection.forEach(this::add);
		}

		public @SafeVarargs Default(Item... items) {
			super(arrayToJavaList(items));
		}

	}


}
