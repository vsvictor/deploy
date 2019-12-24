package d2.modules.talk.api;


import d2.modules.talk.model.Selection;
import ic.interfaces.getter.Safe3Getter1;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.CacheStorage;
import ic.struct.set.CountableSet;
import ic.text.Charset;
import ic.throwables.OutOfLimit;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.json.JSONException;
import org.json.JSONObject;

import static d2.modules.talk.model.Selection.setSelectedTalks;
import static ic.date.DateFormat.parseNullableDate;
import static ic.date.DayIndex.nullableDateToDayIndex;
import static ic.json.JsonArrays.jsonArrayToList;


public abstract class SetSelectedTalksApiMethod extends HttpEndpoint {


	protected abstract CacheStorage getStorage();

	protected abstract void onCalled(String userId, String talkEventId);


	@Override protected String getName() { return "set_selected_talks"; }

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String userId 		= requestJson.getString("userId");
		final String talkEventId 	= requestJson.getString("talkEventId");
		final Integer dayIndex		= nullableDateToDayIndex(parseNullableDate(requestJson.optString("date"), "yyyy.MM.dd"));

		try {

			Selection.setSelectedTalks(
				getStorage(),
				userId,
				talkEventId,
				dayIndex,
				new CountableSet.Default<String>(
					jsonArrayToList(
						requestJson.getJSONArray("selectedTalks"),
						(Safe3Getter1<String, String, JSONException, UnableToParse, Skip>) talkId -> talkId
					)
				)
			);

			onCalled(userId, talkEventId);

		} catch (OutOfLimit outOfLimit) {

			return new JsonResponse() { @Override protected JSONObject getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "OUT_OF_LIMIT");
				return responseJson;
			} };

		}

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject responseJson = new JSONObject();
			responseJson.put("status", "SUCCESS");
			return responseJson;
		} };

	}


}
