package ic.serial.json;


import ic.annotations.*;
import ic.interfaces.getter.Safe3Getter1;
import ic.json.JsonArrays;
import ic.struct.list.EditableList;
import ic.struct.list.List;
import ic.struct.map.CountableMap;
import ic.struct.map.EditableMap;
import ic.struct.set.CountableSet;
import ic.throwables.*;
import ic.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static ic.json.JsonArrays.toJsonArray;
import static ic.util.reflect.Classes.instantiate;


public @Degenerate class JsonSerializer { @Hide private JsonSerializer() {}


	// Serializing:

	public static Object serialize(Object object, boolean allowPrimitives) throws JSONException {

		final JSONObject json = new JSONObject();

		if (object == null) {
			if (allowPrimitives) return null;
			json.put(JsonSerializable.KEY_CLASS, "null");
		} else

		if (object instanceof String) {
			if (allowPrimitives) return object;
			final String string = (String) object;
			json.put(JsonSerializable.KEY_CLASS, String.class.getName());
			json.put("value", string);
		} else

		if (object instanceof Integer) {
			if (allowPrimitives) return object;
			final Integer integer = (Integer) object;
			json.put(JsonSerializable.KEY_CLASS, Integer.class.getName());
			json.put("value", integer);
		} else

		if (object instanceof Long) {
			if (allowPrimitives) return object;
			final Long l = (Long) object;
			json.put(JsonSerializable.KEY_CLASS, Long.class.getName());
			json.put("value", l);
		} else

		if (object instanceof CountableSet) {
			@SuppressWarnings("unchecked")
			final CountableSet<Object> countableSet = (CountableSet) object;
			json.put(JsonSerializable.KEY_CLASS, CountableSet.class.getName());
			synchronized (countableSet) {
				json.put("items", toJsonArray(countableSet, item -> JsonSerializer.serialize(item, allowPrimitives)));
			}
		} else

		if (object instanceof EditableList) {
			@SuppressWarnings("unchecked")
			final EditableList<Object> list = (EditableList) object;
			json.put(JsonSerializable.KEY_CLASS, EditableList.class.getName());
			synchronized (list) {
				json.put("items", toJsonArray(list, item -> JsonSerializer.serialize(item, allowPrimitives)));
			}
		} else

		if (object instanceof List) {
			@SuppressWarnings("unchecked")
			final List<Object> list = (List) object;
			json.put(JsonSerializable.KEY_CLASS, List.class.getName());
			synchronized (list) {
				json.put("items", toJsonArray(list, item -> JsonSerializer.serialize(item, allowPrimitives)));
			}
		} else

		if (object instanceof EditableMap) {
			@SuppressWarnings("unchecked")
			final EditableMap<Object, Object> editableMap = (EditableMap) object;
			json.put(JsonSerializable.KEY_CLASS, EditableMap.class.getName());
			synchronized (editableMap) {
				json.put("entries", toJsonArray(editableMap.getKeys(), key -> {
					final JSONObject entryJson = new JSONObject();
					entryJson.put("key", JsonSerializer.serialize(key, allowPrimitives));
					entryJson.put("value", JsonSerializer.serialize(editableMap.get(key), allowPrimitives));
					return entryJson;
				}));
			}
		} else

		if (object instanceof CountableMap) {
			@SuppressWarnings("unchecked")
			final CountableMap<Object, Object> countableMap = (CountableMap) object;
			json.put(JsonSerializable.KEY_CLASS, CountableMap.class.getName());
			synchronized (countableMap) {
				json.put("entries", toJsonArray(countableMap.getKeys(), key -> {
					final JSONObject entryJson = new JSONObject();
					entryJson.put("key", JsonSerializer.serialize(key, allowPrimitives));
					entryJson.put("value", JsonSerializer.serialize(countableMap.get(key), allowPrimitives));
					return entryJson;
				}));
			}
		} else

		if (object instanceof JSONObject) {
			return object;
		} else

		if (object instanceof JsonSerializable) { final JsonSerializable jsonSerializable = (JsonSerializable) object;
			final Class<?> classToDeclare = jsonSerializable.getClassToDeclare();
			json.put(JsonSerializable.KEY_CLASS, classToDeclare.getName());
			jsonSerializable.serialize(json);
		}

		else throw new WrongType.Runtime(object);

		return json;

	}


	// Parsing:

	private static Object implementParse(

		@Nullable Object json,

		@NotNull Class[] additionalArgClasses,
		@NotNull Object[] additionalArgs

	) throws JSONException, UnableToParse {

		assert additionalArgClasses.length == additionalArgs.length;

		if (json == null) return null; else

		if (json instanceof String) 	return json; else
		if (json instanceof Integer) 	return json; else
		if (json instanceof Long) 		return json; else

		if (json instanceof JSONObject) { final JSONObject jsonObject = (JSONObject) json;

			final @Nullable String className; {
				String value = jsonObject.optString(JsonSerializable.KEY_CLASS, null);
				if (value == null) value = jsonObject.optString("declaredClass", null); // TODO: Backward compatibility
				className = value;
			}

			if (className == null) return json;

			if (className.equals("null")) return null;

			if (className.equals(String.class.getName())) {
				return jsonObject.getString("value");
			} else

			if (className.equals(Integer.class.getName())) {
				return jsonObject.getInt("value");
			} else

			if (className.equals(Long.class.getName())) {
				return jsonObject.getLong("value");
			}

			/*if (className.equals(List.class.getName())) {
				final JSONArray itemsJson = jsonObject.getJSONArray("items");
				final Object[] array = new Object[itemsJson.length()];
				for (int i = 0; i < array.length; i++) { final Object itemJson = itemsJson.get(i);
					array[i] = JsonSerializer.safeParse(itemJson);
				}
				return new List.FromArray<>(array);
			}*/

			if (className.equals(List.class.getName())) { // TODO
				final java.util.List<Object> javaList = new java.util.ArrayList<>();
				final JSONArray itemsJson = jsonObject.getJSONArray("items");
				for (int i = 0; i < itemsJson.length(); i++) { final Object itemJson = itemsJson.get(i);
					javaList.add(JsonSerializer.safeParse(itemJson));
				}
				return new EditableList.FromJavaList<>(javaList);
			}

			if (className.equals(EditableList.class.getName())) {
				final java.util.List<Object> javaList = new java.util.ArrayList<>();
				final JSONArray itemsJson = jsonObject.getJSONArray("items");
				for (int i = 0; i < itemsJson.length(); i++) { final Object itemJson = itemsJson.get(i);
					javaList.add(JsonSerializer.safeParse(itemJson));
				}
				return new EditableList.FromJavaList<>(javaList);
			}

			if (className.equals(CountableSet.class.getName())) {
				final java.util.Set<Object> javaSet = new java.util.HashSet<>();
				final JSONArray itemsJson = jsonObject.getJSONArray("items");
				for (int i = 0; i < itemsJson.length(); i++) { final Object itemJson = itemsJson.get(i);
					javaSet.add(JsonSerializer.safeParse(itemJson));
				}
				return new CountableSet.FromJavaSet<>(javaSet);
			}

			if (className.equals(EditableMap.class.getName())) {
				final EditableMap<Object, Object> editableMap = new EditableMap.Default<>();
				final JSONArray entriesJson = jsonObject.getJSONArray("entries");
				for (int i = 0; i < entriesJson.length(); i++) { final JSONObject entryJson = entriesJson.getJSONObject(i);
					editableMap.set(
						JsonSerializer.safeParse(entryJson.get("key")),
						JsonSerializer.safeParse(entryJson.get("value"))
					);
				}
				return editableMap;
			}

			if (className.equals(CountableMap.class.getName())) {
				final EditableMap<Object, Object> editableMap = new EditableMap.Default<>();
				final JSONArray entriesJson = jsonObject.getJSONArray("entries");
				for (int i = 0; i < entriesJson.length(); i++) { final JSONObject entryJson = entriesJson.getJSONObject(i);
					editableMap.set(
						JsonSerializer.safeParse(entryJson.get("key")),
						JsonSerializer.safeParse(entryJson.get("value"))
					);
				}
				return new CountableMap.Default<>(editableMap);
			}

			else {

				final Class<? extends JsonSerializable> declaredClass; {
					Class<? extends JsonSerializable> value; try {
						@SuppressWarnings("unchecked")
						Class<? extends JsonSerializable> c = (Class<? extends JsonSerializable>) Class.forName(className);
						value = c;
					} catch (ClassNotFoundException e) { throw new NotExists.Error("No such class " + className); }
					declaredClass = value;
				}

				final Class<? extends JsonSerializable> classToInstantiate; {
					Class<? extends JsonSerializable> value;
					try {
						@SuppressWarnings("unchecked")
						final Class<? extends JsonSerializable> value2 = (Class<? extends JsonSerializable>) declaredClass.getDeclaredField("SERIALIZABLE_CLASS_TO_INSTANTIATE").get(null);
						value = value2;
					} catch (IllegalAccessException e) 	{ throw new AccessDenied.Runtime(e);
					} catch (NoSuchFieldException e) 	{
						value = declaredClass;
					}
					classToInstantiate = value;
				}

				return instantiate(
					classToInstantiate,
					Arrays.join(Class.class,
						new Class<?>[] { JSONObject.class },
						additionalArgClasses
					),
					Arrays.join(Object.class,
						new Object[] { jsonObject },
						additionalArgs
					)
				);

			}

		}

		if (json instanceof JSONArray) { final JSONArray jsonArray = (JSONArray) json;
			return JsonArrays.jsonArrayToList(
				jsonArray,
				(Safe3Getter1<Object, Object, JSONException, UnableToParse, Skip>) JsonSerializer::safeParse
			);
		}

		else throw new WrongType.Runtime(json);

	}


	private static Object implementParse(

		@NotNull @NotEmpty String stringToParse,

		@NotNull Class[] additionalArgClasses,
		@NotNull Object[] additionalArgs

	) throws UnableToParse {

		assert !stringToParse.trim().isEmpty();

		assert additionalArgClasses.length == additionalArgs.length;

		try {
			return implementParse(new JSONArray("[" + stringToParse + "]").get(0), additionalArgClasses, additionalArgs);
		} catch (JSONException jsonException) {
			throw new UnableToParse(jsonException);
		}

	}


	public static @Redirect @SuppressWarnings("unchecked") <Type> Type safeParse(@Nullable Object json, @NotNull Class[] additionalArgClasses, @NotNull Object[] additionalArgs) throws UnableToParse, JSONException {
		return (Type) implementParse(json, additionalArgClasses, additionalArgs);
	}

	public static @Overload @Redirect @SuppressWarnings("unchecked") <Type> Type safeParse(@Nullable Object json) throws UnableToParse, JSONException {
		return (Type) implementParse(json, new Class[] {}, new Object[] {});
	}

	public static @Overload @Redirect @SuppressWarnings("unchecked") <Type> Type parse(@Nullable Object json) {
		try {
			return (Type) implementParse(json, new Class[] {}, new Object[] {});
		} catch (UnableToParse unableToParse) { throw new UnableToParse.Runtime(unableToParse); }
	}

	public static @Redirect @SuppressWarnings("unchecked") <Type> Type safeParse(@NotNull @NotEmpty String stringToParse, @NotNull Class[] additionalArgClasses, @NotNull Object[] additionalArgs) throws UnableToParse, JSONException {
		return (Type) implementParse(stringToParse, additionalArgClasses, additionalArgs);
	}

	public static @Overload @Redirect @SuppressWarnings("unchecked") <Type> Type safeParse(@NotNull @NotEmpty String stringToParse) throws UnableToParse, JSONException {
		return (Type) implementParse(stringToParse, new Class[] {}, new Object[] {});
	}


}
