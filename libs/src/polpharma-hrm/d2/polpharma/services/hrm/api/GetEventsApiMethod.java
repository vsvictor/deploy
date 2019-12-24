package d2.polpharma.services.hrm.api;


import d2.modules.events.model.Event;
import d2.modules.events.model.StatisticsEvent;
import d2.polpharma.services.PolpharmaBackendKt;
import d2.polpharma.services.hrm.model.User;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.throwables.AccessDenied;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import ic.throwables.WrongValue;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import static d2.polpharma.services.hrm.PolpharmaHrmStoragesKt.polpharmaHrmEventsStorage;
import static ic.date.DateFormat.formatDate;
import static ic.throwables.WrongValue.WRONG_VALUE;


@Deprecated
public class GetEventsApiMethod extends ProtectedHttpEndpoint {

	@Override protected String getName() { return "get_events"; }

	@Override protected void checkServerKey(String serverKey) throws WrongValue, AccessDenied { throw WRONG_VALUE; }

	@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { PolpharmaBackendKt.checkPolpharmaSuperadminAuth(auth); }

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		{ final JSONArray eventsJson = new JSONArray();

			Event.getMapper(polpharmaHrmEventsStorage).getItems().forEach(e -> {

				if (e instanceof StatisticsEvent) { final StatisticsEvent event = (StatisticsEvent) e;

					final JSONObject eventJson = new JSONObject();

					if (event.type.equals("BUTTON_MY_POLPHARMA")) 		eventJson.put("type", "Моя Польфарма"); 					else
					if (event.type.equals("EDUCATION"))					eventJson.put("type", "Прокачай себе");						else
					if (event.type.equals("WIKI"))						eventJson.put("type", "База знань");						else
					if (event.type.equals("WIKI_QUERY"))				eventJson.put("type", "Контент Wiki");						else
					if (event.type.equals("SURVEY"))					eventJson.put("type", "Опитування");						else
					if (event.type.equals("BUTTON_MY_ACHIVEMENTS"))		eventJson.put("type", "Моя Польфарма / Мої досягнення");	else
					if (event.type.equals("BUTTON_ABOUT_CAP"))			eventJson.put("type", "Моя Польфарма / Про капітана");		else
					if (event.type.equals("BUTTON_ABOUT_POLPHARMA"))	eventJson.put("type", "Моя Польфарма / Факти");				else
					if (event.type.equals("MAILING"))					eventJson.put("type", "Моя Польфарма / Розсилка");			else
					if (event.type.equals("BUTTON_CONTACT_TO_SUPPORT"))	eventJson.put("type", "Моя Польфарма / Зв'язок з офісом");
					else return;

					try {
						final User user = User.mapper.getItemOrThrow(event.userId);
						eventJson.put("user", 		user.getSurname() + " " + user.getName());
						eventJson.put("userRole",	user.getRole());
						eventJson.put("userLine",	user.getLine());
					} catch (NotExists notExists) {}

					eventJson.put("date", 		formatDate(event.date, "yyyy.MM.dd HH:mm:ss"));

					eventJson.put("content", 	event.content);

					eventsJson.put(eventJson);

				}

			});

			responseJson.put("events", eventsJson);

		}

		return new JsonResponse() {
			@Override protected @NotNull JSONObject getJson() { return responseJson; }
		};

	}

}
