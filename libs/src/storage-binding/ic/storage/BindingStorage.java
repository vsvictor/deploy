package ic.storage;


import ic.annotations.Narrowing;
import org.jetbrains.annotations.Nullable;
import ic.annotations.Repeat;
import ic.interfaces.changeable.Changeable;
import ic.interfaces.action.Action;
import ic.struct.map.EphemeralValuesMap;
import ic.struct.variable.Soft;
import ic.throwables.InvalidValue;


public class BindingStorage extends CacheStorage {


	private final EphemeralValuesMap<Action, String> onChangeActionsCache = new EphemeralValuesMap<>(Soft::new);


	@Override protected CacheStorage wrapFolder(Storage folder) {
		return new BindingStorage(folder);
	}

	@Override protected void onWriteToCache(final String key, final @Nullable Object oldValue, final @Nullable Object newValue) {

		if (oldValue instanceof Changeable) { final Changeable changeable = (Changeable) oldValue;
			final Action onChangeAction = onChangeActionsCache.get(key);
			if (onChangeAction != null) {
				changeable.getChangeEvent().forget(onChangeAction);
			}
		}

		if (newValue == null) {

			onChangeActionsCache.set(key, null);

		} else {

			if (newValue instanceof Changeable) { final Changeable changeable = (Changeable) newValue;
				final Action onChangeAction = () -> scheduleWriteToStorage(key, newValue);
				changeable.getChangeEvent().watch(onChangeAction);
				onChangeActionsCache.set(key, onChangeAction);
			}

		}

	}


	// Narrowing methods:

	@Narrowing @Repeat @Override public synchronized BindingStorage createFolderIfNotExists(String key) throws InvalidValue.Runtime { return (BindingStorage) super.createFolderIfNotExists(key); }


	public BindingStorage(Storage sourceStorage) {
		super(sourceStorage);
	}


}
