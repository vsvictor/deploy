package ic.network;


import org.jetbrains.annotations.NotNull;
import ic.stream.ByteInput;
import ic.stream.CacheByteInput;
import ic.stream.ByteSequence;
import ic.text.Charset;
import ic.throwables.IOException;

import static ic.log.InstantCoffeeLogger.LOGGER;
import static ic.parallel.SleepKt.sleep;


public abstract class SocketRRServer extends SocketServer {

	protected abstract ByteSequence handleThrowable(ByteSequence request, Throwable throwable);

	protected abstract @NotNull ByteSequence getResponse(SocketAddress socketAddress, @NotNull ByteInput request) throws IncompleteRequest;

	protected boolean toLogTraffic() { return false; }

	@Override protected void handleConnection(Socket connection) throws IOException.Runtime {

		try {

			for (int i = 0; i < 64; i++) {

				sleep(64);

				final CacheByteInput requestCache = new CacheByteInput(connection.input);

				final ByteSequence response; {
					ByteSequence value;
					try {
						value = getResponse(connection.address, requestCache.getIterator());
						requestCache.close();
						if (toLogTraffic()) synchronized (LOGGER) {
							LOGGER.writeLine("SOCKET_RR_SERVER REQUEST " + Charset.DEFAULT_HTTP.bytesToString(requestCache));
							LOGGER.writeLine("SOCKET_RR_SERVER RESPONSE " + Charset.DEFAULT_HTTP.bytesToString(value));
						}
					} catch (IOException.Runtime | IncompleteRequest ioException) { continue;
					} catch (Throwable throwable) {
						requestCache.close();
						synchronized (LOGGER) {
							LOGGER.writeLine("SOCKET_RR_SERVER REQUEST " + Charset.DEFAULT_HTTP.bytesToString(requestCache));
							LOGGER.writeStackTrace(throwable);
						}
						value = handleThrowable(requestCache, throwable);
					}
					response = value;
				}

				connection.output.write(response);

				break;

			}

		} finally {

			connection.close();

		}

	}

}
