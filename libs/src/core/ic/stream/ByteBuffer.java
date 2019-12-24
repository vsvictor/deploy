package ic.stream;


import ic.util.Arrays;
import ic.interfaces.countable.Countable;
import ic.struct.list.IndexOutOfBounds;
import ic.throwables.CalledTwiceTwice;
import ic.throwables.WrongState;


public class ByteBuffer extends ByteSequence.BaseByteSequence implements ByteOutput, Countable {


	private volatile byte[] array = new byte[64];

	private volatile int length = 0;

	private volatile boolean closed = false;


	// ByteSequence implementation:

	@Override public ByteInput getIterator() {
		return new ByteSequence.FromByteArray(toByteArray()).getIterator();
	}

	@Override public byte[] toByteArray() {
		return Arrays.copy(array, length);
	}


	// ByteOutput implementation:

	@Override public synchronized void put(byte b) {
		if (closed) throw new WrongState.Runtime("Closed");
		if (length >= array.length) {
			array = Arrays.copy(array, array.length * 2);
		}
		array[length] = b;
		length++;
	}

	@Override public void put(Byte value) {
		put((byte) value);
	}

	@Override public synchronized void close() {
		if (closed) throw new CalledTwiceTwice.Runtime("Already closed");
		closed = true;
	}


	// Countable<Byte> implementation:

	@Override public int getCount() {
		return length;
	}


	// Get byte by index:

	public byte getByte(int index) throws IndexOutOfBounds.Runtime {
		if (index < 0) 			throw new IndexOutOfBounds.Runtime(length, index);
		if (index >= length) 	throw new IndexOutOfBounds.Runtime(length, index);
		return array[index];
	}


}
