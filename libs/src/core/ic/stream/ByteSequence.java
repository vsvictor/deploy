package ic.stream;


import ic.annotations.*;
import ic.struct.sequence.Sequence;
import ic.throwables.End;
import kotlin.jvm.functions.Function0;

import java.io.ByteArrayOutputStream;


public interface ByteSequence extends Sequence<Byte> {


	@Narrowing @Override ByteInput getIterator();

	byte[] toByteArray();


	abstract class BaseByteSequence implements ByteSequence {

		@Override public byte[] toByteArray() {
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try { final ByteInput iterator = getIterator(); while (true) {
				byteArrayOutputStream.write(iterator.getByte());
			} } catch (End end) {}
			return byteArrayOutputStream.toByteArray();
		}

	}


	class FromByteArray extends BaseByteSequence {

		private byte[] byteArray;

		@Override public byte[] toByteArray() { return byteArray; }

		@Override public ByteInput getIterator() {
			return new ByteInput.BaseByteInput() {
				private int index = 0;
				@Override public synchronized byte getByte() throws End {
					if (index >= byteArray.length) throw new End();
					return byteArray[index++];
				}
				@Override public synchronized void skip(int amount) throws End {
					assert amount >= 0;
					index += amount;
					if (index > byteArray.length) throw new End();
				}
				@Override public void close() {
					byteArray = null;
				}
			};
		}

		public FromByteArray(byte[] byteArray) {
			this.byteArray = byteArray;
		}

	}


	class Final extends BaseByteSequence {
		final Function0<ByteInput> iteratorGetter;
		@Override public ByteInput getIterator() {
			return iteratorGetter.invoke();
		}
		public Final(Function0<ByteInput> iteratorGetter) {
			this.iteratorGetter = iteratorGetter;
		}
	}


	ByteSequence EMPTY = new FromByteArray(new byte[] {});


}
