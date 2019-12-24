package ic.storage;


import ic.annotations.Redirect;
import ic.serial.json.JsonSerializer;
import ic.stream.ByteInput;
import ic.stream.CancelableByteOutput;
import ic.struct.list.List;
import ic.struct.order.OrderedCountableSet;
import ic.throwables.*;
import ic.text.Charset;
import ic.throwables.AlreadyExists;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static ic.json.Quotes.quote;


public class JsonStorage extends StreamStorage.BaseStreamStorage {


	private final StreamStorage sourceStorage;


	// Reading and writing json:

	@Override protected Object implementGetNonFolder(
		String key, @NotNull List<Class<?>> additionalArgClasses, @NotNull List<Object> additionalArgs
	) throws NotExists {
		final ByteInput input = sourceStorage.getInput(key);
		try {
			return JsonSerializer.safeParse(
				Charset.DEFAULT_UNIX.bytesToString(input.read()),
				additionalArgClasses.toArray(Class.class),
				additionalArgs.toArray()
			);
		} catch (UnableToParse unableToParse) { throw new UnableToParse.Runtime(unableToParse);
		} finally {
			input.close();
		}
	}

	@Override protected void implementSetNonFolder(@NotNull String key, @NotNull Object value) {
		final Object json = JsonSerializer.serialize(value, true);
		final String string; {
			if (json instanceof JSONObject) { final JSONObject jsonObject = (JSONObject) json;
				string = jsonObject.toString(4);
			} else
			if (json instanceof String) { final String jsonString = (String) json;
				string = quote(jsonString);
			}
			else string = json.toString();
		}
		write(key, Charset.DEFAULT_UNIX.stringToByteSequence(string));
	}


	// Redirecting sourceStorage methods:

	@Redirect @Override public OrderedCountableSet<String> getKeys() 			{ return sourceStorage.getKeys(); 		}
	@Redirect @Override public boolean isFolder(String key) throws NotExists 	{ return sourceStorage.isFolder(key); 	}
	@Redirect @Override public void removeOrThrow(String key) throws NotExists 	{ sourceStorage.removeOrThrow(key); 	}
	@Redirect @Override public ByteInput getInput(String key) throws NotExists 	{ return sourceStorage.getInput(key); 	}
	@Redirect @Override public CancelableByteOutput getOutput(String key) 		{ return sourceStorage.getOutput(key); 	}


	// Wrapping folders:

	@Override protected Storage implementGetFolder(String key) throws NotExists {
		return new JsonStorage(sourceStorage.getFolderOrThrow(key));
	}

	@Override protected Storage implementCreateFolder(String key) throws AlreadyExists {
		return new JsonStorage(sourceStorage.createFolderOrThrow(key));
	}


	// Not supported:

	@Override public Storage getParent() 	{ throw new NotSupported.Runtime(); }
	@Override public String getName() 		{ throw new NotSupported.Runtime(); }


	// Constructors:

	public JsonStorage(StreamStorage sourceStorage) {
		this.sourceStorage = sourceStorage;
	}


}
