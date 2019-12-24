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

import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;


public abstract class SetTalkEventDescriptionApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "set_talk_event_description"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String id 			= requestJson.getString("id");
		final String description 	= requestJson.getString("description");

		final TalkEvent talkEvent = getTalkEventsMapper(getStorage()).getItem(id);

		talkEvent.setDescription(description);

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
