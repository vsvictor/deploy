package ic.util;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.annotations.Overload;
import ic.stream.ByteInput;
import ic.stream.ByteSequence;


public @Degenerate class Bytes { @Hide private Bytes() {}

	public static byte[] intToByteArray (int i) {
		return new byte[] {
			(byte) (i >>> 24),
			(byte) (i >>> 16),
			(byte) (i >>> 8),
			(byte) (i)
		};
	}

	@Overload public static ByteSequence intToByteSequence(int i) {
		return new ByteSequence.FromByteArray(intToByteArray(i));
	}

	public static int bytesToInt(byte... bytes) {
		assert bytes.length == 4;
		return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}

	public static byte[] longToByteArray(long l) {
		return new byte[] {
			(byte) (l >>> 56),
			(byte) (l >>> 48),
			(byte) (l >>> 40),
			(byte) (l >>> 32),
			(byte) (l >>> 24),
			(byte) (l >>> 16),
			(byte) (l >>> 8),
			(byte) (l)
		};
	}

	public static long bytesToLong(byte... bytes) {
		assert bytes.length == 8;
		return (
			((long) bytes[0] << 56) |
			((long) bytes[1] & 0xff) << 48 |
			((long) bytes[2] & 0xff) << 40 |
			((long) bytes[3] & 0xff) << 32 |
			((long) bytes[4] & 0xff) << 24 |
			((long) bytes[5] & 0xff) << 16 |
			((long) bytes[6] & 0xff) << 8 |
			((long) bytes[7] & 0xff)
		);
	}

}
