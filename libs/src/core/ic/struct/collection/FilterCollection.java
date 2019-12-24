package ic.struct.collection;


import ic.interfaces.action.Action1;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;


public class FilterCollection<Item> implements Collection<Item> {


	private final Collection<Item> sourceCollection;

	private final Function1<Item, Boolean> filter;


	@Override public void implementForEach(final Action1<Item> action) {
		sourceCollection.forEach(item -> {
			if (!filter.invoke(item)) return;
			action.run(item);
		});
	}


	public FilterCollection(@NotNull Collection<Item> sourceCollection, Function1<Item, Boolean> filter) {
		this.sourceCollection = sourceCollection;
		this.filter = filter;
	}


}
