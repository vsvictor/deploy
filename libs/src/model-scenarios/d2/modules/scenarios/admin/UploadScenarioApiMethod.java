package d2.modules.scenarios.admin;


import d2.modules.scenarios.model.Block;
import d2.modules.scenarios.model.Scenario;
import ic.interfaces.getter.Safe3Getter1;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.BindingStorage;
import ic.struct.list.List;
import ic.text.Charset;
import ic.throwables.NotExists;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.json.JSONException;
import org.json.JSONObject;

import static d2.modules.scenarios.model.Scenario.getScenariosMapper;
import static ic.json.JsonArrays.jsonArrayToList;


@Deprecated
public abstract class UploadScenarioApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "upload_scenario"; }

	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String subjectId 	= requestJson.getString("subjectId");
		final String scenarioId = requestJson.getString("scenarioId");

		final List<Block> blocks = jsonArrayToList(
			requestJson.getJSONArray("blocks"),
			(Safe3Getter1<Block, JSONObject, JSONException, UnableToParse, Skip>) json -> {
				final String type = json.getString("type");
				final String id = json.getString("id");
				final String content = json.getString("content");
				final String keyboardType = json.getString("keyboardType");
				final List<Block.Way> ways = jsonArrayToList(
					json.getJSONArray("ways"),
					(Safe3Getter1<Block.Way, JSONObject, JSONException, UnableToParse, Skip>) json1 -> new Block.Way(
						json1.getString("blockId"),
						json1.getInt("score")
					)
				);
				return new Block(id, type, content, keyboardType, ways);
			}
		);

		if (blocks.isEmpty()) {
			return new JsonResponse() {
				@Override public int getStatus() { return STATUS_BAD_REQUEST; }
				@Override protected JSONObject getJson() {
					final JSONObject json = new JSONObject();
					json.put("status", "EMPTY_SCENARIO");
					return json;
				}
			};
		}

		final Scenario scenario; try {
			scenario = getScenariosMapper(getStorage(), subjectId).getItem(scenarioId);
		} catch (NotExists.Runtime notFound) {
			return new JsonResponse() { @Override protected JSONObject getJson() {
				final JSONObject json = new JSONObject();
				json.put("status", "SCENARIO_NOT_FOUND");
				return json;
			} };
		}

		scenario.setBlocks(blocks);

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject json = new JSONObject();
			json.put("status", "SUCCESS");
			return json;
		} };

	}

}
