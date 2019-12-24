package ic.interfaces.condition;


import org.jetbrains.annotations.NotNull;


public class EqualityCondition1<Item> implements Condition1<Item> {

	private final Item item;
	private final EqualityCondition2<Item> equalityCondition2;

	@Override public boolean is(Item item) {
		return equalityCondition2.is(item, this.item);
	}

	public EqualityCondition1(@NotNull Item item, @NotNull EqualityCondition2<Item> equalityCondition2) {
		this.item = item;
		this.equalityCondition2 = equalityCondition2;
	}

	public EqualityCondition1(Item item) {
		this(
			item,
			new EqualityCondition2.Default<>()
		);
	}

}
