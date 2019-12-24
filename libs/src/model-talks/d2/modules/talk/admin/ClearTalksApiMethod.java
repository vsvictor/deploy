package d2.modules.talk.admin;


import d2.modules.talk.model.TalkEvent;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.throwables.UnableToParse;
import org.json.JSONObject;
import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;


public abstract class ClearTalksApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "clear_talks"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final String talkEventId = request.urlParams.getNotNull("talk_event_id");

		final Storage storage = getStorage();

		final TalkEvent talkEvent = getTalkEventsMapper(storage).getItem(talkEventId);

		talkEvent.clearTalks();

		return new JsonResponse() {
			@Override protected JSONObject getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUCCESS");
				return responseJson;
			}
		};

	}



}
