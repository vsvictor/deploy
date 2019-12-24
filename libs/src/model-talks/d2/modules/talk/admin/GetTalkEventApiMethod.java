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

import java.text.SimpleDateFormat;
import java.util.Date;

import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;


public abstract class GetTalkEventApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "get_talk_event"; }

	protected abstract Storage getStorage();

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm");

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String id = requestJson.getString("id");

		final TalkEvent talkEvent = getTalkEventsMapper(getStorage()).getItem(id);

		final JSONObject responseJson = new JSONObject();

		responseJson.put("title", 		talkEvent.getTitle());
		responseJson.put("description", talkEvent.getDescription());
		responseJson.put("imageUrl", 	talkEvent.getImageUrl());

		{ final Date startDate = talkEvent.getStartDate();
			if (startDate != null) {
				responseJson.put("startDate", DATE_FORMAT.format(startDate));
			}
		}

		{ final Date endDate = talkEvent.getEndDate();
			if (endDate != null) {
				responseJson.put("endDate", DATE_FORMAT.format(endDate));
			}
		}

		responseJson.put("status", "SUCCESS");

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
