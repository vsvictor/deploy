package d2.modules.talk.api;


import d2.modules.talk.model.Selection;
import ic.network.SocketAddress;
import ic.network.http.HttpEndpoint;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.CacheStorage;
import ic.struct.collection.Collection;
import ic.struct.set.EditableSet;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static ic.date.DayIndex.dateToDayIndexOrThrow;
import static ic.json.JsonArrays.toJsonArray;
import static ic.throwables.Skip.SKIP;


public abstract class GetUsersWithOnlyOneDaySelected extends HttpEndpoint {

	@Override protected String getName() { return "get_users_with_only_one_day_selected"; }

	protected abstract CacheStorage getStorage();
	protected abstract Collection<String> getUserIds();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject responseJson = new JSONObject();

		responseJson.put("users", toJsonArray(getUserIds(), userId -> {

			final EditableSet<Integer> days = new EditableSet.Default<>();

			Selection.getSelectedTalks(getStorage(), userId, "00000001.75ef2fe8d423f3f3").forEach(talk -> {
				try {
					days.addIfNotExists(dateToDayIndexOrThrow(talk.getStartDate()));
				} catch (NotExists notExists) {}
			});

			if (days.getCount() != 1) throw SKIP;

			return userId;

		}));

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
