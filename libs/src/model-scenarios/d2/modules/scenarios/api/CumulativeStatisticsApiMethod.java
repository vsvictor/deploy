package d2.modules.scenarios.api;


import d2.modules.scenarios.model.History;
import d2.modules.scenarios.model.Scenario;
import d2.modules.scenarios.model.Subject;
import ic.id.Mapper;
import ic.interfaces.action.SafeAction1;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.BindingStorage;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.collection.JoinCollection;
import ic.struct.list.List;
import ic.struct.map.EditableMap;
import ic.throwables.*;
import org.json.JSONObject;

import static d2.modules.scenarios.model.History.getHistory;
import static d2.modules.scenarios.model.Scenario.getScenariosMapper;
import static d2.modules.scenarios.model.Subject.getSubjectsMapper;
import static ic.json.JsonArrays.toJsonArray;
import static ic.struct.variable.Variable.Constant;


public abstract class CumulativeStatisticsApiMethod extends ProtectedHttpEndpoint {

	@Override protected String getName() { return "cumulative_statistics"; }

	protected abstract BindingStorage getStorage();

	protected abstract Collection<String> getUserIds();

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final BindingStorage storage = getStorage();

		final JSONObject responseJson = new JSONObject();

		final Mapper<Subject, String> subjectsMapper = getSubjectsMapper(storage);

		responseJson.put("blocks", toJsonArray(
			new JoinCollection<JSONObject>(
				new ConvertCollection<Collection<JSONObject>, String>(
					subjectsMapper.getIds(),
					subjectId -> {
						final Subject subject = subjectsMapper.getItem(subjectId);
						final Mapper<Scenario, String> scenariosMapper = getScenariosMapper(storage, subjectId);
						return new JoinCollection<JSONObject>(
							new ConvertCollection<Collection<JSONObject>, String>(
								scenariosMapper.getIds(),
								scenarioId -> {
									final Scenario scenario = scenariosMapper.getItem(scenarioId);
									final EditableMap<EditableMap<Integer, Integer>, String> counterByBlockIdAndWay = new EditableMap.Default<>();
									getUserIds().forEach(userId -> {
										final List<History.Item> historyItems = getHistory(storage, userId, subjectId, scenarioId).getItems();
										historyItems.forEach(new SafeAction1<History.Item, Break>() {
											History.Item previousItem;
											@Override public void run(History.Item item) {
												assert item != null;
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
									return new JoinCollection<JSONObject>(
										new ConvertCollection<Collection<JSONObject>, String>(
											counterByBlockIdAndWay.getKeys(),
											blockId -> {
												final String blockName; {
													String value;
													try {
														value = scenario.getBlockByIdOrThrow(blockId).content;
													} catch (NotExists notExists) {
														value = "blockId: " + blockId;
													}
													blockName = value;
												}
												final EditableMap<Integer, Integer> counterByWay = counterByBlockIdAndWay.getNotNull(blockId);
												return new ConvertCollection<JSONObject, Integer>(
													counterByWay.getKeys(),
													way -> {
														final JSONObject blockJson = new JSONObject();
														blockJson.put("subjectName", subject.getTitle());
														blockJson.put("scenarioName", scenario.getTitle());
														blockJson.put("blockName", blockName);
														blockJson.put("way", way);
														blockJson.put("count", counterByWay.getNotNull(way));
														return blockJson;
													}
												);
											}
										)
									);
								}
							)
						);
					}
				)
			),
			json -> json
		));

		responseJson.put("status", "SUCCESS");

		return new JsonResponse() {
			@Override protected Object getJson() { return responseJson; }
		};

	}

}
