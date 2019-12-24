package ic.storage;


import ic.annotations.Narrowing;
import ic.annotations.NotEmpty;
import ic.annotations.Repeat;
import ic.interfaces.reader.Reader1;
import ic.interfaces.writer.Writer1;
import ic.stream.ByteInput;

import ic.stream.ByteSequence;
import ic.stream.CancelableByteOutput;
import ic.struct.list.List;
import ic.text.SplitString;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;

import static ic.serial.stream.ParseKt.parseFromStream;
import static ic.serial.stream.SerializeKt.serializeToStream;


public interface StreamStorage extends Storage, Writer1<ByteSequence, String>, Reader1<ByteSequence, String> {


	ByteInput getInput(String key) throws NotExists;

	CancelableByteOutput getOutput(String key);


	default ByteSequence readOrThrow(String key) throws NotExists {
		final ByteInput input = getInput(key); try {
			return input.read();
		} finally {
			input.close();
		}
	}

	@Override default ByteSequence read(String key) {
		try {
			return readOrThrow(key);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}

	default ByteSequence recursiveReadOrThrow(String path) throws NotExists {
		final List<String> pathSplit = new List.Default<>(new SplitString(path, '/'));
		final int pathPartsCount = pathSplit.getCount();
		StreamStorage storage = this;
		for (int i = 0; i < pathPartsCount - 1; i++) {
			storage = storage.getFolderOrThrow(pathSplit.get(i));
		}
		return storage.readOrThrow(pathSplit.getLast());
	}


	@Override default void write(String key, ByteSequence value) {
		final CancelableByteOutput output = getOutput(key); try {
			output.write(value);
			output.close();
		} catch (RuntimeException|Error runtimeThrowable) {
			output.cancel();
		}
	}

	default void write(String key, byte[] bytes) {
		final CancelableByteOutput output = getOutput(key); try {
			output.write(bytes);
			output.close();
		} catch (RuntimeException|Error runtimeThrowable) {
			output.cancel();
		}
	}


	@Narrowing @Override StreamStorage getFolder(String key) throws NotExists.Runtime;
	@Narrowing @Override StreamStorage getFolderOrThrow(String key) throws NotExists;
	@Narrowing @Override StreamStorage createFolderOrThrow(String key) throws AlreadyExists;
	@Narrowing @Override StreamStorage createFolderIfNotExists(String key) throws WrongValue.Runtime;


	abstract class BaseStreamStorage extends BaseStorage implements StreamStorage {


		@Narrowing @Repeat @Override public StreamStorage getFolder(String key) throws NotExists.Runtime { return (StreamStorage) super.getFolder(key); }
		@Narrowing @Repeat @Override public StreamStorage getFolderOrThrow(String key) throws NotExists { return (StreamStorage) super.getFolderOrThrow(key); }
		@Narrowing @Repeat @Override public StreamStorage createFolderOrThrow(String key) throws AlreadyExists { return (StreamStorage) super.createFolderOrThrow(key); }
		@Narrowing @Repeat @Override public StreamStorage createFolderIfNotExists(String key) throws WrongValue.Runtime { return (StreamStorage) super.createFolderIfNotExists(key); }


		@Override protected Object implementGetNonFolder(@NotNull @NotEmpty String key, @NotNull List<Class<?>> additionalArgClasses, @NotNull List<Object> additionalArgs) throws NotExists {

			assert !key.isEmpty();

			final ByteInput input = getInput(key);

			try {

				return parseFromStream(input, additionalArgClasses, additionalArgs);

			} catch (UnableToParse unableToParse) { throw new UnableToParse.Runtime(unableToParse);
			} finally {
				input.close();
			}

		}


		@Override protected void implementSetNonFolder(@NotNull String key, @NotNull Object value) {
			final CancelableByteOutput output = getOutput(key); try {
				serializeToStream(output, value);
				output.close();
			} catch (RuntimeException|Error runtimeThrowable) {
				output.cancel();
			}
		}


	}


}
