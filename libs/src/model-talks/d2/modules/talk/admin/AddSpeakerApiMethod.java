package d2.modules.talk.admin;


import d2.modules.talk.model.Speaker;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.talk.model.Speaker.getSpeakersMapper;


public abstract class AddSpeakerApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "add_speaker"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String name = requestJson.getString("name");

		final Speaker speaker = new Speaker();
		speaker.setName(name);

		final String speakerId = getSpeakersMapper(getStorage()).getId(speaker);

		return new JsonResponse() {
			@Override protected JSONObject getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUCCESS");
				responseJson.put("id", speakerId);
				return responseJson;
			}
		};

	}

}
