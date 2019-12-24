package d2.modules.scenarios.api;


import d2.modules.scenarios.admin.ProtectedAdminApiMethod;
import d2.modules.scenarios.model.Subject;
import ic.interfaces.getter.Safe3Getter1;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.network.http.ProtectedHttpEndpoint;
import ic.storage.BindingStorage;
import ic.struct.set.CountableSet;
import ic.text.Charset;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.json.JSONException;
import org.json.JSONObject;

import static d2.modules.scenarios.model.Subject.getSubjectsMapper;
import static ic.json.JsonArrays.jsonArrayToList;


public abstract class AddSubjectApiMethod extends ProtectedHttpEndpoint {

	@Override protected String getName() { return "add_subject"; }

	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectTitle = requestJson.getString("title");

		final Subject subject = new Subject(subjectTitle);

		if (requestJson.has("roles")) {
			final CountableSet<String> roles = new CountableSet.Default<>(
				jsonArrayToList(
					requestJson.getJSONArray("roles"),
					(Safe3Getter1<String, String, JSONException, UnableToParse, Skip>) role -> role
				)
			);
			subject.setRoles(roles);
		}

		final String subjectId = getSubjectsMapper(getStorage()).getId(subject);

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject json = new JSONObject();
			json.put("status", "SUCCESS");
			json.put("id", subjectId);
			return json;
		} };

	}

}
