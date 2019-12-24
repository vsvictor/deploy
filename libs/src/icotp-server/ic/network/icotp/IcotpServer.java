package ic.network.icotp;


import ic.interfaces.getter.SafeGetter2;
import ic.network.Socket;
import ic.network.SocketServer;
import ic.network.SocketAddress;
import ic.throwables.*;

import static ic.serial.stream.ParseKt.parseFromStream;
import static ic.serial.stream.SerializeKt.serializeToStream;
import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;


public abstract class IcotpServer extends SocketServer {

	protected abstract SafeGetter2<Object, SocketAddress, Object, ? extends Throwable> initRequestHandler();

	private final SafeGetter2<Object, SocketAddress, Object, ? extends Throwable> requestHandler = initRequestHandler();

	@Override protected void handleConnection(Socket connection) {

		try {

			final IcotpMode mode; try {
				mode = IcotpMode.byByteValue(connection.input.getByte());
			} catch (End end) {
				return;
			}

			if (mode == IcotpMode.PLAIN) {

				final Object request; try {
					request = parseFromStream(connection.input);
				} catch (UnableToParse unableToParse) {
					connection.output.put(IcotpStatus.ERROR.byteValue);
					serializeToStream(connection.output, unableToParse);
					throwAsUnchecked(unableToParse); return;
				}

				final Object response; try {
					if (request instanceof Ping) {
						response = new Pong();
					}
					else {
						response = requestHandler.get(connection.address, request);
					}
				} catch (Throwable throwable) {
					connection.output.put(IcotpStatus.ERROR.byteValue);
					try {
						serializeToStream(connection.output, throwable);
					} catch (WrongType.Runtime runtime) {}
					throwAsUnchecked(throwable); return;
				}

				connection.output.put(IcotpStatus.SUCCESS.byteValue);
				serializeToStream(connection.output, response);

			} else

			if (mode == IcotpMode.HANDSHAKE) {
				throw new NotImplementedYet();
			}

			else throw new InvalidValue.Runtime("mode: " + mode + ", host: " + connection.address.host);

		} finally {

			connection.close();

		}

	}

}
