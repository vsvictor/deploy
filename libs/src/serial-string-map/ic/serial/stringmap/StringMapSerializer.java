package ic.serial.stringmap;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.annotations.Overload;
import ic.annotations.Redirect;
import ic.interfaces.action.SafeAction1;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.struct.list.Array;
import ic.struct.collection.Collection;
import ic.struct.list.EditableList;
import ic.struct.list.List;
import ic.struct.map.CountableMap;
import ic.struct.map.EditableMap;
import ic.struct.set.CountableSet;
import ic.struct.set.EditableSet;
import ic.throwables.*;
import ic.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ic.util.Arrays.createArray;
import static ic.util.reflect.Classes.instantiate;
import static java.lang.Integer.parseInt;


public @Degenerate class StringMapSerializer { @Hide private StringMapSerializer() {}


	// Write:

	public static void write(@NotNull final Setter1<String, String> output, @Nullable Object object) {

		if (object == null) {
			output.set(StringMapSerializable.KEY_CLASS, "null");
		} else

		if (object instanceof String) { final String string = (String) object;
			output.set(StringMapSerializable.KEY_CLASS, String.class.getName());
			output.set("value", string);
		} else

		if (object instanceof Integer) { final Integer integer = (Integer) object;
			output.set(StringMapSerializable.KEY_CLASS, Integer.class.getName());
			output.set("value", Integer.toString(integer));
		} else

		if (object.getClass().isArray()) { final Object[] array = (Object[]) object;
			output.set(StringMapSerializable.KEY_CLASS, "[]");
			output.set("count", Integer.toString(array.length));
			for (int i = 0; i < array.length; i++) {
				write(output, Integer.toString(i), array[i]);
			}
		} else

		if (object instanceof StringMapSerializable) { final StringMapSerializable jsonSerializable = (StringMapSerializable) object;
			output.set(StringMapSerializable.KEY_CLASS, jsonSerializable.getClassToDeclare().getName());
			jsonSerializable.serialize(output);
		} else

		if (object instanceof EditableSet) { @SuppressWarnings("unchecked") final EditableSet<Object> editableSet = (EditableSet<Object>) object;
			output.set(StringMapSerializable.KEY_CLASS, EditableSet.class.getName());
			class Iterator implements SafeAction1<Object, Break> {
				int i;
				@Override public synchronized void run(Object item) {
					write(output, Integer.toString(i), item);
					i++;
				}
			}
			final Iterator iterator = new Iterator();
			editableSet.forEach(iterator);
			output.set("count", Integer.toString(iterator.i));
		} else

		if (object instanceof CountableSet) { @SuppressWarnings("unchecked") final CountableSet<Object> countableSet = (CountableSet<Object>) object;
			output.set(StringMapSerializable.KEY_CLASS, CountableSet.class.getName());
			class Iterator implements SafeAction1<Object, Break> {
				int i;
				@Override public synchronized void run(Object item) {
					write(output, Integer.toString(i), item);
					i++;
				}
			}
			final Iterator iterator = new Iterator();
			countableSet.forEach(iterator);
			output.set("count", Integer.toString(iterator.i));
		} else

		if (object instanceof EditableList) { @SuppressWarnings("unchecked") final EditableList<Object> list = (EditableList<Object>) object;
			output.set(StringMapSerializable.KEY_CLASS, EditableList.class.getName());
			synchronized (list) {
				final int count = list.getCount();
				output.set("count", Integer.toString(count));
				for (int i = 0; i < count; i++) {
					write(output, Integer.toString(i), list.get(i));
				}
			}
		} else

		if (object instanceof Array) { @SuppressWarnings("unchecked") final Array<Object> list = (Array<Object>) object;
			output.set(StringMapSerializable.KEY_CLASS, Array.class.getName());
			synchronized (list) {
				final int count = list.getCount();
				output.set("count", Integer.toString(count));
				for (int i = 0; i < count; i++) {
					write(output, Integer.toString(i), list.get(i));
				}
			}
		} else

		if (object instanceof List) { @SuppressWarnings("unchecked") final List<Object> list = (List<Object>) object;
			output.set(StringMapSerializable.KEY_CLASS, List.class.getName());
			synchronized (list) {
				final int count = list.getCount();
				output.set("count", Integer.toString(count));
				for (int i = 0; i < count; i++) {
					write(output, Integer.toString(i), list.get(i));
				}
			}
		} else

		if (object instanceof Collection) { @SuppressWarnings("unchecked") final Collection<Object> collection = (Collection<Object>) object;
			output.set(StringMapSerializable.KEY_CLASS, Collection.class.getName());
			class Iterator implements SafeAction1<Object, Break> {
				int i;
				@Override public synchronized void run(Object item) {
					write(output, Integer.toString(i), item);
					i++;
				}
			}
			final Iterator iterator = new Iterator();
			collection.forEach(iterator);
			output.set("count", Integer.toString(iterator.i));
		} else

		if (object instanceof EditableMap) { @SuppressWarnings("unchecked") final EditableMap<Object, Object> editableMap = (EditableMap<Object, Object>) object;
			output.set(StringMapSerializable.KEY_CLASS, EditableMap.class.getName());
			class Iterator implements SafeAction1<Object, Break> {
				int i;
				@Override public synchronized void run(Object key) {
					final Object value; try {
						value = editableMap.getOrThrow(key);
					} catch (NotExists notExists) { return; }
					write(output, i + "/key", key);
					write(output, i + "/value", value);
					i++;
				}
			}
			final Iterator iterator = new Iterator();
			synchronized (editableMap) {
				editableMap.getKeys().forEach(iterator);
			}
			output.set("count", Integer.toString(iterator.i));
		} else

		if (object instanceof CountableMap) { @SuppressWarnings("unchecked") final CountableMap<Object, Object> countableMap = (CountableMap<Object, Object>) object;
			output.set(StringMapSerializable.KEY_CLASS, CountableMap.class.getName());
			class Iterator implements SafeAction1<Object, Break> {
				int i;
				@Override public synchronized void run(Object key) {
					final Object value; try {
						value = countableMap.getOrThrow(key);
					} catch (NotExists notExists) { return; }
					write(output, i + "/key", key);
					write(output, i + "/value", value);
					i++;
				}
			}
			final Iterator iterator = new Iterator();
			synchronized (countableMap) {
				countableMap.getKeys().forEach(iterator);
			}
			output.set("count", Integer.toString(iterator.i));
		}

		else throw new WrongType.Runtime(object);

	}

	public static void write(@NotNull final Setter1<String, String> output, @NotNull final String key, @Nullable Object object) {

		write(
			new Setter1<String, String>() { @Override public void set(String subname, String value) {
				output.set(key + "/" + subname, value);
			} },
			object
		);

	}


	// Read:

	private static Object implementParse(

		@NotNull Getter1<String, String> input,

		@NotNull Class[] additionalArgClasses, @NotNull Object[] additionalArgs

	) throws UnableToParse {

		assert additionalArgClasses.length == additionalArgs.length;

		String className = input.get(StringMapSerializable.KEY_CLASS);

		if (className == null) return null;

		if (className.equals("null")) return null;

		if (className.equals(String.class.getName())) {
			return input.get("value");
		} else

		if (className.equals(Integer.class.getName())) {
			return parseInt(input.get("value"));
		} else

		if (className.equals("[]")) {
			final int count = parseInt(input.get("count"));
			final Object[] array = new Object[count];
			for (int i = 0; i < count; i++) {
				array[i] = read(input, Integer.toString(i));
			}
			return array;
		} else

		if (className.equals(CountableMap.class.getName())) {
			final String countString = input.get("count");
			if (countString == null) { // TODO Backward compatibility
				final int count = read(input, "count");
				final EditableMap<Object, Object> editableMap = new EditableMap.Default<Object, Object>();
				for (int i = 0; i < count; i++) {
					editableMap.set(
						read(input, "key" + i),
						read(input, "value" + i)
					);
				}
				return editableMap;
			} else {
				final int count = parseInt(countString);
				final Object[] keysAndValues = new Object[count * 2];
				for (int i = 0; i < count; i++) {
					keysAndValues[i * 2] 		= read(input, i + "/key");
					keysAndValues[i * 2 + 1] 	= read(input, i + "/value");
				}
				return new CountableMap.Default<Object, Object>(keysAndValues);
			}
		} else

		if (className.equals(EditableMap.class.getName())) {
			final String countString = input.get("count");
			if (countString == null) { // TODO Backward compatibility
				final int count = read(input, "count");
				final EditableMap<Object, Object> editableMap = new EditableMap.Default<Object, Object>();
				for (int i = 0; i < count; i++) {
					editableMap.set(
						read(input, "key" + i),
						read(input, "value" + i)
					);
				}
				return editableMap;
			} else {
				final int count = parseInt(input.get("count"));
				final EditableMap<Object, Object> editableMap = new EditableMap.Default<Object, Object>();
				for (int i = 0; i < count; i++) {
					editableMap.set(
						read(input, i + "/key"),
						read(input, i + "/value")
					);
				}
				return editableMap;
			}
		} else

		if (className.equals(Collection.class.getName())) {
			final int count = parseInt(input.get("count"));
			final Object[] items = createArray(count);
			for (int i = 0; i < count; i++) {
				items[i] = read(input, Integer.toString(i));
			}
			return new List.Default<Object>(items); // TODO: Backward compatibility
		} else

		if (className.equals(List.class.getName())) {
			final int count = parseInt(input.get("count"));
			final Object[] items = createArray(count);
			for (int i = 0; i < count; i++) {
				items[i] = read(input, Integer.toString(i));
			}
			return new EditableList.Default<Object>(items); // TODO: Backward compatibility
		}

		if (className.equals(Array.class.getName())) {
			final int count = parseInt(input.get("count"));
			final Object[] items = createArray(count);
			for (int i = 0; i < count; i++) {
				items[i] = read(input, Integer.toString(i));
			}
			return new Array.Default<Object>(items);
		}

		if (className.equals(EditableList.class.getName())) {
			final int count = parseInt(input.get("count"));
			final EditableList<Object> editableList = new EditableList.Default<Object>();
			for (int i = 0; i < count; i++) {
				editableList.add(read(input, Integer.toString(i)));
			}
			return editableList;
		}

		if (className.equals(CountableSet.class.getName())) {
			final int count = parseInt(input.get("count"));
			final Object[] items = createArray(count);
			for (int i = 0; i < count; i++) {
				items[i] = read(input, Integer.toString(i));
			}
			return new CountableSet.Default<Object>(items);
		}

		if (className.equals(EditableSet.class.getName())) {
			final int count = parseInt(input.get("count"));
			final EditableSet<Object> editableSet = new EditableSet.Default<Object>();
			for (int i = 0; i < count; i++) {
				editableSet.addIfNotExists(read(input, Integer.toString(i)));
			}
			return editableSet;
		}

		else {

			{ // TODO Backward compatibility:
				if (className.equals("ic.object.id.SimpleStringIdGenerator")) className = "ic.id.SimpleStringIdGenerator"; else
				if (className.equals("ic.object.id.SecureStringIdGenerator")) className = "ic.id.SecureStringIdGenerator";
			}

			final Class declaredClass; {
				try {
					declaredClass = Class.forName(className);
				} catch (ClassNotFoundException e) { throw new NotExists.Error("No such class " + className); }
			}

			final Class<?> classToInstantiate; {
				Class value;
				try {
					value = (Class) declaredClass.getDeclaredField("SERIALIZABLE_CLASS_TO_INSTANTIATE").get(null);
				} catch (IllegalAccessException e) 	{ throw new AccessDenied.Runtime(e);
				} catch (NoSuchFieldException e) 	{
					value = declaredClass;
				}
				classToInstantiate = value;
			}

			return instantiate(
				classToInstantiate,
				Arrays.join(Class.class,
					new Class<?>[] { Getter1.class },
					additionalArgClasses
				),
				Arrays.join(Object.class,
					new Object[] { input },
					additionalArgs
				)
			);

		}

	}


	private static Object implementParse(

		@NotNull final Getter1<String, String> input,

		@NotNull final String key,

		@NotNull Class[] additionalArgClasses, @NotNull Object[] additionalArgs

	) throws UnableToParse {

		assert additionalArgClasses.length == additionalArgs.length;

		return implementParse(
			subkey -> input.get(key + "/" + subkey),
			additionalArgClasses, additionalArgs
		);

	}


	public static @Redirect @SuppressWarnings("unchecked") <Type> Type read(@NotNull Getter1<String, String> input, @NotNull Class[] additionalArgClasses, @NotNull Object[] additionalArgs) throws UnableToParse {
		return (Type) implementParse(input, additionalArgClasses, additionalArgs);
	}

	public static @Overload @Redirect @SuppressWarnings("unchecked") <Type> Type read(@NotNull Getter1<String, String> input) throws UnableToParse {
		return (Type) implementParse(input, new Class[] {}, new Object[] {});
	}

	public static @Redirect @SuppressWarnings("unchecked") <Type> Type read(@NotNull Getter1<String, String> input, @NotNull String key, @NotNull Class[] additionalArgClasses, @NotNull Object[] additionalArgs) throws UnableToParse {
		return (Type) implementParse(input, key, additionalArgClasses, additionalArgs);
	}

	public static @Overload @Redirect @SuppressWarnings("unchecked") <Type> Type read(@NotNull Getter1<String, String> input, @NotNull String key) throws UnableToParse {
		return (Type) implementParse(input, key, new Class[] {}, new Object[] {});
	}


}
