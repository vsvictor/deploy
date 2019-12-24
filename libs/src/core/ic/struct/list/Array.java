package ic.struct.list;


import ic.annotations.Positive;
import ic.annotations.Valid;
import ic.util.Arrays;
import org.jetbrains.annotations.NotNull;


public interface Array<Item> extends List<Item> {


	void implementSet(@Valid @Positive int index, Item value) throws IndexOutOfBounds;

	default void setOrThrow(int index, Item value) throws IndexOutOfBounds {
		final int length = getCount();
		if (!isIndexValid(length, index)) throw new IndexOutOfBounds(length, index);
		implementSet(index, value);
	}

	default Item set(int index, Item value) throws IndexOutOfBounds.Runtime {
		try {
			setOrThrow(index, value);
		} catch (IndexOutOfBounds indexOutOfBounds) { throw new IndexOutOfBounds.Runtime(indexOutOfBounds); }
		return value;
	}

	class Default<Item> implements Array<Item> {

		private final Item[] items;

		@Override public final int getLength() {
			return items.length;
		}

		@Override public final Item implementGet(int index) {
			return items[index];
		}

		@Override public void implementSet(@Valid @Positive int index, Item value) throws IndexOutOfBounds.Runtime {
			items[index] = value;
		}

		public @SafeVarargs Default(Item... items) {
			this.items = items;
		}

		public Default(int length) {
			this.items = Arrays.createArray(length);
		}

	}


}
