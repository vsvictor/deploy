package d2.modules.scenarios.api;


import d2.modules.scenarios.model.Scenario;
import ic.annotations.Same;
import ic.interfaces.getter.Safe2Getter1;
import ic.log.Logger;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.json.JsonArrays;
import ic.network.http.JsonResponse;
import ic.network.http.ProtectedHttpEndpoint;
import ic.storage.BindingStorage;
import ic.struct.collection.Collection;
import ic.text.Charset;
import ic.throwables.NotExists;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.json.JSONException;
import org.json.JSONObject;

import static d2.modules.scenarios.model.History.isScenarioPassed;
import static d2.modules.scenarios.model.Scenario.getScenariosMapper;
import static ic.log.InstantCoffeeLogger.LOGGER;
import static ic.throwables.Skip.SKIP;


public abstract class GetScenariosApiEndpoint extends ProtectedHttpEndpoint {

	@Same
	protected abstract BindingStorage getStorage();

	protected abstract String getUserRoleById(String id) throws NotExists;

	@Override protected String getName() { return "get_scenarios"; }

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final BindingStorage storage = getStorage();

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String userId 	= requestJson.getString("userId");
		final String subjectId 	= requestJson.getString("subjectId");

		final String role; try {
			role = getUserRoleById(userId);
		} catch (NotExists notExists) {
			return new JsonResponse() {
				@Override protected JSONObject getJson() {
					final JSONObject responseJson = new JSONObject();
					responseJson.put("status", "USER_NOT_FOUND");
					return responseJson;
				}
			};
		}

		final Collection<Scenario> scenarios = getScenariosMapper(storage, subjectId).getItems();

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		responseJson.put("scenarios", JsonArrays.toJsonArray(scenarios, (Safe2Getter1<Object, Scenario, JSONException, Skip>) scenario -> {
			if (!scenario.getRoles().contains(role)) throw SKIP;
			final JSONObject json = new JSONObject();
			json.put("id", 		getScenariosMapper(storage, subjectId).getId(scenario));
			json.put("title", 	scenario.getTitle());
			json.put("passed", 	isScenarioPassed(storage, userId, subjectId, getScenariosMapper(storage, subjectId).getId(scenario)));
			return json;
		}));

		return new JsonResponse() { @Override protected JSONObject getJson() {
			return responseJson;
		} };

	}

}
