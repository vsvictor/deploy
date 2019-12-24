package d2.modules.talk.admin;


import d2.modules.talk.model.TalkEvent;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;


public abstract class AddTalkEventApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "add_talk_event"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String title = requestJson.getString("title");

		new TalkEvent(getStorage(), title);

		return new JsonResponse() {
			@Override protected JSONObject getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUCCESS");
				return responseJson;
			}
		};

	}

}
