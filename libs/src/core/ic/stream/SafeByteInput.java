package ic.stream;


import ic.interfaces.action.SafeAction;
import ic.annotations.AlwaysThrows;
import ic.annotations.DoesNothing;
import org.jetbrains.annotations.NotNull;
import ic.annotations.ToOverride;
import ic.struct.sequence.SafeSeries;
import ic.throwables.NotExists;
import ic.throwables.End;

import java.io.InputStream;

import static ic.util.Unsigned.byteToInt;
import static ic.util.Unsigned.intToByte;
import static ic.throwables.End.END;
import static ic.throwables.NotExists.NOT_EXISTS;


public interface SafeByteInput<Thrown extends Throwable> extends SafeSeries<Byte, Thrown> {


	byte getByte() throws End, Thrown;


	ByteSequence read() throws Thrown;

	ByteSequence read(int count) throws Thrown, End;


	ByteSequence readTill(byte... bytes) throws Thrown;

	ByteSequence safeReadTill(byte... bytes) throws Thrown, NotExists;


	InputStream toInputStream();


	abstract class BaseSafeByteInput<Thrown extends Throwable> implements SafeSeries<Byte, Thrown>, SafeByteInput<Thrown> {

		@Override public Byte get() throws End, Thrown {
			return getByte();
		}

		@Override public ByteSequence read() throws Thrown {
			ByteBuffer byteBuffer = new ByteBuffer();
			try { while (true) {
				byteBuffer.put(getByte());
			} } catch (End end) {}
			return byteBuffer;
		}

		@Override public ByteSequence read(int count) throws Thrown, End {
			ByteBuffer byteBuffer = new ByteBuffer();
			for (int i = 0; i < count; i++) {
				byteBuffer.put(getByte());
			}
			return byteBuffer;
		}

		private <NotFoundThrowable extends Throwable> ByteSequence implementReadTill(byte[] bytes, SafeAction<NotFoundThrowable> notFoundAction) throws Thrown, NotFoundThrowable {
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

		@Override public ByteSequence readTill(byte[] bytes) throws Thrown {
			return implementReadTill(bytes, UNSAFE_NOT_FOUND_ACTION);
		}

		private static final SafeAction<NotExists> SAFE_NOT_FOUND_ACTION = new SafeAction<NotExists>() {
			@AlwaysThrows @Override public void run() throws NotExists { throw NOT_EXISTS; }
		};

		@Override public ByteSequence safeReadTill(byte[] bytes) throws NotExists, Thrown {
			return implementReadTill(bytes, SAFE_NOT_FOUND_ACTION);
		}

		@ToOverride protected java.io.IOException convertException(Thrown throwable) {
			return new java.io.IOException(throwable);
		}

		@Override public InputStream toInputStream() {
			return new InputStream() {
				@Override public int read() throws java.io.IOException {
					try {
						return byteToInt(getByte());
					} catch (End end) {
						return -1;
					} catch (Throwable throwable) {

						@SuppressWarnings("unchecked")
						final Thrown castedThrowable = (Thrown) throwable;

						throw convertException(castedThrowable);

					}
				}
			};
		}

	}


	abstract class FromInputStream<Thrown extends Throwable> extends BaseSafeByteInput<Thrown> {

		protected abstract Thrown convertException(java.io.IOException javaIOException);

		private final InputStream inputStream;

		@Override public void skip() {
			try {
				inputStream.close();
			} catch (java.io.IOException e) {}
		}

		@Override public byte getByte() throws Thrown, End {
			try {
				final int i = inputStream.read();
				if (i == -1) {
					skip();
					throw END;
				}
				return intToByte(i);
			} catch (java.io.IOException e) { throw convertException(e); }
		}

		public FromInputStream(@NotNull InputStream inputStream) {
			assert inputStream != null;
			this.inputStream = inputStream;
		}

	}


}
