package d2.modules.scenarios.api;


import d2.modules.scenarios.model.Block;
import d2.modules.scenarios.model.History;
import d2.modules.scenarios.model.Scenario;
import ic.interfaces.action.SafeAction2;
import ic.interfaces.getter.Safe2Getter1;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.BindingStorage;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.list.List;
import ic.text.Charset;
import ic.throwables.Break;
import ic.throwables.Empty;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static d2.modules.scenarios.model.Scenario.getScenariosMapper;
import static ic.json.JsonArrays.toJsonArray;
import static ic.struct.collection.Collections.sumInt;


public abstract @Deprecated class GetStatisticsApiMethod extends ProtectedApiMethod {


	@Override protected String getName() { return "get_statistics"; }


	protected abstract BindingStorage getStorage();


	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final BindingStorage storage = getStorage();

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectId = requestJson.getString("subjectId");
		final String scenarioId = requestJson.getString("scenarioId");

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		/*final Scenario scenario = getScenariosMapper(storage, subjectId).getItem(scenarioId);

		final Collection<History> histories = getHistoriesByScenario(storage, subjectId, scenarioId);

		final List<Block> blocks; try {
			blocks = scenario.getBlocks();
		} catch (Empty empty) {
			return new JsonResponse() {
				@Override protected JSONObject getJson() {
					final JSONObject responseJson = new JSONObject();
					responseJson.put("status", "EMPTY_SCENARIO");
					return responseJson;
				}
			};
		}

		responseJson.put("blocks", toJsonArray(blocks, (Safe2Getter1<Object, Block, JSONException, Skip>) block -> {

			final JSONObject blockJson = new JSONObject();

			blockJson.put("id", block.id);
			blockJson.put("type", block.type);
			blockJson.put("content", block.content);

			{ final JSONArray waysJson = new JSONArray();

				block.ways.forEach((SafeAction2<Block.Way, Integer, Break>) (way, wayIndex) -> waysJson.put(

					sumInt(new ConvertCollection<>(
						histories,
						history -> history.contains(block.id, wayIndex) ? 1 : 0
					))

				));

				blockJson.put("ways", waysJson);

			}

			return blockJson;

		}));*/

		return new JsonResponse() { @Override protected JSONObject getJson() { return responseJson; } };

	}


}
