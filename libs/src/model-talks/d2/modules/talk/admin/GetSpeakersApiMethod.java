package d2.modules.talk.admin;


import d2.modules.talk.model.Speaker;
import d2.modules.talk.model.TalkEvent;
import ic.id.Mapper;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.talk.model.Speaker.getSpeakersMapper;
import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;
import static ic.json.JsonArrays.toJsonArray;


public abstract class GetSpeakersApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "get_speakers"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final Storage storage = getStorage();

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String talkEventId = requestJson.getString("talkEventId");

		final JSONObject responseJson = new JSONObject();

		final TalkEvent talkEvent = getTalkEventsMapper(storage).getItem(talkEventId);

		final Mapper<Speaker, String> speakersMapper = getSpeakersMapper(storage);

		responseJson.put("speakers", toJsonArray(
			talkEvent.getSpeakerIds(),
			speakerId -> {
				final Speaker speaker = speakersMapper.getItem(speakerId);
				final JSONObject speakerJson = new JSONObject();
				speakerJson.put("id", speakerId);
				speakerJson.put("name", speaker.getName());
				speakerJson.put("surname", speaker.getSurname());
				return speakerJson;
			}
		));

		responseJson.put("status", "SUCCESS");

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
