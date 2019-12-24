package ic.service.monitoring.monitor;


import ic.network.SocketAddress;
import ic.network.http.*;
import ic.struct.list.List;
import ic.struct.set.CountableSet;
import ic.throwables.NotSupported;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static ic.json.JsonArrays.toJsonArray;
import static ic.service.monitoring.monitor.model.MonitoredService.getMonitoredServices;


public abstract class MonitorApiHttpsServer extends HttpsServer {

	@Override protected boolean toAllowCors() { return true; }

	@Override protected CountableSet<String> getDomainNames() throws NotSupported {
		return new CountableSet.Default<>(getDomainName(null));
	}

	@Override protected HttpRoute initRoute() { return new FolderHttpRoute.Final(

		new FolderHttpRoute.Final(
			"api",
			new List.Default<>(

				new HttpEndpoint() {
					@Override protected String getName() { return "list"; }
					@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {
						final JSONObject responseJson = new JSONObject();
						responseJson.put("status", "SUCCESS");
						responseJson.put("services", toJsonArray(getMonitoredServices(), service -> {
							final JSONObject serviceJson = new JSONObject();
							serviceJson.put("name", service.name);
							serviceJson.put("host", service.host);
							return serviceJson;
						}));
						return new JsonResponse() {
							@Override protected Object getJson() { return responseJson; }
						};
					}
				}

			)
		)

	); }

}
