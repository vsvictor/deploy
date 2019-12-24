package ic.struct.collection;


import org.jetbrains.annotations.NotNull;
import ic.interfaces.condition.Condition2;


public class CollectionEqualityCondition<Item> implements Condition2<Collection<Item>, Collection<Item>> {

	private final @NotNull Condition2<Item, Item> itemsEqualityChecker;

	@Override public boolean is(Collection<Item> a, Collection<Item> b) {
		for (Item itemToFind : a) {
			searchingItem: {
				for (Item item : b) {
					if (itemsEqualityChecker.is(item, itemToFind)) break searchingItem;
				}
				return false;
			}
		}
		for (Item itemToFind : b) {
			searchingItem: {
				for (Item item : a) {
					if (itemsEqualityChecker.is(item, itemToFind)) break searchingItem;
				}
				return false;
			}
		}
		return true;
	}

	public CollectionEqualityCondition(@NotNull Condition2<Item, Item> itemsEqualityChecker) {
		{ // Checking arguments:
			assert itemsEqualityChecker != null : "itemsEqualityChecker == null";
		}
		this.itemsEqualityChecker = itemsEqualityChecker;
	}

}
