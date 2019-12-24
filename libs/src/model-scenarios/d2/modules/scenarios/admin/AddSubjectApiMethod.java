package d2.modules.scenarios.admin;


import d2.modules.scenarios.model.Subject;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.BindingStorage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.scenarios.model.Subject.getSubjectsMapper;


@Deprecated
public abstract class AddSubjectApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "add_subject"; }

	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectTitle = requestJson.getString("title");

		final String subjectId = getSubjectsMapper(getStorage()).getId(
			new Subject(subjectTitle)
		);

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject json = new JSONObject();
			json.put("status", "SUCCESS");
			json.put("id", subjectId);
			return json;
		} };

	}

}
