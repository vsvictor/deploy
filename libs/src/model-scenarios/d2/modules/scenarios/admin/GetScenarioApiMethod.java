package d2.modules.scenarios.admin;


import d2.modules.scenarios.model.Scenario;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.BindingStorage;
import ic.text.Charset;
import ic.throwables.Empty;
import ic.throwables.UnableToParse;
import org.json.JSONArray;
import org.json.JSONObject;

import static ic.json.JsonArrays.toJsonArray;


@Deprecated
public abstract class GetScenarioApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "get_scenario"; }

	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectId = requestJson.getString("subjectId");
		final String scenarioId = requestJson.getString("scenarioId");

		final Scenario scenario = Scenario.getScenariosMapper(getStorage(), subjectId).getItem(scenarioId);

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
		}

		try {

			responseJson.put("blocks", toJsonArray(scenario.getBlocks(), block -> {

				final JSONObject blockJson = new JSONObject();

				blockJson.put("id", 			block.id);
				blockJson.put("type", 			block.type);
				blockJson.put("content", 		block.content);
				blockJson.put("keyboardType", 	block.keyboardType);

				blockJson.put("ways", toJsonArray(block.ways, way -> {
					final JSONObject wayJson = new JSONObject();
					wayJson.put("blockId", 	way.blockId);
					wayJson.put("score", 	way.score);
					return wayJson;
				}));

				return blockJson;

			}));

		} catch (Empty empty) {
			responseJson.put("blocks", new JSONArray());
		}

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
