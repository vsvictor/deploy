package d2.modules.scenarios.api;


import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.network.http.ProtectedHttpEndpoint;
import ic.storage.BindingStorage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.scenarios.model.Subject.getSubjectsMapper;


public abstract class SetSubjectTitleApiMethod extends ProtectedHttpEndpoint {

	@Override protected String getName() { return "set_subject_title"; }

	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectId = requestJson.getString("subjectId");

		final String title = requestJson.getString("title");

		getSubjectsMapper(getStorage()).getItem(subjectId).setTitle(title);

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject responseJson = new JSONObject();
			responseJson.put("status", "SUCCESS");
			return responseJson;
		} };

	}

}
