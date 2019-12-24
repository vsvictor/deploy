package ic.storage;


import ic.annotations.AlwaysThrows;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.collection.JoinCollection;
import ic.struct.order.Order;
import ic.struct.order.OrderedCountableSet;
import ic.struct.sequence.Sequence;
import ic.struct.sequence.Series;
import ic.throwables.NotExists;
import ic.throwables.End;
import ic.throwables.NotSupported;

import static ic.throwables.NotExists.NOT_EXISTS;


public class MergeStreamStorage extends DistributedStreamStorage {


	private final Sequence<StreamStorage> storages;


	@Override public OrderedCountableSet<String> getKeys() {
		return new OrderedCountableSet.Default<>(
			Order.ALPHABETIC_STRING_ORDER,
			new JoinCollection<>(
				new ConvertCollection<>(
					new Collection.Default<>(storages),
					Storage::getKeys
				)
			)
		);
	}


	@Override protected final StreamStorage getStorageToRead(String name) throws NotExists {
		{ final Series<StreamStorage> storagesIterator = storages.getIterator(); try { while (true) { final StreamStorage storage = storagesIterator.get();
			if (storage.getKeys().contains(name)) return storage;
		} } catch (End end) { throw NOT_EXISTS; } }
	}

	@Override protected Storage implementGetFolder(final String key) throws NotExists {
		return new MergeStreamStorage(
			() -> {
				final Series<StreamStorage> parentStoragesIterator = storages.getIterator();
				return () -> {
					while (true) {
						final StreamStorage parentStorage = parentStoragesIterator.get();
						try {
							return parentStorage.getOrThrow(key);
						} catch (NotExists notExists) {}
					}
				};
			}
		);
	}

	@AlwaysThrows @Override protected StreamStorage getStorageToWrite(String name) { throw new NotSupported.Runtime("MergeStreamStorage get read only"); }


	public MergeStreamStorage(Sequence<StreamStorage> storages) {
		this.storages = storages;
	}


}
