package d2.modules.scenarios.api;


import d2.modules.scenarios.model.Block;
import d2.modules.scenarios.model.History;
import ic.annotations.Same;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.BindingStorage;
import ic.text.Charset;
import ic.throwables.*;
import org.json.JSONObject;
import static d2.modules.scenarios.model.History.getHistory;


public abstract class GetBlockApiMethod extends ProtectedApiMethod {

	@Same
	protected abstract BindingStorage getStorage();

	@Override protected String getName() { return "get_block"; }

	protected abstract void onScenarioFinish(String userId, String subjectId, String scenarioId, boolean firstTime, int currentTimeScore);

	protected abstract String getUserRoleById(String id) throws NotExists;

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String userId = requestJson.getString("userId");

		try {
			getUserRoleById(userId);
		} catch (NotExists notExists) {
			return new JsonResponse() { @Override protected JSONObject getJson() {
				final JSONObject json = new JSONObject();
				json.put("status", "NO_SUCH_USER");
				return json;
			} };
		}

		final String subjectId 	= requestJson.getString("subjectId");
		final String scenarioId = requestJson.getString("scenarioId");
		final Integer way; { final int wayValue = requestJson.optInt("way", Integer.MIN_VALUE);
			way = wayValue == Integer.MIN_VALUE ? null : wayValue;
		}

		try {

			final History history = getHistory(getStorage(), userId, subjectId, scenarioId);

			final Block block;
			final int score;
			final boolean firstTime;

			synchronized (history) {

				if (way != null) {
					history.go(way, new History.GoCallback() {
						@Override protected void onFinish(String userId, String subjectId, String scenarioId, boolean firstTime, int currentTimeScore) {
							onScenarioFinish(userId, subjectId, scenarioId, firstTime, currentTimeScore);
						}
					});
				}

				block 		= history.getLastBlock();
				score		= history.getCurrentTimeScore();
				firstTime	= history.isFirstTime();

			}

			return new JsonResponse() { @Override protected JSONObject getJson() {

				final JSONObject json = new JSONObject();

				json.put("status", "SUCCESS");

				json.put("blockType", 		block.type);
				json.put("content", 		block.content);
				json.put("keyboardType", 	block.keyboardType);
				json.put("score",			score);
				json.put("firstTime",		firstTime);

				return json;

			} };

		} catch (Empty empty) {

			return new JsonResponse() { @Override protected JSONObject getJson() {
				final JSONObject json = new JSONObject();
				json.put("status", "EMPTY_SCENARIO");
				return json;
			} };

		}

	}

}
