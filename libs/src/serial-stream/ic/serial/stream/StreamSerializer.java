package ic.serial.stream;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.annotations.Overload;
import ic.annotations.Redirect;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;
import ic.stream.ByteSequence;
import ic.struct.list.List;
import ic.struct.set.CountableSet;
import ic.struct.set.EditableSet;
import ic.text.Text;
import ic.util.Arrays;
import ic.text.Charset;
import ic.throwables.*;
import kotlin.Pair;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ic.throwables.End.END;
import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;
import static ic.util.Bytes.*;
import static ic.util.reflect.Classes.getClassByName;
import static ic.util.reflect.Classes.instantiate;


@Deprecated
public @Degenerate class StreamSerializer { @Hide private StreamSerializer() {}


	// Write:

	private static void writeClassName(@NotNull final ByteOutput output, @Nullable Class c) {
		output.write(Charset.UTF_8.stringToByteArray(c == null ? "null" : c.getName()));
		output.put((byte) ' ');
	}

	public static void write(@NotNull final ByteOutput output, @Nullable Object object) {

		if (object == null) {

			writeClassName(output, null);

		} else

		if (object instanceof Unit) {
			writeClassName(output, null);
		} else

		if (object instanceof String) { final String string = (String) object;
			writeClassName(output, String.class);
			final byte[] byteArray = Charset.UTF_8.stringToByteArray(string);
			output.write(intToByteArray(byteArray.length));
			output.write(byteArray);
		} else

		if (object instanceof Text) { final Text text = (Text) object;
			writeClassName(output, Text.class);
			final byte[] byteArray = Charset.UTF_8.textToByteArray(text);
			output.write(intToByteArray(byteArray.length));
			output.write(byteArray);
		} else

		if (object instanceof Integer) { final Integer i = (Integer) object;

			writeClassName(output, Integer.class);
			output.write(intToByteArray(i));

		} else

		if (object instanceof Long) { final Long l = (Long) object;

			writeClassName(output, Long.class);
			output.write(longToByteArray(l));

		} else

		if (object instanceof Byte) { final Byte b = (Byte) object;

			writeClassName(output, Byte.class);
			output.put(b);

		} else

		if (object instanceof Boolean) { final Boolean b = (Boolean) object;

			writeClassName(output, Boolean.class);
			output.put(b ? (byte) 0xFF : (byte) 0x00);

		} else

		if (object instanceof StreamSerializable) { final StreamSerializable serializable = (StreamSerializable) object;
			final Class classToDeclare = serializable.getClassToDeclare();
			writeClassName(output, classToDeclare);
			serializable.serialize(output);
		} else

		if (object instanceof ByteSequence) { final ByteSequence byteSequence = (ByteSequence) object;
			writeClassName(output, ByteSequence.class);
			final byte[] bytes = byteSequence.toByteArray();
			output.write(intToByteArray(bytes.length));
			output.write(bytes);
		} else

		if (object instanceof List) { @SuppressWarnings("unchecked") final List<Object> list = (List<Object>) object;
			writeClassName(output, List.class);
			synchronized (list) {
				output.write(intToByteArray(list.getLength()));
				list.forEach(item -> write(output, item));
			}
		} else

		if (object instanceof EditableSet) { @SuppressWarnings("unchecked") final EditableSet<Object> editableSet = (EditableSet<Object>) object;
			writeClassName(output, EditableSet.class);
			synchronized (editableSet) {
				output.write(intToByteArray(editableSet.getCount()));
				editableSet.forEach(item -> write(output, item));
			}
		} else

		if (object instanceof CountableSet) { @SuppressWarnings("unchecked") final CountableSet<Object> countableSet = (CountableSet<Object>) object;
			writeClassName(output, CountableSet.class);
			synchronized (countableSet) {
				output.write(intToByteArray(countableSet.getCount()));
				countableSet.forEach(item -> write(output, item));
			}
		} else

		if (object instanceof Pair) { @SuppressWarnings("unchecked") final Pair<Object, Object> pair = (Pair<Object, Object>) object;
			writeClassName(output, Pair.class);
			write(output, pair.getFirst());
			write(output, pair.getSecond());
		} else

		if (object instanceof Throwable) { final Throwable throwable = (Throwable) object;
			writeClassName(output, object.getClass());
			write(output, throwable.getMessage());
		}

		else throw new WrongType.Runtime(object);

	}


	// Read:

	@SuppressWarnings("unchecked") private static Object implementParse(

		@NotNull final ByteInput input,

		@NotNull Class[] additionalArgClasses, @NotNull Object[] additionalArgs

	) throws UnableToParse {

		assert additionalArgClasses.length == additionalArgs.length;

		try {

			String declaredClassName = Charset.UTF_8.bytesToString(input.safeReadTill((byte) ' ')); {

				if (declaredClassName.isEmpty()) throw END;

				if (declaredClassName.equals("null")) {
					return null;
				}

				if (declaredClassName.equals("ic.object.tuple.Pair")) declaredClassName = "kotlin.Pair"; // Backward compatibility
				if (declaredClassName.equals("ic.struct.tuple.Pair")) declaredClassName = "kotlin.Pair"; // Backward compatibility

			}

			final Class<?> declaredClass = getClassByName(declaredClassName);

			if (declaredClassName.equals(String.class.getName())) {
				final int length = bytesToInt(
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte()
				);
				return Charset.UTF_8.bytesToString(input.read(length));
			} else

			if (declaredClassName.equals(Text.class.getName())) {
				final int length = bytesToInt(
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte()
				);
				return Charset.UTF_8.bytesToText(input.read(length));
			} else

			if (declaredClassName.equals(Integer.class.getName())) {

				return bytesToInt(
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte()
				);

			} else

			if (declaredClassName.equals(Long.class.getName())) {

				return bytesToLong(
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte()
				);

			} else

			if (declaredClassName.equals(Byte.class.getName())) {

				return input.getByte();

			} else

			if (declaredClassName.equals(Boolean.class.getName())) {

				return input.getByte() != 0x00;

			} else

			if (declaredClassName.equals(ByteSequence.class.getName())) {
				final int count = bytesToInt(
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte()
				);
				return input.read(count);
			} else

			if (declaredClassName.equals(CountableSet.class.getName())) {
				final int count = bytesToInt(
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte()
				);
				final java.util.List<Object> javaList = new java.util.ArrayList<>();
				for (int i = 0; i < count; i++) {
					javaList.add(read(input));
				}
				return new CountableSet.Default<>(javaList);
			} else

			if (declaredClassName.equals(List.class.getName())) {
				final int count = bytesToInt(
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte()
				);
				final java.util.List<Object> javaList = new java.util.ArrayList<>();
				for (int i = 0; i < count; i++) {
					javaList.add(read(input));
				}
				return new List.Default<>(javaList);
			} else

			if (declaredClassName.equals(EditableSet.class.getName())) {
				final int count = bytesToInt(
					input.getByte(),
					input.getByte(),
					input.getByte(),
					input.getByte()
				);
				final java.util.List<Object> javaList = new java.util.ArrayList<>();
				for (int i = 0; i < count; i++) {
					javaList.add(read(input));
				}
				return new EditableSet.Default<>(javaList);
			} else

			if (declaredClassName.equals(Pair.class.getName())) {
				return new Pair<>(read(input), read(input));
			} else

			if (StreamSerializable.class.isAssignableFrom(declaredClass)) {

				final Class<? extends StreamSerializable> classToInstantiate; {
					Class<? extends StreamSerializable> value;
					try {
						@SuppressWarnings("unchecked")
						final Class<? extends StreamSerializable> value2 = (Class<? extends StreamSerializable>) declaredClass.getDeclaredField("SERIALIZABLE_CLASS_TO_INSTANTIATE").get(null);
						value = value2;
					} catch (IllegalAccessException e) 	{ throw new AccessDenied.Runtime(e);
					} catch (NoSuchFieldException e) 	{
						value = (Class<? extends StreamSerializable>) declaredClass;
					}
					classToInstantiate = value;
				}

				return instantiate(
					classToInstantiate,
					Arrays.join(Class.class,
						new Class<?>[] { ByteInput.class },
						additionalArgClasses
					),
					Arrays.join(Object.class,
						new Object[] { input },
						additionalArgs
					)
				);

			} else

			if (Throwable.class.isAssignableFrom(declaredClass)) {
				final String message = read(input);
				try {
					return instantiate(declaredClass, new Class[] { String.class }, new Object[] { message });
				} catch (Throwable e) {
					return instantiate(declaredClass.getSuperclass(), new Class[] { String.class }, new Object[] { message });
				}
			}

			else throw new WrongValue.Runtime("declaredClass: " + declaredClassName);

		} catch (Throwable throwable) {
			if (throwable instanceof IOException || throwable instanceof IOException.Runtime) throwAsUnchecked(throwable);
			throw new UnableToParse(throwable);
		}

	}


	public static @Redirect @SuppressWarnings("unchecked") <Type> Type read(
		@NotNull ByteInput input,
		@NotNull Class[] additionalArgClasses, @NotNull Object[] additionalArgs
	) throws UnableToParse {
		return (Type) implementParse(input, additionalArgClasses, additionalArgs);
	}

	public static @Overload @Redirect @SuppressWarnings("unchecked") <Type> Type read(
		@NotNull ByteInput input
	) throws UnableToParse {
		return (Type) implementParse(input, new Class[] {}, new Object[] {});
	}


}
