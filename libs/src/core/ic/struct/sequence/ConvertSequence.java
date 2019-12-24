package ic.struct.sequence;


import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import ic.throwables.Skip;


public class ConvertSequence<Item, SourceItem> implements Sequence<Item> {


	private final Sequence<SourceItem> sourceSequence;


	private final Function1<SourceItem, Item> itemConverter;


	@Override public Series<Item> getIterator() {

		final Series<SourceItem> sourceIterator = sourceSequence.getIterator();

		return () -> {

			while (true) {
				try {
					return itemConverter.invoke(sourceIterator.get());
				} catch (Skip skip) {}
			}

		};

	}


	public ConvertSequence(@NotNull Sequence<SourceItem> sourceSequence, @NotNull Function1<SourceItem, Item> itemConverter) {
		this.sourceSequence = sourceSequence;
		this.itemConverter = itemConverter;
	}


}
