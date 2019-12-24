package d2.modules.talk.api;


import d2.modules.talk.model.Ticket;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.CacheStorage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;


public abstract class NotifyBuyTicketApiMethod extends HttpEndpoint {

	@Override protected String getName() { return "notify_buy_ticket"; }

	protected abstract CacheStorage getStorage();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String userId 		= requestJson.getString("userId");
		final String talkEventId 	= requestJson.getString("talkEventId");

		new Ticket(getStorage(), userId, talkEventId);

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject responseJson = new JSONObject();
			responseJson.put("status", "SUCCESS");
			return responseJson;
		} };

	}

}
