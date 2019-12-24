package ic.network.icotp;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.network.Socket;
import ic.network.SocketAddress;
import ic.throwables.InvalidValue;
import org.jetbrains.annotations.Nullable;

import static ic.serial.stream.ParseKt.parseFromStream;
import static ic.serial.stream.SerializeKt.serializeToStream;


public @Degenerate class IcotpClient { @Hide private IcotpClient() {}


	public static @Nullable <Type> Type request(SocketAddress socketAddress, IcotpMode mode, Object request) throws Throwable {

		final Socket connection = new Socket(socketAddress);

		try {

			connection.output.put(mode.byteValue);

			if (mode == IcotpMode.PLAIN) {

				serializeToStream(connection.output, request);

				final IcotpStatus status = IcotpStatus.byByteValue(connection.input.getByte());

				if (status == IcotpStatus.SUCCESS) {

					return parseFromStream(connection.input);

				} else

				if (status == IcotpStatus.ERROR) {

					throw (Throwable) parseFromStream(connection.input);

				}

				else throw new InvalidValue.Runtime("status: " + status);

			}

			else throw new InvalidValue.Runtime("mode: " + mode);

		} finally {

			connection.close();

		}

	}


}
