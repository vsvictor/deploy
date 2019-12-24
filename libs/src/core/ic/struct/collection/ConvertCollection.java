package ic.struct.collection;


import ic.interfaces.action.Action1;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import ic.throwables.Skip;


public class ConvertCollection<Item, SourceItem> implements Collection<Item> {


	private final @NotNull Collection<SourceItem> sourceCollection;

	private final @NotNull Function1<SourceItem, Item> itemConverter;


	@Override public void implementForEach(final Action1<Item> action) {
		sourceCollection.forEach( sourceItem -> {
			final Item item; {
				try {
					item = itemConverter.invoke(sourceItem);
				} catch (Skip skip) {
					return;
				}
			}
			action.run(item);
		});
	}


	public ConvertCollection(@NotNull Collection<SourceItem> sourceCollection, @NotNull Function1<SourceItem, Item> itemConverter) {
		this.sourceCollection = sourceCollection;
		this.itemConverter = itemConverter;
	}


}
