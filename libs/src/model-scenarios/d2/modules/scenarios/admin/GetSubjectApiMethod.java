package d2.modules.scenarios.admin;


import d2.modules.scenarios.model.Scenario;
import d2.modules.scenarios.model.Subject;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.BindingStorage;
import ic.struct.collection.Collection;
import ic.text.Charset;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.scenarios.model.Scenario.getScenariosMapper;
import static ic.json.JsonArrays.toJsonArray;


@Deprecated
public abstract class GetSubjectApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "get_subject"; }

	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectId = requestJson.getString("id");

		final Subject subject; try {
			subject = Subject.getSubjectsMapper(getStorage()).getItem(subjectId);
		} catch (NotExists.Runtime notFound) {
			return new JsonResponse() { @Override protected JSONObject getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUBJECT_NOT_EXISTS");
				return responseJson;
			} };
		}

		final Collection<Scenario> scenarios = getScenariosMapper(getStorage(), subjectId).getItems();

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
			responseJson.put("title", subject.getTitle());
			responseJson.put("scenarios", toJsonArray(scenarios, scenario -> {
				final JSONObject scenarioJson = new JSONObject();
				scenarioJson.put("id", getScenariosMapper(getStorage(), subjectId).getId(scenario));
				scenarioJson.put("title", scenario.getTitle());
				scenarioJson.put("roles", toJsonArray(scenario.getRoles(), role -> role));
				return scenarioJson;
			}));
		}

		return new JsonResponse() { @Override protected JSONObject getJson() { return responseJson; } };

	}

}