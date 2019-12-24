package ic.struct.list;


import org.jetbrains.annotations.NotNull;
import ic.annotations.Positive;
import ic.annotations.Valid;

import ic.struct.sequence.Sequence;
import ic.struct.sequence.Series;

import ic.throwables.End;


public class JoinList<Item> implements List<Item> {


	private final @NotNull Sequence<List<? extends Item>> parts;


	@Override public @Positive int getLength() {

		int count = 0;

		try { final Series<List<? extends Item>> iterator = parts.getIterator();
			while (true) { final List<? extends Item> part = iterator.get();
				count += part.getCount();
			}
		} catch (End end) { return count; }

	}

	@Override public Item implementGet(@Valid @Positive int index) {

		try { final Series<List<? extends Item>> iterator = parts.getIterator();
			while (true) { final List<? extends Item> part = iterator.get();
				try {
					return part.safeGet(index);
				} catch (IndexOutOfBounds indexOutOfBounds) {
					index -= part.getCount();
				}
			}
		} catch (End end) { throw new Error("List inconsistency"); }

	}


	public JoinList(@NotNull Sequence<List<? extends Item>> parts) {
		this.parts = parts;
	}

	public @SafeVarargs JoinList(@NotNull List<? extends Item>... parts) {
		this(new Sequence.FromArray<>(parts));
	}

	@SuppressWarnings("unchecked") public JoinList(@NotNull List<? extends Item> part1, @NotNull List<? extends Item> part2) 		{ this(new List[] { part1, part2 }); 		}
	@SuppressWarnings("unchecked") public JoinList(@NotNull List<Item> part1, @NotNull List<Item> part2, @NotNull List<Item> part3) { this(new List[] { part1, part2, part3 }); }


}
