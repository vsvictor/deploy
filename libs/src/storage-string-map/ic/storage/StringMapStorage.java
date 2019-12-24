package ic.storage;


import ic.interfaces.closeable.Closeable;
import ic.interfaces.getter.Getter1;
import ic.interfaces.openable.Openable;
import ic.interfaces.setter.Setter1;
import ic.serial.stringmap.StringMapSerializer;
import ic.struct.list.List;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;


public abstract class StringMapStorage extends Storage.BaseStorage {


	protected abstract Getter1<String, String> getInput(String name) throws NotExists;

	protected abstract Setter1<String, String> getOutput(String name);


	@Override protected Object implementGetNonFolder(
		@NotNull String key, @NotNull List<Class<?>> additionalArgClasses, @NotNull List<Object> additionalArgs
	) throws NotExists {

		final Getter1<String, String> input = getInput(key);

		{

			if (input instanceof Openable) { final Openable openable = (Openable) input;
				openable.open();
			}

		} try {

			return StringMapSerializer.read(input, additionalArgClasses.toArray(Class.class), additionalArgs.toArray());

		} catch (UnableToParse unableToParse) { throw new UnableToParse.Runtime(unableToParse);
		} finally {

			if (input instanceof Closeable) { final Closeable closeable = (Closeable) input;
				closeable.close();
			}

		}

	}


	@Override protected void implementSetNonFolder(@NotNull String key, @NotNull Object value) {

		final Setter1<String, String> output = getOutput(key);

		if (output instanceof Openable) { final Openable openable = (Openable) output;
			openable.open();
		}

		StringMapSerializer.write(output, value);

		if (output instanceof Closeable) { final Closeable closeable = (Closeable) output;
			closeable.close();
		}

	}


}
