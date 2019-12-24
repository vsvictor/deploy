package ic.stream;


import ic.annotations.AlwaysThrows;
import ic.annotations.DoesNothing;
import ic.interfaces.action.SafeAction;
import ic.interfaces.closeable.Closeable;
import ic.struct.sequence.Series;
import ic.throwables.End;
import ic.throwables.IOException;
import ic.throwables.NotExists;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import static ic.util.Unsigned.byteToInt;
import static ic.util.Unsigned.intToByte;
import static ic.throwables.End.END;


public interface ByteInput extends Series<Byte>, Closeable {


	byte getByte() throws End;


	ByteSequence read();

	ByteSequence read(int count) throws End;


	ByteSequence readTill(byte... bytes);

	ByteSequence safeReadTill(byte... bytes) throws NotExists;

	ByteSequence readTill(byte b);


	InputStream toInputStream();


	abstract class BaseByteInput implements ByteInput {

		@Override public Byte get() throws End {
			return getByte();
		}

		@Override public ByteSequence read() {
			ByteBuffer byteBuffer = new ByteBuffer();
			try { while (true) {
				byteBuffer.put(getByte());
			} } catch (End end) {}
			return byteBuffer;
		}

		@Override public ByteSequence read(int count) throws End {
			ByteBuffer byteBuffer = new ByteBuffer();
			for (int i = 0; i < count; i++) {
				byteBuffer.put(getByte());
			}
			return byteBuffer;
		}

		private <NotFoundThrowable extends Throwable> ByteSequence implementReadTill(byte[] bytes, SafeAction<NotFoundThrowable> notFoundAction) throws NotFoundThrowable {
			final ByteBuffer buffer = new ByteBuffer();
			int arrIndex = 0;
			try {
				while (true) {
					byte b = getByte();
					if (b == bytes[arrIndex]) {
						arrIndex++;
						if (arrIndex >= bytes.length) {
							break;
						}
					} else {
						for (int i = 0; i < arrIndex; i++) {
							buffer.put(bytes[i]);
						}
						arrIndex = 0;
						buffer.put(b);
					}
				}
			} catch (End end) {
				notFoundAction.run();
			}
			return buffer;
		}

		private static final SafeAction<RuntimeException> UNSAFE_NOT_FOUND_ACTION = new SafeAction<RuntimeException>() {
			@DoesNothing @Override public void run() {}
		};

		@Override public ByteSequence readTill(byte[] bytes) {
			return implementReadTill(bytes, UNSAFE_NOT_FOUND_ACTION);
		}

		@Override public ByteSequence readTill(byte b) {
			return readTill(new byte[] { b });
		}

		private static final SafeAction<RuntimeException> SAFE_NOT_FOUND_ACTION = new SafeAction<RuntimeException>() {
			@AlwaysThrows @Override public void run() {}
		};

		@Override public ByteSequence safeReadTill(byte[] bytes) {
			return implementReadTill(bytes, SAFE_NOT_FOUND_ACTION);
		}

		@Override public InputStream toInputStream() {
			return new InputStream() {
				@Override public int read() {
					try {
						return byteToInt(getByte());
					} catch (End end) {
						return -1;
					}
				}
				@Override public void close() {
					BaseByteInput.this.close();
				}
			};
		}

	}


	class FromInputStream extends BaseByteInput {

		private final InputStream inputStream;

		@Override public void skip() {
			try {
				inputStream.close();
			} catch (java.io.IOException e) {}
		}

		@Override public byte getByte() throws End {
			try {
				final int i = inputStream.read();
				if (i == -1) {
					skip();
					throw END;
				}
				return intToByte(i);
			} catch (java.io.IOException e) { throw new IOException.Runtime(e); }
		}

		public FromInputStream(@NotNull InputStream inputStream) {
			this.inputStream = inputStream;
		}

		@Override public void close() {
			try {
				inputStream.close();
			} catch (java.io.IOException e) { throw new IOException.Runtime(e); }
		}

	}


}
