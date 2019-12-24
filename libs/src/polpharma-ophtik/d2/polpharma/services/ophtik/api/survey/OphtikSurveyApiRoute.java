package d2.polpharma.services.ophtik.api.survey;


import d2.modules.scenarios.api.ScenariosApiRoute;
import d2.modules.scenarios.model.Scenario;
import d2.polpharma.services.PolpharmaBackendKt;
import d2.polpharma.services.ophtik.model.User;
import ic.network.http.BasicHttpAuth;
import ic.network.http.HttpClient;
import ic.network.http.HttpException;
import ic.network.http.HttpRequest;
import ic.storage.BindingStorage;
import ic.struct.collection.Collection;
import ic.text.Charset;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static d2.polpharma.services.ophtik.OphtikStoragesKt.ophtikSurveyStorage;
import static ic.ServiceAppKt.getTier;
import static ic.json.JsonArrays.toJsonArray;


public class OphtikSurveyApiRoute extends ScenariosApiRoute {

	@Override protected String getName() { return "survey"; }
	@Override protected String getBearer() { return "06bc983a9307c501"; }
	@Override protected Collection<String> getUserIds() { return User.mapper.getIds(); }
	@Override protected void notifyScenario(String subjectId, String scenarioId) {
		final Scenario scenario = Scenario.getScenariosMapper(ophtikSurveyStorage, subjectId).getItem(scenarioId);
		final JSONObject requestJson = new JSONObject();
		requestJson.put("subjectId", subjectId);
		requestJson.put("scenarioId", scenarioId);
		requestJson.put("title", scenario.getTitle());
		requestJson.put("roles", toJsonArray(scenario.getRoles(), role -> role));
		if (getTier().equals("stage")) {
			try {
				HttpClient.request(new HttpRequest(
					"https://www.corezoid.com/api/1/json/public/538465/6e450f688ec59d3249e2007d8b8e2bb705b0c6c4", "POST",
					Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
				));
			} catch (IOException e) 	{ throw new IOException.Runtime(e);
			} catch (HttpException e) 	{ throw new HttpException.Runtime(e); }
		} else
		if (getTier().equals("prod")) {
			try {
				HttpClient.request(new HttpRequest(
					"https://www.corezoid.com/api/1/json/public/540038/8d719a98e84a87a61f5c31935257745bb41ce479", "POST",
					Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
				));
			} catch (IOException e) 	{ throw new IOException.Runtime(e);
			} catch (HttpException e) 	{ throw new HttpException.Runtime(e); }
		}
	}
	@Override protected BindingStorage getStorage() { return ophtikSurveyStorage; }
	@Override protected String getUserRoleById(String id) { return "USER"; }

	@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { PolpharmaBackendKt.checkPolpharmaServerKey(serverKey); }
	@Override protected void onScenarioFinish(String userId, String subjectId, String scenarioId, boolean firstTime, int currentTimeScore) {}
	@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { PolpharmaBackendKt.checkPolpharmaSuperadminAuth(auth); }

}
