package ic.struct.list;


import org.jetbrains.annotations.NotNull;


public class ReverseList<Item> implements List<Item> {

	private final List<Item> sourceList;

	@Override public Item implementGet(int index) {
		synchronized (sourceList) {
			return sourceList.get(sourceList.getCount() - 1 - index);
		}
	}

	@Override public int getLength() {
		return sourceList.getLength();
	}

	public ReverseList(@NotNull List<Item> sourceList) {
		this.sourceList = sourceList;
	}

}
