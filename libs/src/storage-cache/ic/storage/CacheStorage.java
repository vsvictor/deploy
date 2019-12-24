package ic.storage;


import ic.annotations.Narrowing;
import ic.annotations.Repeat;
import ic.annotations.ToOverride;
import ic.interfaces.condition.Condition1;
import ic.parallel.Thread;
import ic.struct.list.List;
import ic.struct.map.EditableMap;
import ic.struct.map.EphemeralValuesMap;
import ic.struct.order.OrderedCountableSet;
import ic.struct.order.OrderedEditableSet;
import ic.struct.variable.Soft;
import ic.throwables.*;
import ic.struct.set.EditableSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;
import static ic.throwables.NotExists.NOT_EXISTS;


public class CacheStorage extends Storage.BaseStorage {


	private final @NotNull Storage sourceStorage;


	private final EphemeralValuesMap<Object, String> cache = new EphemeralValuesMap<>(Soft::new);


	private final OrderedEditableSet<String> keysCache;


	private final EditableSet<String> keysToWrite = new EditableSet.Default<>();
	private final EditableMap<Object, String> valuesToWrite = new EditableMap.Default<>();

	private volatile Thread writerThread;

	protected synchronized void scheduleWriteToStorage(String name, @Nullable Object value) {

		keysToWrite.addIfNotExists(name);
		valuesToWrite.set(name, value);

		if (writerThread == null) {

			writerThread = new Thread() { @Override protected void doInParallel() {

				while (true) {

					try {

						final String keyToWrite;
						final Object valueToWrite; {

							synchronized (CacheStorage.this) {

								try {
									keyToWrite = keysToWrite.findOrThrow(new Condition1.True<String>());
									valueToWrite = valuesToWrite.get(keyToWrite);
									keysToWrite.remove(keyToWrite);
									valuesToWrite.set(keyToWrite, null);
								} catch (NotExists empty) {
									writerThread = null;
									break;
								}

							}

						}

						sourceStorage.set(keyToWrite, valueToWrite);

					} catch (Throwable throwable) { throwable.printStackTrace(); }

				}

			} };

			writerThread.start();

		}

	}

	@ToOverride protected void onWriteToCache(final String key, final @Nullable Object oldValue, final @Nullable Object newValue) {}

	private synchronized void writeToCache(final String key, final @Nullable Object oldValue, final @Nullable Object value) {
		onWriteToCache(key, oldValue, value);
		cache.set(key, value);
	}


	// Storage implementation:

	@Override public synchronized OrderedCountableSet<String> getKeys() {
		return new OrderedCountableSet.Default<>(ALPHABETIC_STRING_ORDER, keysCache);
	}

	@Override public synchronized boolean isFolder(@NotNull String key) throws NotExists {
		if (!keysCache.contains(key)) throw NOT_EXISTS;
		try {
			return cache.getOrThrow(key) instanceof Storage;
		} catch (NotExists notExistsInCache) {
			try {
				return sourceStorage.isFolder(key);
			} catch (NotExists notExistsInSourceStorage) { throw new InconsistentData(notExistsInSourceStorage); }
		}
	}

	@ToOverride protected CacheStorage wrapFolder(Storage folder) {
		return new CacheStorage(folder);
	}

	@Override public synchronized Object getObject(
		@NotNull final String key, @NotNull List<Class<?>> additionalArgClasses, @NotNull List<Object> additionalArgs
	) {
		if (!keysCache.contains(key)) return null;
		try {
			return cache.getOrThrow(key);
		} catch (NotExists notExistsInCache) {
			Object objectFromSourceStorage; try {
				objectFromSourceStorage = sourceStorage.getOrThrow(key, additionalArgClasses, additionalArgs);
			} catch (NotExists notExistsInSourceStorage) { throw new InconsistentData(notExistsInSourceStorage); }
			if (objectFromSourceStorage instanceof Storage) { Storage storageFromSourceStorage = (Storage) objectFromSourceStorage;
				objectFromSourceStorage = wrapFolder(storageFromSourceStorage);
			}
			writeToCache(key, null, objectFromSourceStorage);
			return objectFromSourceStorage;
		}
	}

	@Override protected Object implementGetNonFolder(
		@NotNull String key, @NotNull List<Class<?>> additionalArgClasses, @NotNull List<Object> additionalArgs
	) throws NotExists {
		return getOrThrow(key, additionalArgClasses, additionalArgs);
	}

	@Override protected CacheStorage implementGetFolder(@NotNull String key) throws NotExists {
		return getOrThrow(key);
	}

	@Override protected synchronized void implementSetNonFolder(@NotNull String key, Object value) {
		if (!keysCache.contains(key) && value == null) return;
		final @Nullable Object existingCacheEntry = cache.get(key);
		if (existingCacheEntry != null) {
			if (existingCacheEntry == value) {
				return;
			}
		}
		if (value == null) {
			keysCache.removeIfExists(key);
		} else {
			keysCache.addIfNotExists(key);
		}
		writeToCache(key, existingCacheEntry, value);
		scheduleWriteToStorage(key, value);
	}

	@Override public void removeOrThrow(String key) throws NotExists {
		implementSetNonFolder(key, null);
	}

	@Override protected CacheStorage implementCreateFolder(@NotNull String key) throws AlreadyExists {
		final Storage folder = sourceStorage.createFolderOrThrow(key);
		keysCache.add(key);
		return wrapFolder(folder);
	}

	@Override public Storage getParent() { throw new NotSupported.Runtime(); }

	@Override public String getName() { return sourceStorage.getName(); }


	// Narrowing methods:

	@Narrowing @Repeat @Override public synchronized CacheStorage createFolderIfNotExists(String key) throws InvalidValue.Runtime { return (CacheStorage) super.createFolderIfNotExists(key); }


	// Constructors:

	public CacheStorage(@NotNull Storage sourceStorage) {
		this.sourceStorage = sourceStorage;
		this.keysCache = new OrderedEditableSet.Default<>(ALPHABETIC_STRING_ORDER, sourceStorage.getKeys());
	}


}
