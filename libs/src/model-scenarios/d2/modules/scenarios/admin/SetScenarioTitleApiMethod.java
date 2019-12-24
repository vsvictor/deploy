package d2.modules.scenarios.admin;


import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.BindingStorage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.scenarios.model.Scenario.getScenariosMapper;


@Deprecated
public abstract class SetScenarioTitleApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "set_scenario_title"; }

	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectId = requestJson.getString("subjectId");
		final String scenarioId = requestJson.getString("scenarioId");

		final String title = requestJson.getString("title");

		getScenariosMapper(getStorage(), subjectId).getItem(scenarioId).setTitle(title);

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject responseJson = new JSONObject();
			responseJson.put("status", "SUCCESS");
			return responseJson;
		} };

	}

}
