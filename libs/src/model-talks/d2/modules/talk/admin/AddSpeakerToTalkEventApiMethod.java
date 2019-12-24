package d2.modules.talk.admin;


import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.text.Charset;
import ic.throwables.AlreadyExists;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;


public abstract class AddSpeakerToTalkEventApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "add_speaker_to_talk_event"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String talkEventId = requestJson.getString("talkEventId");
		final String speakerId = requestJson.getString("speakerId");

		try {

			getTalkEventsMapper(getStorage()).getItem(talkEventId).addSpeaker(speakerId);

		} catch (AlreadyExists alreadyExists) {

			return new JsonResponse() {
				@Override protected JSONObject getJson() {
					final JSONObject responseJson = new JSONObject();
					responseJson.put("status", "ALREADY_EXISTS");
					return responseJson;
				}
			};

		}

		return new JsonResponse() {
			@Override protected JSONObject getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUCCESS");
				return responseJson;
			}
		};

	}

}
