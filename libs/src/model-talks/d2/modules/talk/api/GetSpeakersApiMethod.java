package d2.modules.talk.api;


import d2.modules.talk.model.Speaker;
import d2.modules.talk.model.TalkEvent;
import ic.id.Mapper;
import ic.interfaces.getter.Safe2Getter1;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.Storage;
import ic.struct.order.ConvertOrder;
import ic.struct.order.Order;
import ic.struct.order.OrderedCountableSet;
import ic.text.Charset;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.json.JSONException;
import org.json.JSONObject;

import static d2.modules.talk.model.Speaker.getSpeakersMapper;
import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;
import static ic.json.JsonArrays.toJsonArray;
import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;
import static ic.throwables.Skip.SKIP;


public abstract class GetSpeakersApiMethod extends HttpEndpoint {

	@Override protected String getName() { return "get_speakers"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final Storage storage = getStorage();

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String talkEventId = requestJson.getString("talkEventId");

		final boolean getOnlyList = requestJson.optBoolean("getOnlyList", false);

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		final TalkEvent talkEvent = getTalkEventsMapper(storage).getItem(talkEventId);

		final Mapper<Speaker, String> speakersMapper = getSpeakersMapper(storage);

		final OrderedCountableSet<Speaker> speakers = new OrderedCountableSet.Default<Speaker>(
			new ConvertOrder<>(
				ALPHABETIC_STRING_ORDER,
				speaker -> {
					final String surname = speaker.getSurname();
					if (surname == null) return "";
					return surname;
				}
			),
			talkEvent.getSpeakers(storage)
		);

		responseJson.put("speakers", toJsonArray(
			speakers,
			(Safe2Getter1<Object, Speaker, JSONException, Skip>) speaker -> {
				if (getOnlyList) {
					if (!speaker.toShowInList()) throw SKIP;
				}
				final JSONObject speakerJson = new JSONObject();
				speakerJson.put("id", 				speakersMapper.getId(speaker));
				speakerJson.put("name", 			speaker.getName());
				speakerJson.put("surname",			speaker.getSurname());
				speakerJson.put("shortDescription", speaker.getShortDescription());
				speakerJson.put("description", 		speaker.getDescription());
				speakerJson.put("imageUrl",			speaker.getImageUrl());
				if (!getOnlyList) {
					speakerJson.put("showInList", speaker.toShowInList());
				}
				return speakerJson;
			}
		));

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
