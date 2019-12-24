package ic.hash;


import ic.interfaces.action.Action1;
import org.jetbrains.annotations.NotNull;
import ic.interfaces.condition.EqualityCondition1;
import ic.interfaces.condition.EqualityCondition2;
import ic.throwables.Break;
import ic.throwables.NotExists;
import ic.struct.variable.Variable;
import ic.throwables.AlreadyExists;
import ic.struct.set.EditableSet;


public class HashSet<Item> extends EditableSet.BaseEditableSet<Item> {


	private final IntHasher<Item> hasher;

	private final EqualityCondition2<Item> equalityCondition;


	private final HashTable<Item> hashTable;


	@Override public boolean contains(@NotNull Item item) {
		return hashTable.contains(
			hasher.get(item),
			new EqualityCondition1<>(item, equalityCondition)
		);
	}

	@Override public void implementForEach(Action1<Item> action) throws Break {
		hashTable.forEach(action);
	}

	@Override public synchronized void implementAdd(Item item) throws AlreadyExists {
		hashTable.add(
			hasher.get(item),
			new EqualityCondition1<>(item, equalityCondition),
			new Variable.Constant<>(item)
		);
	}

	@Override public void implementRemove(Item item) throws NotExists {
		hashTable.remove(
			hasher.get(item),
			new EqualityCondition1<>(item, equalityCondition)
		);
	}

	@Override public void empty() {
		hashTable.empty();
	}


	public HashSet(@NotNull IntHasher<Item> hasher, @NotNull EqualityCondition2<Item> equalityCondition) {
		this.hasher 			= hasher;
		this.equalityCondition 	= equalityCondition;
		this.hashTable			= new HashTable<>(hasher);
	}

	public HashSet() {
		this(
			new IntHasher.Default<>(),
			new EqualityCondition2.Default<>()
		);
	}

}
