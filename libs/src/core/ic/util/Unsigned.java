package ic.util;


public class Unsigned {

	public static int byteToInt(byte b) {
		return b & 0x000000FF;
	}

	public static byte intToByte(int i) {
		return (byte) i;
	}

	public static int unsignedMod(int a, int b) {
		int mod = a % b;
		if (mod < 0) mod += b;
		return mod;
	}

}
