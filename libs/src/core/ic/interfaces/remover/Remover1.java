package ic.interfaces.remover;


import ic.throwables.NotExists;


public interface Remover1<Item> {


	void removeOrThrow(Item item) throws NotExists;


	default void remove(Item item) throws NotExists.Runtime {
		try {
			removeOrThrow(item);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}

	default void removeIfExists(Item item) {
		try {
			removeOrThrow(item);
		} catch (NotExists notExists) {}
	}


}
