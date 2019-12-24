package d2.modules.talk.admin;


import d2.modules.talk.model.Talk;
import d2.modules.talk.model.TalkEvent;
import ic.id.Mapper;
import ic.interfaces.getter.Safe2Getter1;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.text.Charset;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.json.JSONException;
import org.json.JSONObject;

import static d2.modules.talk.model.Talk.getTalksMapper;
import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;
import static ic.json.JsonArrays.toJsonArray;


public abstract class GetTalksApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "get_talks"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final Storage storage = getStorage();

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String talkEventId = requestJson.getString("talkEventId");

		final JSONObject responseJson = new JSONObject();

		final TalkEvent talkEvent = getTalkEventsMapper(storage).getItem(talkEventId);

		final Mapper<Talk, String> talksMapper = getTalksMapper(storage);

		responseJson.put("talks", toJsonArray(
			talkEvent.getTalkIds(),
			(Safe2Getter1<Object, String, JSONException, Skip>) talkId -> {
				final Talk talk = talksMapper.getItem(talkId);
				final JSONObject talkJson = new JSONObject();
				talkJson.put("id", talkId);
				talkJson.put("name", talk.getName());
				return talkJson;
			}
		));

		responseJson.put("status", "SUCCESS");

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
