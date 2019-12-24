package ic.struct.list;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.interfaces.action.Action2;
import org.jetbrains.annotations.NotNull;
import ic.interfaces.getter.Getter1;
import ic.throwables.Empty;


public @Degenerate class Lists { @Hide private Lists() {}


	public static  <Item> Index<Item> findMin(@NotNull List<Item> list, Getter1<Double, Item> ranker) {
		if (list.isEmpty()) throw new Empty.Runtime();
		class Searcher implements Action2<Item, Integer> {
			double rank = Double.MAX_VALUE;
			int index;
			Item item;
			@Override public void run(Item item, Integer index) {
				final double rank = ranker.getNotNull(item);
				if (rank < this.rank) {
					this.rank = rank;
					this.index = index;
					this.item = item;
				}
			}
		}
		final Searcher searcher = new Searcher();
		list.forEach(searcher);
		return new Index<>(searcher.index, searcher.item);
	}


}
