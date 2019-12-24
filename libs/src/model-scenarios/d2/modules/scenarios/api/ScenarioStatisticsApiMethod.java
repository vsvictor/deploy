package d2.modules.scenarios.api;


import d2.modules.scenarios.model.Block;
import d2.modules.scenarios.model.History;
import d2.modules.scenarios.model.Scenario;
import ic.interfaces.action.SafeAction1;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.BindingStorage;
import ic.struct.collection.Collection;
import ic.struct.list.ConvertList;
import ic.struct.list.List;
import ic.struct.map.CountableMap;
import ic.struct.map.EditableMap;
import ic.text.Charset;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static d2.modules.scenarios.model.History.getHistory;
import static d2.modules.scenarios.model.Scenario.getScenariosMapper;
import static ic.json.JsonArrays.toJsonArray;
import static ic.struct.variable.Variable.Constant;


public abstract class ScenarioStatisticsApiMethod extends ProtectedHttpEndpoint {

	@Override protected String getName() {
		return "scenario_statistics";
	}

	protected abstract BindingStorage getStorage();

	protected abstract Collection<String> getUserIds();

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectId 	= requestJson.getString("subjectId");
		final String scenarioId = requestJson.getString("scenarioId");

		final BindingStorage storage = getStorage();

		final EditableMap<EditableMap<Integer, Integer>, String> counterByBlockIdAndWay = new EditableMap.Default<>(); {

			getUserIds().forEach(userId -> {

				final List<History.Item> historyItems = getHistory(storage, userId, subjectId, scenarioId).getItems();

				historyItems.forEach(new SafeAction1<>() {
					History.Item previousItem;
					@Override public void run(@NotNull History.Item item) {
						if (previousItem != null) {
							final EditableMap<Integer, Integer> counterByWay = counterByBlockIdAndWay.createIfNull(
								previousItem.blockId,
								EditableMap.Default::new
							);
							counterByWay.set(
								item.way,
								counterByWay.get(item.way, new Constant<>(0)) + 1
							);
						}
						previousItem = item;
					}
				});

			});

		}

		final List<Block> blocks; {
			List<Block> blocksValue;
			try {
				final Scenario scenario = getScenariosMapper(storage, subjectId).getItemOrThrow(scenarioId);
				blocksValue = scenario.getBlocks();
			} catch (Empty empty) {
				return new JsonResponse() {
					@Override protected JSONObject getJson() {
						final JSONObject responseJson = new JSONObject();
						responseJson.put("status", "EMPTY_SCENARIO");
						return responseJson;
					}
				};
			} catch (NotExists notExists) {
				blocksValue = new ConvertList<Block, String>(
					new List.Default<>(counterByBlockIdAndWay.getKeys()),
					blockId -> new Block(blockId, "TEXT", "", "NONE", new List.Default<>())
				);
			}
			blocks = blocksValue;
		}

		final JSONObject responseJson = new JSONObject();
		responseJson.put("status", "SUCCESS");

		responseJson.put("blocks", toJsonArray(blocks, block -> {

			final CountableMap<Integer, Integer> counterByWay = counterByBlockIdAndWay.get(
				block.id,
				EditableMap.Default::new
			);

			final JSONObject blockJson = new JSONObject();
			blockJson.put("id", block.id);
			blockJson.put("content", block.content);
			blockJson.put("ways", toJsonArray(
				new List.Default<>(
					Integer.toString(counterByWay.get(0, new Constant<>(0))),
					Integer.toString(counterByWay.get(1, new Constant<>(0))),
					Integer.toString(counterByWay.get(2, new Constant<>(0))),
					Integer.toString(counterByWay.get(3, new Constant<>(0)))
				),
				count -> count
			));

			return blockJson;

		}));

		return new JsonResponse.Final(responseJson);

	}

}
