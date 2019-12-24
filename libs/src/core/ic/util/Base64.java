package ic.util;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.annotations.Overload;
import ic.stream.ByteSequence;


public @Degenerate class Base64 { @Hide private Base64() {}


	public static @NotNull byte[] base64StringToBytes(@NotNull String base64String) {
		assert base64String != null;
		return java.util.Base64.getDecoder().decode(base64String);
	}

	public static @NotNull String bytesToBase64String(@NotNull byte[] bytes) {
		assert bytes != null;
		return java.util.Base64.getEncoder().encodeToString(bytes);
	}

	@Overload public static @NotNull String bytesToBase64String(@NotNull ByteSequence byteSequence) {
		assert byteSequence != null;
		return bytesToBase64String(byteSequence.toByteArray());
	}


}
