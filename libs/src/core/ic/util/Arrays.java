package ic.util;


import java.lang.reflect.Array;
import java.util.ArrayList;

import ic.annotations.Overload;
import ic.interfaces.condition.Condition1;
import ic.interfaces.getter.SafeGetter1;
import ic.throwables.NotExists;
import ic.throwables.Skip;


public class Arrays {


	public static <Item> Item[] createArray(Class<? super Item> itemClass, int length) {
		@SuppressWarnings("unchecked")
		final Item[] array = (Item[]) Array.newInstance(itemClass, length);
		return array;
	}

	public static byte[] createByteArray(byte... bytes) {
		return bytes;
	}

	public static <Item> Item[] createArray(int length) {
		return createArray(Object.class, length);
	}

	public static <Item> Item[] createArray(Item item1, Item item2) {
		@SuppressWarnings("unchecked")
		final Item[] array = (Item[]) new Object[] { item1, item2 };
		return array;
	}


	public static <Item> Class<Item> getItemClass(Item[] array) {
		@SuppressWarnings("unchecked")
		final Class<Item> itemClass = (Class<Item>) array.getClass().getComponentType();
		return itemClass;
	}


	public static <OldItemType, NewItemType> NewItemType[] convert(OldItemType[] oldArray, Class<NewItemType> newItemClass, SafeGetter1<NewItemType, OldItemType, Skip> converter) {
		final java.util.List<NewItemType> newItemList = new ArrayList<NewItemType>();
		for (int i = 0; i < oldArray.length; i++) {
			try {
				newItemList.add(converter.get(oldArray[i]));
			} catch (Skip skip) {}
		}
		return javaListToArray(newItemList, newItemClass);
	}

	public static <OldItem, NewItem> NewItem[] convert(Iterable<OldItem> oldIterable, Class<? super NewItem> newItemClass, SafeGetter1<NewItem, OldItem, Skip> converter) {
		final java.util.List<NewItem> newItemList = new ArrayList<NewItem>();
		for (OldItem oldItem : oldIterable) {
			try {
				newItemList.add(converter.get(oldItem));
			} catch (Skip skip) {}
		}
		return javaListToArray(newItemList, newItemClass);
	}

	public static <Item> Item[] filter(Item[] array, final Condition1<Item> filter) {
		return convert(array, getItemClass(array), item -> {
			if (filter.is(item)) return item;
			else throw Skip.SKIP;
		});
	}

	public static <Item> Item[] copy(Item[] array, int length) {
		final Item[] newArray = createArray(getItemClass(array), length);
		for (int i = 0; i < length && i < array.length; i++) {
			newArray[i] = array[i];
		}
		return newArray;
	}

	@Overload public static byte[] copy(byte[] array, int length) {
		final byte[] newArray = new byte[length];
		for (int i = 0; i < length && i < array.length; i++) {
			newArray[i] = array[i];
		}
		return newArray;
	}

	@Overload public static <Item> Item[] copy(Item[] array) {
		return copy(array, array.length);
	}

	public static <Item> Item[] cutArray(Item[] array, int begin, int end) {
		final int length = end - begin;
		final Item[] newArray = createArray(getItemClass(array), length);
		for (int i = 0; i < length; i++) {
			newArray[i] = array[begin + i];
		}
		return newArray;
	}


	public static <Item> Item[] javaListToArray(java.util.List<Item> list, Class<? super Item> itemClass) {
		final Item[] array = createArray(itemClass, list.size());
		list.toArray(array);
		return array;
	}

	public static <ItemType> java.util.List<ItemType> arrayToJavaList(ItemType[] array) {
		return new ArrayList<ItemType>(java.util.Arrays.asList(array));
	}

	public static <Item> Item[] iterableToArray(Iterable<Item> iterable, Class<? super Item> itemClass) {
		return convert(iterable, itemClass, item -> item);
	}

	public static <Item> Item[] iterableToArray(Iterable<Item> iterable) {
		return iterableToArray(iterable, Object.class);
	}


	public static byte[] join(byte[]... parts) {
		final int length; {
			int value = 0;
			for (byte[] part : parts) value += part.length;
			length = value;
		}
		final byte[] array = new byte[length];
		int index = 0;
		for (byte[] part : parts) {
			for (byte b : part) {
				array[index] = b;
				index++;
			}
		}
		return array;
	}

	public static @SafeVarargs <Item> Item[] join(Class<Item> itemClass, Item[]... parts) {
		final int length; {
			int value = 0;
			for (Item[] part : parts) value += part.length;
			length = value;
		}
		final Item[] array = createArray(itemClass, length);
		int index = 0;
		for (Item[] part : parts) {
			for (Item b : part) {
				array[index] = b;
				index++;
			}
		}
		return array;
	}

	public static <Item> int findIndex(Item[] array, Condition1<Item> searcher) throws NotExists {
		for (int i = 0; i < array.length; i++) {
			if (searcher.is(array[i])) return i;
		} throw new NotExists();
	}

	public static <Item> Item find(Item[] array, Condition1<Item> searcher) throws NotExists {
		return array[findIndex(array, searcher)];
	}


	public static <ItemType> ItemType[] remove(ItemType[] array, int index) {
		@SuppressWarnings("unchecked")
		ItemType[] newArray = (ItemType[]) Array.newInstance(array.getClass().getComponentType(), array.length - 1);
		for (int i = 0; i < array.length; i++) {
			newArray[i < index ? i : i - 1] = array[i];
		}
		return newArray;
	}

	public static <ItemType> ItemType[] remove(ItemType[] array, Condition1<ItemType> searcher) throws NotExists {
		return remove(array, findIndex(array, searcher));
	}


}
