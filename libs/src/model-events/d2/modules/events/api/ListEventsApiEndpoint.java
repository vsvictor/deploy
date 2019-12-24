package d2.modules.events.api;


import d2.modules.events.model.Event;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.network.http.ProtectedHttpEndpoint;
import ic.storage.Storage;
import ic.struct.set.CountableSet;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import static ic.date.Millis.dateToMillis;
import static ic.json.JsonArrays.toJsonArray;


public abstract class ListEventsApiEndpoint extends ProtectedHttpEndpoint {

	protected abstract Storage getStorage();

	protected abstract CountableSet<String> getUserIds();

	@Nullable @Override protected String getName() { return "list"; }

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final Storage storage = getStorage();

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		responseJson.put("events", toJsonArray(

			Event.getMapper(storage).getItems(),

			event -> {
				JSONObject eventJson = new JSONObject();
				eventJson.put("userId", event.userId);
				eventJson.put("date", dateToMillis(event.date));
				event.toJson(eventJson);
				return eventJson;
			}

		));

		return new JsonResponse() {
			@Override @NotNull protected Object getJson() { return responseJson; }
		};

	}

}
