package d2.modules.talk.api;


import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.CacheStorage;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.talk.model.Ticket.getUsersWithTickets;


public abstract class GetTicketsStatisticsApiMethod extends HttpEndpoint {

	@Override protected String getName() { return "get_tickets_statistics"; }

	protected abstract CacheStorage getStorage();

	protected abstract int getUsersCount();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final CacheStorage storage = getStorage();

		final int usersCount = getUsersCount();
		final int usersWithTicketsCount = getUsersWithTickets(storage).getCount();

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject responseJson = new JSONObject();
			responseJson.put("status", "SUCCESS");
			responseJson.put("usersCount", 				usersCount);
			responseJson.put("usersWithTicketsCount", 	usersWithTicketsCount);
			return responseJson;
		} };

	}

}
