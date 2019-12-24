package ic.interfaces.adder;


import org.jetbrains.annotations.NotNull;
import ic.throwables.AlreadyExists;


public interface SetAdder<Item> extends Adder<Item> {


	void addOrThrow(Item item) throws AlreadyExists;


	@Override default void add(Item item) {
		try {
			addOrThrow(item);
		} catch (AlreadyExists alreadyExists) { throw new AlreadyExists.Runtime(alreadyExists); }
	}

	default void addIfNotExists(Item item) {
		try {
			addOrThrow(item);
		} catch (AlreadyExists alreadyExists) {}
	}

	@Override default void addAll(@NotNull Iterable<Item> items) {
		for (Item item : items) {
			addIfNotExists(item);
		}
	}


}
