package ic.struct.set;


import ic.interfaces.action.Action1;
import org.jetbrains.annotations.NotNull;


public abstract class ConvertCountableSet<Item, SourceItem> implements CountableSet<Item> {


	private final CountableSet<SourceItem> sourceCountableSet;

	protected abstract Item convert(SourceItem sourceItem);

	protected abstract SourceItem convertBack(Item item);


	@Override public void implementForEach(final Action1<Item> action) {
		sourceCountableSet.forEach(sourceItem -> action.run(convert(sourceItem)));
	}

	@Override public boolean contains(Item item) {
		return sourceCountableSet.contains(convertBack(item));
	}

	@Override public synchronized int getCount() {
		return sourceCountableSet.getCount();
	}


	public ConvertCountableSet(@NotNull CountableSet<SourceItem> sourceCountableSet) {
		assert sourceCountableSet != null;
		this.sourceCountableSet = sourceCountableSet;
	}


}
