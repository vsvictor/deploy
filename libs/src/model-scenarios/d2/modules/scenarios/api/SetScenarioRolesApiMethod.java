package d2.modules.scenarios.api;


import d2.modules.scenarios.admin.ProtectedAdminApiMethod;
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

import static d2.modules.scenarios.model.Scenario.getScenariosMapper;
import static ic.json.JsonArrays.jsonArrayToList;


public abstract class SetScenarioRolesApiMethod extends ProtectedHttpEndpoint {

	@Override protected String getName() { return "set_scenario_roles"; }

	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectId = requestJson.getString("subjectId");
		final String scenarioId = requestJson.getString("scenarioId");

		final CountableSet<String> roles = new CountableSet.Default<>(
			jsonArrayToList(
				requestJson.getJSONArray("roles"),
				(Safe3Getter1<String, String, JSONException, UnableToParse, Skip>) role -> role
			)
		);

		getScenariosMapper(getStorage(), subjectId).getItem(scenarioId).setRoles(roles);

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject responseJson = new JSONObject();
			responseJson.put("status", "SUCCESS");
			return responseJson;
		} };

	}

}
