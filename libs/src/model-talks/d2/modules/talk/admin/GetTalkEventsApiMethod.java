package d2.modules.talk.admin;


import d2.modules.talk.model.TalkEvent;
import ic.id.Mapper;
import ic.interfaces.getter.Safe2Getter1;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.json.JSONException;
import org.json.JSONObject;

import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;
import static ic.json.JsonArrays.toJsonArray;


public abstract class GetTalkEventsApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "get_talk_events"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject responseJson = new JSONObject();

		final Mapper<TalkEvent, String> talkEventsMapper = getTalkEventsMapper(getStorage());

		responseJson.put("talkEvents", toJsonArray(
			talkEventsMapper.getIds(),
			(Safe2Getter1<Object, String, JSONException, Skip>) talkEventId -> {
				final TalkEvent talkEvent = talkEventsMapper.getItem(talkEventId);
				final JSONObject talkEventJson = new JSONObject();
				talkEventJson.put("id", talkEventId);
				talkEventJson.put("title", talkEvent.getTitle());
				return talkEventJson;
			}
		));

		responseJson.put("status", "SUCCESS");

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
