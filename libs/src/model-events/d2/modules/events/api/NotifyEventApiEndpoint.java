package d2.modules.events.api;


import d2.modules.events.model.Event;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.Storage;
import ic.text.Charset;
import ic.throwables.AccessDenied;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import ic.throwables.WrongValue;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static ic.throwables.AccessDenied.ACCESS_DENIED;


public abstract class NotifyEventApiEndpoint<EventType extends Event> extends ProtectedHttpEndpoint {

	@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied {
		throw ACCESS_DENIED;
	}

	protected abstract EventType createEvent(@NotNull String userId, @NotNull JSONObject requestJson) throws UnableToParse;

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final EventType event = createEvent(requestJson.getString("userId"), requestJson);

		Event.getMapper(getStorage()).getId(event);

		return new JsonResponse() {
			@Override @NotNull protected JSONObject getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUCCESS");
				return responseJson;
			}
		};

	}

}
