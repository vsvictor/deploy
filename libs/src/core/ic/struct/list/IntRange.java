package ic.struct.list;


public class IntRange implements List<Integer> {


	private final int from;
	private final int to;


	@Override public int getLength() {
		return to - from;
	}


	@Override public Integer implementGet(int index) {
		return from + index;
	}


	public IntRange(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public IntRange(int length) {
		this.from = 0;
		this.to = length;
	}


}
