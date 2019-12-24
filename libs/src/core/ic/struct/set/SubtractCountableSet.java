package ic.struct.set;


import ic.interfaces.action.Action1;


public class SubtractCountableSet<Item> implements CountableSet<Item> {


	private final CountableSet<Item> a;

	private final CountableSet<Item> b;


	@Override public void implementForEach(final Action1<Item> action)  {
		a.forEach(item -> {
			if (b.contains(item)) return;
			action.run(item);
		});
	}

	@Override public boolean contains(Item item) {
		return (a.contains(item) && !b.contains(item));
	}


	public SubtractCountableSet(CountableSet<Item> a, CountableSet<Item> b) {
		this.a = a;
		this.b = b;
	}


}
