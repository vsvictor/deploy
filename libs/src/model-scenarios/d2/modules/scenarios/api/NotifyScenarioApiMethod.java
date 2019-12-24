package d2.modules.scenarios.api;


import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.network.http.ProtectedHttpEndpoint;
import ic.storage.CacheStorage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;


public abstract class NotifyScenarioApiMethod extends ProtectedHttpEndpoint {

	@Override protected String getName() { return "notify_scenario"; }

	protected abstract CacheStorage getStorage();

	protected abstract void notifyScenario(String subjectId, String scenarioId);

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectId = requestJson.getString("subjectId");
		final String scenarioId = requestJson.getString("scenarioId");

		notifyScenario(subjectId, scenarioId);

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject responseJson = new JSONObject();
			responseJson.put("status", "SUCCESS");
			return responseJson;
		} };

	}

}
