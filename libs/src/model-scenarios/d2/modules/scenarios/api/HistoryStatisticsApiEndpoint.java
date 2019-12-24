package d2.modules.scenarios.api;


import d2.modules.scenarios.model.Scenario;
import d2.modules.scenarios.model.Subject;
import ic.id.Mapper;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.BindingStorage;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.collection.JoinCollection;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.scenarios.model.History.getHistory;
import static d2.modules.scenarios.model.Scenario.getScenariosMapper;
import static d2.modules.scenarios.model.Subject.getSubjectsMapper;
import static ic.date.DateFormat.formatDateOrThrow;
import static ic.json.JsonArrays.toJsonArray;
import static ic.throwables.Skip.SKIP;


@Deprecated
public abstract class HistoryStatisticsApiEndpoint extends ProtectedHttpEndpoint {

	@Override protected String getName() { return "history_statistics"; }

	protected abstract Collection<String> getUserIds();
	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		final BindingStorage storage = getStorage();

		responseJson.put("history", toJsonArray(

			new JoinCollection<>(
				new ConvertCollection<>(
					getUserIds(),
					userId -> {
						final Mapper<Subject, String> subjectsMapper = getSubjectsMapper(storage);
						return new JoinCollection<>(
							new ConvertCollection<>(
								subjectsMapper.getIds(),
								subjectId -> {
									final Subject subject = subjectsMapper.getItem(subjectId);
									final Mapper<Scenario, String> scenariosMapper = getScenariosMapper(storage, subjectId);
									return new JoinCollection<>(
										new ConvertCollection<>(
											scenariosMapper.getIds(),
											scenarioId -> {
												final Scenario scenario = scenariosMapper.getItem(scenarioId);
												return new ConvertCollection<>(
													getHistory(storage, userId, subjectId, scenarioId).getItems(),
													item -> {
														final JSONObject itemJson = new JSONObject();
														itemJson.put("userId", userId);
														itemJson.put("subject", subject.getTitle());
														itemJson.put("scenario", scenario.getTitle());
														try {
															itemJson.put("date", formatDateOrThrow(item.date, "yyyy.MM.dd HH:mm:ss"));
														} catch (NotExists notExists) { throw SKIP; }
														try {
															itemJson.put("blockText", scenario.getBlockByIdOrThrow(item.blockId).content);
														} catch (NotExists notExists) {
															itemJson.put("blockText", "blockId: " + item.blockId);
														}
														itemJson.put("way", item.way);
														itemJson.put("score", item.score);
														return itemJson;
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

		return new JsonResponse() {
			@Override protected Object getJson() { return responseJson; }
		};

	}

}
