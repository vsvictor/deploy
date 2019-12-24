package ic.id;


import ic.struct.map.EditableMap;
import org.jetbrains.annotations.NotNull;
import ic.storage.Storage;
import ic.struct.set.CountableSet;
import ic.struct.set.FilterCountableSet;
import ic.throwables.InvalidValue;


public abstract class StorageMapper<Item> extends Mapper<Item, String> {


	private static final String ID_GENERATOR_FILENAME = "id-generator";


	private final Storage storage;


	protected abstract IdGenerator<String> initIdGenerator();

	@Override protected IdGenerator<String> getIdGenerator() {
		return storage.createIfNull(ID_GENERATOR_FILENAME, this::initIdGenerator);
	}


	@Override protected void onIdGenerated(IdGenerator<String> idGenerator) {
		storage.set(ID_GENERATOR_FILENAME, idGenerator);
	}


	@Override protected EditableMap<Item, String> initItemsMapImplementation() {

		return new EditableMap<Item, String>() {

			@Override public Item get(String id) {
				if (id.equals(ID_GENERATOR_FILENAME)) return null;
				return storage.get(id);
			}

			@Override public void set(String id, Item item) {
				if (id.equals(ID_GENERATOR_FILENAME)) throw new InvalidValue.Runtime("Generated id can't be \"" + ID_GENERATOR_FILENAME + "\"");
				storage.set(id, item);
			}

			@NotNull @Override public CountableSet<String> getKeys() {
				return new FilterCountableSet<String>(storage.getKeys()) { @Override public boolean filter(String id) {
					return !id.equals(ID_GENERATOR_FILENAME);
				} };
			}

		};

	}


	public void saveItem(@NotNull Item item) {
		storage.set(getId(item), item);
	}


	public StorageMapper(@NotNull Storage storage) {
		this.storage = storage;
	}


}
