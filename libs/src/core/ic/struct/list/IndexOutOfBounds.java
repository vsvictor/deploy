package ic.struct.list;


@SuppressWarnings("serial")
public class IndexOutOfBounds extends Exception {

	public final int length;
	public final int index;
		
	public IndexOutOfBounds(int length, int index) {
		
		super("Can't access item " + index + " of List that has length " + length);
		
		this.length = length;
		this.index = index;
			
	}
	
	
	// Runtime:
	
	public static class Runtime extends RuntimeException {
	
		public final int length;
		public final int index;
		
		public Runtime(int length, int index) {
		
			super("Can't access item " + index + " of List that has length " + length);
		
			this.length = length;
			this.index = index;
			
		}

		public Runtime(IndexOutOfBounds indexOutOfBounds) {
			this(indexOutOfBounds.length, indexOutOfBounds.index);
		}
		
	}
	
}
