package ic.struct.set;


import ic.interfaces.action.Action1;
import ic.interfaces.action.SafeAction1;
import ic.struct.collection.Collection;
import ic.throwables.Break;

import static ic.util.Arrays.iterableToArray;


public class UnionCountableSet<Item> implements CountableSet<Item> {


	private final Collection<CountableSet<Item>> children;


	@Override public void implementForEach(Action1<Item> action) throws Break {
		final CountableSet<Item>[] children = iterableToArray(this.children, CountableSet.class);
		for (int i = 0; i < children.length; i++) {
			final CountableSet<Item> child = children[i];
			assert child != null;
			for (Item item : child) {
				checkingPreviousChildren: {
					for (int j = 0; j < i; j++) {
						if (children[j].contains(item)) break checkingPreviousChildren;
					} action.run(item);
				}
			}
		}
	}

	@Override public boolean contains(Item item) {
		for (CountableSet<Item> child : children) {
			if (child.contains(item)) return true;
		} return false;
	}


	// Constructors:

	public UnionCountableSet(Collection<CountableSet<Item>> children) {
		this.children = children;
	}

	public @SafeVarargs UnionCountableSet(CountableSet<Item>... children) {
		this(new Collection.Default<>(children));
	}

	@SuppressWarnings("unchecked") public UnionCountableSet(CountableSet<Item> child1, CountableSet<Item> child2) 								{ this(new CountableSet[] { child1, child2 }); 			}
	@SuppressWarnings("unchecked") public UnionCountableSet(CountableSet<Item> child1, CountableSet<Item> child2, CountableSet<Item> child3) 	{ this(new CountableSet[] { child1, child2, child3 }); 	}


}
