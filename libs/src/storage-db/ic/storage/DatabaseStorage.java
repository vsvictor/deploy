package ic.storage;


import ic.interfaces.condition.Condition1;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.map.UntypedCountableMap;
import kotlin.Pair;

import static ic.struct.collection.Collections.and;
import static ic.throwables.Skip.SKIP;


public interface DatabaseStorage extends Storage {

	default <Item> Collection<Pair<String, Item>> select(Collection<Condition1<Item>> conditions) {
		synchronized (this) {
			return new ConvertCollection<>(
				getKeys(),
				key -> {
					final Item item = getNotNull(key);
					if (!and(conditions, item)) throw SKIP;
					return new Pair<>(key, item);
				}
			);
		}
	}

	<Type> Collection<Pair<String, Type>> select(UntypedCountableMap<String> fields);

	abstract class BaseDatabaseStorage extends BaseStorage implements DatabaseStorage {



	}

}
