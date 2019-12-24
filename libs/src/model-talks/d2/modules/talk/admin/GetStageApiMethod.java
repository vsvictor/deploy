package d2.modules.talk.admin;


import d2.modules.talk.model.Stage;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import static d2.modules.talk.model.Stage.getStagesMapper;


public abstract class GetStageApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "get_stage"; }

	protected abstract Storage getStorage();

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm");

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String id = requestJson.getString("id");

		final Stage stage = getStagesMapper(getStorage()).getItem(id);

		final JSONObject responseJson = new JSONObject();

		responseJson.put("name", 			stage.getName());
		responseJson.put("coverImageUrl", 	stage.getCoverImageUrl());
		responseJson.put("planImageUrl", 	stage.getPlanImageUrl());

		responseJson.put("status", "SUCCESS");

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
