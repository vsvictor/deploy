package d2.modules.talk.api;


import d2.modules.talk.model.TalkEvent;
import ic.id.Mapper;
import ic.interfaces.getter.Safe2Getter1;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.Storage;
import ic.struct.map.CountableMap;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;
import static ic.json.JsonArrays.toJsonArray;


public abstract class GetTalkEventsApiMethod extends HttpEndpoint {

	@Override protected String getName() { return "get_talk_events"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		final Mapper<TalkEvent, String> talkEventsMapper = getTalkEventsMapper(getStorage());

		responseJson.put("talkEvents", toJsonArray(
			talkEventsMapper.getIds(),
			(Safe2Getter1<Object, String, JSONException, Skip>) talkEventId -> {
				final TalkEvent talkEvent = talkEventsMapper.getItem(talkEventId);
				final JSONObject talkEventJson = new JSONObject();
				talkEventJson.put("id", 			talkEventId);
				talkEventJson.put("title", 			talkEvent.getTitle());
				talkEventJson.put("description", 	talkEvent.getDescription());
				talkEventJson.put("imageUrl",		talkEvent.getImageUrl());
				{ final @Nullable Date startDate = talkEvent.getStartDate();
					if (startDate != null) talkEventJson.put("startDate", startDate.getTime());
				}
				{ final @Nullable Date endDate = talkEvent.getEndDate();
					if (endDate != null) talkEventJson.put("endDate", endDate.getTime());
				}
				return talkEventJson;
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
