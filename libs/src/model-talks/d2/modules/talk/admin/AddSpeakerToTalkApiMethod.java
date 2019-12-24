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

import static d2.modules.talk.model.Talk.getTalksMapper;


public abstract class AddSpeakerToTalkApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "add_speaker_to_talk"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String talkId 	= requestJson.getString("talkId");
		final String speakerId 	= requestJson.getString("speakerId");

		try {

			getTalksMapper(getStorage()).getItem(talkId).addSpeaker(speakerId);

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
