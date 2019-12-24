package d2.modules.talk.api;


import d2.modules.talk.model.Talk;
import d2.modules.talk.model.TalkEvent;
import ic.id.Mapper;
import ic.network.SocketAddress;
import ic.network.http.HttpEndpoint;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.struct.map.CountableMap;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Date;

import static d2.modules.talk.model.Talk.getTalksMapper;
import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;
import static ic.json.JsonArrays.toJsonArray;


public abstract class GetTalksApiMethod extends HttpEndpoint {

	@Override protected String getName() { return "get_talks"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final Storage storage = getStorage();

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String talkEventId = requestJson.getString("talkEventId");

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		final TalkEvent talkEvent = getTalkEventsMapper(storage).getItem(talkEventId);

		final Mapper<Talk, String> talksMapper = getTalksMapper(storage);

		responseJson.put("talks", toJsonArray(
			talkEvent.getTalkIds(),
			talkId -> {
				final Talk talk = talksMapper.getItem(talkId);
				final JSONObject talkJson = new JSONObject();
				talkJson.put("id", 					talkId);
				talkJson.put("name", 				talk.getName());
				talkJson.put("shortDescription", 	talk.getShortDescription());
				talkJson.put("description", 		talk.getDescription());
				talkJson.put("imageUrl",			talk.getImageUrl());
				{ final @Nullable Date startDate = talk.getStartDate();
					if (startDate != null) talkJson.put("startDate", startDate.getTime());
				}
				{ final @Nullable Date endDate = talk.getEndDate();
					if (endDate != null) talkJson.put("endDate", endDate.getTime());
				}
				return talkJson;
			}
		));

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
			@Override public CountableMap<String, String> getHeaders() { return new CountableMap.Default<String, String>(
				"Access-Control-Allow-Origin", 	"*",
				"Access-Control-Allow-Methods", "*",
				"Access-Control-Allow-Headers", "*"
			); }
		};

	}

}
