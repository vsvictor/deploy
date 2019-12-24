package ic.interfaces.condition;


import org.jetbrains.annotations.NotNull;
import ic.annotations.Repeat;


public interface EqualityCondition2<Item> extends Condition2<Item, Item> {


	@Repeat @Override boolean is(Item item1, Item item2);


	final class Default<Item> implements EqualityCondition2<Item> {

		@Override public boolean is(@NotNull Item item1, @NotNull Item item2) {

			return item1.equals(item2) || item2.equals(item1);

		}

	}

	final class Identity<Item> implements EqualityCondition2<Item> {

		@Override public boolean is(@NotNull Item item1, @NotNull Item item2) {

			{ // Checking arguments:
				assert item1 != null;
				assert item2 != null;
			}

			return item1 == item2;

		}

	}


}
