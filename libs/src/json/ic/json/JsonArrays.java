package ic.json;


import ic.interfaces.getter.Safe3Getter1;
import ic.struct.list.List;
import kotlin.jvm.functions.Function1;
import org.json.JSONArray;
import org.json.JSONException;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;

import static ic.util.Arrays.javaListToArray;


public class JsonArrays {


	public static <Item, JsonItem> java.util.List<Item> jsonArrayToJavaList(JSONArray jsonArray, Function1<JsonItem, Item> converter) throws JSONException, UnableToParse {
		final java.util.List<Item> items = new java.util.ArrayList<Item>();
		final int jsonArrayLength = jsonArray.length();
		for (int i = 0; i < jsonArrayLength; i++) {
			@SuppressWarnings("unchecked")
			final JsonItem jsonItem = (JsonItem) jsonArray.get(i);
			try {
				items.add(converter.invoke(jsonItem));
			} catch (JSONException jsonException) { throw new UnableToParse(jsonException);
			} catch (Skip skip) {}
		}
		return items;
	}

	public static <Item, JsonItem> List<Item> jsonArrayToList(
		JSONArray jsonArray, Function1<JsonItem, Item> converter
	) throws JSONException, UnableToParse {
		return new List.FromJavaList<Item>(jsonArrayToJavaList(jsonArray, converter));
	}

	public static <Item, JsonItem> Item[] jsonArrayToArray(
		JSONArray jsonArray, Class<Item> itemClass, Safe3Getter1<Item, JsonItem, JSONException, UnableToParse, Skip> converter
	) throws JSONException, UnableToParse {
		return javaListToArray(jsonArrayToJavaList(jsonArray, converter), itemClass);
	}


	public static <Item, JsonItem> JSONArray toJsonArray(
		Iterable<Item> items, Function1<Item, JsonItem> converter
	) throws JSONException {
		final JSONArray jsonArray = new JSONArray();
		for (Item item : items) {
			try {
				jsonArray.put(converter.invoke(item));
			} catch (Skip skip) {}
		}
		return jsonArray;
	}


}
