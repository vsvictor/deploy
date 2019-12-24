package ic.stream;


import ic.interfaces.closeable.Closeable;
import ic.throwables.End;


public class CacheByteInput extends ByteSequence.BaseByteSequence implements Closeable {


	private final ByteInput sourceInput;

	private final ByteBuffer buffer = new ByteBuffer();


	private boolean closed = false;

	@Override public void close() { closed = true; }

	@Override public ByteInput getIterator() { return new ByteInput.BaseByteInput() {
		int index = 0;
		@Override public byte getByte() throws End {
			try {
				synchronized (buffer) {
					if (index < buffer.getCount()) {
						return buffer.getByte(index);
					}
				}
				if (closed) {
					throw End.END;
				}
				final Byte b = sourceInput.get();
				synchronized (buffer) {
					buffer.put(b);
				}
				return b;
			} finally {
				index++;
			}
		}
		@Override public void close() {}
	}; }


	public CacheByteInput(ByteInput sourceInput) {
		this.sourceInput = sourceInput;
	}


}
