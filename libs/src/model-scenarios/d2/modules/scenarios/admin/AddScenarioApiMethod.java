package d2.modules.scenarios.admin;


import d2.modules.scenarios.model.Scenario;
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
public abstract class AddScenarioApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "add_scenario"; }

	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectId 		= requestJson.getString("subjectId");
		final String scenarioTitle 	= requestJson.getString("title");

		final String scenarioId = getScenariosMapper(getStorage(), subjectId).getId(
			new Scenario(scenarioTitle)
		);

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject json = new JSONObject();
			json.put("status", 	"SUCCESS");
			json.put("id", 	scenarioId);
			return json;
		} };

	}

}
