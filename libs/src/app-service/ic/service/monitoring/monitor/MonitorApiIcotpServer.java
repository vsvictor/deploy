package ic.service.monitoring.monitor;


import ic.interfaces.getter.SafeGetter2;
import ic.network.SocketAddress;
import ic.network.icotp.IcotpServer;
import ic.service.monitoring.Monitoring;
import ic.service.monitoring.api.ImIn;
import ic.service.monitoring.api.ImOut;
import ic.service.monitoring.api.ProtectedRequest;
import ic.service.monitoring.monitor.model.MonitoredService;
import ic.throwables.WrongType;

import static ic.service.monitoring.monitor.model.MonitoredService.removeMonitoredService;
import static ic.throwables.AccessDenied.ACCESS_DENIED;


abstract class MonitorApiIcotpServer extends IcotpServer {


	@Override protected int getPort() { return Monitoring.PORT_MONITOR; }


	protected abstract String getMonitorKey();

	protected abstract void onServiceIn(MonitoredService monitoredService);
	protected abstract void onServiceOut(String serviceName);


	@Override protected SafeGetter2<Object, SocketAddress, Object, Throwable> initRequestHandler() { return (socketAddress, request) -> {

		if (request instanceof ProtectedRequest) { final ProtectedRequest protectedRequest = (ProtectedRequest) request;

			if (!protectedRequest.monitorKey.equals(getMonitorKey())) throw ACCESS_DENIED;

			if (request instanceof ImIn) { final  ImIn imIn = (ImIn) request;

				final MonitoredService monitoredService = new MonitoredService(imIn.name, socketAddress.host, imIn.sshAuth, imIn.appPackageName);

				onServiceIn(monitoredService);

				return null;

			} else

			if (request instanceof ImOut) { final ImOut imOut = (ImOut) request;

				removeMonitoredService(imOut.name);

				onServiceOut(imOut.name);

				return null;

			}

			else throw new WrongType.Error(request);

		}

		else throw new WrongType.Error(request);

	}; }


}
