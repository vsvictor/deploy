package ic.struct.set;


import ic.interfaces.action.Action1;
import org.jetbrains.annotations.NotNull;


public abstract class FilterCountableSet<Item> implements CountableSet<Item> {


	private final @NotNull CountableSet<Item> sourceCountableSet;


	protected abstract boolean filter(Item item);


	@Override public void implementForEach(final Action1<Item> action) {
		sourceCountableSet.forEach(item -> {
			if (!filter(item)) return;
			action.run(item);
		});
	}


	@Override public boolean contains(Item item) {
		if (!filter(item)) return false;
		return sourceCountableSet.contains(item);
	}


	public FilterCountableSet(@NotNull CountableSet<Item> sourceCountableSet) {
		assert sourceCountableSet != null;
		this.sourceCountableSet = sourceCountableSet;
	}


}
