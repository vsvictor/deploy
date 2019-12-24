package ic.interfaces.adder;


import org.jetbrains.annotations.NotNull;


public interface Adder<Item> {


	void add(Item item);

	default void addAll(@NotNull Iterable<Item> items) {
		for (Item item : items) {
			add(item);
		}
	}


}
