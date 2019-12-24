package ic.storage;


import ic.stream.ByteInput;
import ic.stream.CancelableByteOutput;
import ic.struct.order.OrderedCountableSet;
import ic.throwables.AlreadyExists;
import ic.throwables.NotExists;
import ic.throwables.NotSupported;
import org.jetbrains.annotations.Nullable;

import static ic.throwables.AccessDenied.Runtime.ACCESS_DENIED_RUNTIME;


public abstract class DistributedStreamStorage extends StreamStorage.BaseStreamStorage {


	// Reading:

	@Override public OrderedCountableSet<String> getKeys() { throw new NotSupported.Runtime(); }

	protected abstract StreamStorage getStorageToRead(String name) throws NotExists;

	@Override public ByteInput getInput(String key) throws NotExists {
		return getStorageToRead(key).getInput(key);
	}

	@Override public boolean isFolder(String key) throws NotExists {
		return getStorageToRead(key).isFolder(key);
	}


	// Writing:

	protected abstract @Nullable StreamStorage getStorageToWrite(String key) throws NotExists;

	@Override public CancelableByteOutput getOutput(String key) {
		try {
			return getStorageToWrite(key).getOutput(key);
		} catch (NotExists notExists) { throw ACCESS_DENIED_RUNTIME; }
	}

	@Override protected Storage implementCreateFolder(String key) throws AlreadyExists {
		try {
			return getStorageToWrite(key).createFolderOrThrow(key);
		} catch (NotExists notExists) { throw ACCESS_DENIED_RUNTIME; }
	}

	@Override public void removeOrThrow(String key) throws NotExists {
		try {
			getStorageToWrite(key).removeOrThrow(key);
		} catch (NotExists notExists) { throw ACCESS_DENIED_RUNTIME; }
	}


	@Override public Storage getParent() 	{ throw new NotSupported.Runtime(); }
	@Override public String getName() 		{ throw new NotSupported.Runtime(); }


}
