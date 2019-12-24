package ic.storage;


import ic.annotations.NotEmpty;
import ic.annotations.ToOverride;
import ic.interfaces.emptiable.Emptiable;
import ic.interfaces.getter.UntypedGetter3;
import ic.interfaces.remover.Remover1;
import ic.interfaces.setter.UntypedGetterSetter1;
import ic.struct.list.List;
import ic.struct.order.OrderedCountableSet;
import ic.struct.sequence.Series;
import ic.text.SplitText;
import ic.text.Text;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface Storage extends
	UntypedGetterSetter1<String>,
	UntypedGetter3<String, List<Class<?>>, List<Object>>,
	Remover1<String>,
	Emptiable
{


	OrderedCountableSet<String> getKeys();

	boolean contains(String key);


	boolean isFolder(String key) throws NotExists;

	Storage getFolder(String key) throws NotExists.Runtime;
	Storage getFolderOrThrow(String key) throws NotExists;

	Storage createFolder(String key) throws AlreadyExists.Runtime;
	Storage createFolderOrThrow(String key) throws AlreadyExists;
	@NotNull Storage createFolderIfNotExists(String key) throws WrongValue.Runtime;


	@Nullable String getName();

	@Nullable Storage getParent();

	void remove();
	void replace(Object value);

	default <Type> Type recursiveGetOrThrow(String path) throws NotExists {
		Object object = this;
		final Series<Text> pathIterator = new SplitText(path, '/').getIterator();
		while (true) {
			try {
				final String key = pathIterator.getNotNull().getStringValue();
				final Storage storage = (Storage) object;
				object = storage.getOrThrow(key);
			} catch (End end) {
				@SuppressWarnings("unchecked")
				final Type result = (Type) object;
				return result;
			}
		}
	}


	default <Type> Type recursiveGet(String path) {
		try {
			return recursiveGetOrThrow(path);
		} catch (NotExists notExists) { return null; }
	}


	abstract class BaseStorage implements Storage {


		// Keys:

		@ToOverride
		@Override public boolean contains(@NotNull String key) {
			return getKeys().contains(key);
		}


		protected abstract Storage implementGetFolder(@NotNull String key) throws NotExists;
		protected abstract Object implementGetNonFolder(@NotNull String key, @NotNull List<Class<?>> additionalArgClasses, @NotNull List<Object> additionalArgs) throws NotExists;

		public synchronized @Nullable Object getObject(
			@NotNull @NotEmpty String key,
			@NotNull List<Class<?>> additionalArgClasses, @NotNull List<Object> additionalArgs
		) {
			try {
				if (isFolder(key)) {
					try {
						return implementGetFolder(key);
					} catch (NotExists notExists) { throw new InconsistentData(notExists); }
				} else {
					try {
						return implementGetNonFolder(key, additionalArgClasses, additionalArgs);
					} catch (NotExists notExists) { throw new InconsistentData(notExists); }
				}
			} catch (NotExists notExists) {
				return null;
			}
		}


		// UntypedGetterSetter1<Object, String> implementation:

		public @Nullable Object getObject(String key) {
			return getObject(key, new List.Default<>(), new List.Default<>());
		}

		protected abstract void implementSetNonFolder(@NotNull String key, Object value);

		protected synchronized void implementSetFolder(@NotNull String key, @NotNull final Storage folder) {
			removeIfExists(key);
			final Storage newFolder = createFolder(key);
			folder.getKeys().forEach(oldFolderKey -> newFolder.set(oldFolderKey, folder.getNotNull(oldFolderKey)));
		}

		@Override public synchronized void set(@NotNull String key, @Nullable Object value) {
			if (value == null) {
				removeIfExists(key);
			} else {
				if (value instanceof Storage) { final Storage storageValue = (Storage) value;
					implementSetFolder(key, storageValue);
				} else {
					implementSetNonFolder(key, value);
				}
			}
		}

		@Override public void remove() {
			getParent().remove(getName());
		}

		@Override public void replace(Object value) {
			getParent().set(getName(), value);
		}

		// Emptiable implementation:

		@Override public synchronized void empty() {
			getKeys().forEach(this::removeIfExists);
		}


		// Storage implementation:

		@Override public Storage getFolder(String key) throws NotExists.Runtime {
			try {
				return implementGetFolder(key);
			} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
		}

		@Override public Storage getFolderOrThrow(String key) throws NotExists {
			return implementGetFolder(key);
		}

		protected abstract Storage implementCreateFolder(@NotNull String key) throws AlreadyExists;

		@Override public Storage createFolder(@NotNull String key) throws AlreadyExists.Runtime {
			try {
				return implementCreateFolder(key);
			} catch (AlreadyExists alreadyExists) { throw new AlreadyExists.Runtime(alreadyExists); }
		}

		@Override public Storage createFolderOrThrow(@NotNull String key) throws AlreadyExists {
			return implementCreateFolder(key);
		}

		@Override public synchronized Storage createFolderIfNotExists(@NotNull String key) throws InvalidValue.Runtime {
			try {
				if (isFolder(key)) {
					try {
						return implementGetFolder(key);
					} catch (NotExists notExists) { throw new InconsistentData(notExists); }
				} else {
					throw new InvalidValue.Runtime("Not a folder: " + key);
				}
			} catch (NotExists notExists) {
				try {
					return implementCreateFolder(key);
				} catch (AlreadyExists alreadyExists) { throw new InconsistentData(alreadyExists); }
			}
		}


	}


}
