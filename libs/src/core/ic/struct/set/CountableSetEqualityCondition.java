package ic.struct.set;


import ic.interfaces.condition.Condition2;


public class CountableSetEqualityCondition<Item> implements Condition2<CountableSet<Item>, CountableSet<Item>> {

	@Override public boolean is(CountableSet<Item> a, CountableSet<Item> b) {

		for (Item item : a) {
			if (!b.contains(item)) return false;
		}
		for (Item item : b) {
			if (!a.contains(item)) return false;
		}
		return true;

	}

}
