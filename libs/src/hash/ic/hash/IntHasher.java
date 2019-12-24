package ic.hash;


import org.jetbrains.annotations.NotNull;


public interface IntHasher<Item> {


	int get(Item item);


	class Default<Item> implements IntHasher<Item> {

		@Override public int get(@NotNull Item item) {
			assert item != null;
			return item.hashCode();
		}

	}


}
