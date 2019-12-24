package d2.modules.talk.admin;


import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.text.Charset;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.talk.model.Talk.getTalksMapper;


public abstract class RemoveSpeakerFromTalkApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "remove_speaker_from_talk"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String talkId 	= requestJson.getString("talkId");
		final String speakerId 	= requestJson.getString("speakerId");

		try {

			getTalksMapper(getStorage()).getItem(talkId).removeSpeaker(speakerId);

		} catch (NotExists notExists) {

			return new JsonResponse() {
				@Override protected JSONObject getJson() {
					final JSONObject responseJson = new JSONObject();
					responseJson.put("status", "NOT_EXISTS");
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
