package ic.service.monitoring.client;


import ic.interfaces.getter.SafeGetter2;
import ic.network.SocketAddress;
import ic.network.icotp.IcotpServer;
import ic.service.monitoring.Monitoring;
import ic.service.monitoring.api.GetBackup;
import ic.service.monitoring.api.GetStatus;
import ic.stream.ByteSequence;
import ic.throwables.Fatal;
import ic.throwables.WrongType;

import static ic.log.InstantCoffeeLogger.LOGGER;


abstract class MonitorClientApiServer extends IcotpServer {


	@Override protected int getPort() { return Monitoring.PORT_MONITOR_CLIENT; }


	protected abstract Object getStatus() throws Fatal;

	protected abstract ByteSequence getBackup() throws Fatal;


	@Override protected SafeGetter2<Object, SocketAddress, Object, Throwable> initRequestHandler() { return (socketAddress, request) -> {

		if (request instanceof GetStatus) { final GetStatus getStatus = (GetStatus) request;

			try {
				return getStatus();
			} catch (Throwable throwable) {
				LOGGER.writeStackTrace(throwable);
				throw throwable;
			}

		} else

		if (request instanceof GetBackup) { final GetBackup getBackup = (GetBackup) request;

			try {
				return getBackup();
			} catch (Throwable throwable) {
				LOGGER.writeStackTrace(throwable);
				throw throwable;
			}

		}

		else throw new WrongType.Runtime(request);

	}; }


}
