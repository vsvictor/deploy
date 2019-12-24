package ic.util;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.throwables.UnableToParse;

import java.math.BigInteger;


public class Hex {


	public static String byteToFixedSizeHexString(byte b) {
		return String.format("%02x", b);
	}

	public static String bytesToHexString(byte[] bytes) {
		final StringBuffer stringBuffer = new StringBuffer();
		for (byte b : bytes) stringBuffer.append(byteToFixedSizeHexString(b));
		return stringBuffer.toString();
	}

	public static byte hexStringToByte(String string) throws UnableToParse {
		try {
			return (byte) Integer.parseInt(string, 16);
		} catch (NumberFormatException numberFormatException) {
			throw new UnableToParse(numberFormatException);
		}
	}


	public static String intToFixedSizeHexString(int i) {
		return String.format("%08x", i);
	}

	public static String longToFixedSizeHexString(long l) {
		final String string = String.format("%016x", l);
		return string;
	}

	public static String nullableLongToFixedSizeHexString(@Nullable Long l) {
		if (l == null) return null;
		else return longToFixedSizeHexString(l);
	}

	public static long safeHexStringToLong(@NotNull String string) throws UnableToParse {
		assert string != null;
		try {
			return new BigInteger(string, 16).longValue();
		} catch (NumberFormatException numberFormatException) { throw new UnableToParse(numberFormatException); }
	}

	public static long hexStringToLong(@NotNull String string) throws UnableToParse.Runtime {
		try {
			return safeHexStringToLong(string);
		} catch (UnableToParse unableToParse) { throw new UnableToParse.Runtime(unableToParse); }
	}

	public static @Nullable Long nullableHexStringToLong(@NotNull String string) throws UnableToParse.Runtime {
		if (string == null) return null;
		else return hexStringToLong(string);
	}


}
