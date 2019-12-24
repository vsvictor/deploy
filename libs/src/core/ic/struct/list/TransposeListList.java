package ic.struct.list;


import org.jetbrains.annotations.NotNull;
import ic.struct.collection.ConvertCollection;

import static ic.struct.collection.Collections.maxInt;


public class TransposeListList<Item> implements List<List<Item>> {

	private final List<List<Item>> sourceListList;

	@Override public List<Item> implementGet(final int rowIndex) {
		return new List<>() {
			@Override public int getLength() {
				return sourceListList.getLength();
			}
			@Override public Item implementGet(int columnIndex) {
				try {
					return sourceListList.safeGet(columnIndex).safeGet(rowIndex);
				} catch (IndexOutOfBounds indexOutOfBounds) {
					return null;
				}
			}
		};
	}

	@Override public int getLength() {
		return maxInt(new ConvertCollection<>(
			sourceListList,
			List::getLength
		));
	}

	public TransposeListList(@NotNull List<List<Item>> sourceListList) {
		this.sourceListList = sourceListList;
	}

}
