package ic.id;


import ic.annotations.DoesNothing;
import ic.annotations.ToOverride;
import ic.interfaces.emptiable.Emptiable;
import ic.interfaces.getter.Getter1;
import ic.interfaces.remover.Remover1;
import ic.interfaces.setter.Setter1;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.map.EditableMap;
import ic.struct.map.EphemeralKeysMap;
import ic.struct.set.CountableSet;
import ic.struct.variable.Ephemeral;
import ic.struct.variable.Weak;
import ic.throwables.NotExists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ic.throwables.Skip.SKIP;


public abstract class Mapper<Item, Id> implements Getter1<Item, Id>, Setter1<Item, Id>, Remover1<Id>, Emptiable {


	protected abstract IdGenerator<Id> getIdGenerator();


	@ToOverride
	protected EditableMap<Item, Id> initItemsMapImplementation() { return new EditableMap.Default<>(); }

	private final EditableMap<Item, Id> itemsMapImplementation = initItemsMapImplementation();


	@ToOverride
	protected EphemeralKeysMap<Id, Item> initIdentityMapImplementation(Getter1<Ephemeral<Item>, Item> ephemeralVariableCreator) {
		return new EphemeralKeysMap<>(ephemeralVariableCreator);
	}

	private final EphemeralKeysMap<Id, Item> identityMapImplementation = initIdentityMapImplementation(Weak::new);


	public synchronized final CountableSet<Id> getIds() {
		return itemsMapImplementation.getKeys();
	}

	@Override public Item get(Id id) {
		final @Nullable Item item = itemsMapImplementation.get(id);
		if (item == null) return null;
		identityMapImplementation.set(item, id);
		return item;
	}

	@Deprecated
	public final @NotNull Item getItem(@NotNull Id id) {
		try {
			return getOrThrow(id);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}

	@Deprecated
	public final @Nullable Item getItemOrNull(@NotNull Id id) {
		return get(id);
	}

	@Deprecated
	public final @Nullable Item getNullableItem(@Nullable Id id) {
		if (id == null) return null;
		else return getItem(id);
	}

	@Deprecated
	public final @NotNull Item getItemOrThrow(@NotNull Id id) throws NotExists {
		return getOrThrow(id);
	}

	@ToOverride protected @DoesNothing void onIdGenerated(IdGenerator<Id> idGenerator) {}

	@Override public synchronized void set(Id id, Item item) {
		identityMapImplementation.set(item, id);
		itemsMapImplementation.set(id, item);
	}

	public synchronized final Id id(@NotNull final Item item) {
		return identityMapImplementation.createIfNull(item, () -> {
			final IdGenerator<Id> idGenerator = getIdGenerator();
			final Id id = idGenerator.get();
			itemsMapImplementation.set(id, item);
			onIdGenerated(idGenerator);
			return id;
		});
	}

	@Deprecated
	public final Id getId(@NotNull final Item item) { return id(item); }

	public final Collection<Item> getItems() {
		return new ConvertCollection<>(
			getIds(),
			id -> {
				try {
					return getItemOrThrow(id);
				} catch (NotExists notExists) { throw SKIP; }
			}
		);
	}

	@Override public void empty() {
		itemsMapImplementation.getKeys().forEach(id -> itemsMapImplementation.set(id, null));
	}


	// Remover implementation:

	@Override public void removeOrThrow(Id id) throws NotExists {
		itemsMapImplementation.set(id, null);
	}


}
