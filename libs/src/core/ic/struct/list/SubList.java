package ic.struct.list;


import org.jetbrains.annotations.NotNull;


public class SubList<Item> implements List<Item> {


	private final List<Item> sourceList;

	private final int start;
	private final int end;


	@Override public Item implementGet(int index) {
		return sourceList.get(start + index);
	}

	@Override public int getLength() {
		return end - start;
	}


	public SubList(@NotNull List<Item> sourceList, int start, int end) {
		this.sourceList = sourceList;
		this.start = start;
		this.end = end;
	}

	public SubList(@NotNull List<Item> sourceList, int start) {
		this.sourceList = sourceList;
		this.start = start;
		this.end = sourceList.getCount();
	}


}
