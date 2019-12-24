package ic.hash;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.annotations.Overload;
import ic.stream.ByteSequence;
import org.jetbrains.annotations.NotNull;

import static ic.util.Bytes.bytesToLong;
import static org.apache.commons.codec.digest.DigestUtils.sha256;


public @Degenerate class Sha { @Hide private Sha() {}


	public static long sha64(@NotNull byte[] bytes) {
		final byte[] sha256 = sha256(bytes);
		return bytesToLong(
			sha256[0],
			sha256[1],
			sha256[2],
			sha256[3],
			sha256[4],
			sha256[5],
			sha256[6],
			sha256[7]
		);
	}

	@Overload public static long sha64(@NotNull ByteSequence bytes) {
		return sha64(bytes.toByteArray());
	}


}
