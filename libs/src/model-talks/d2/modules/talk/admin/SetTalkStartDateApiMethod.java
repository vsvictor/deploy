package d2.modules.talk.admin;


import d2.modules.talk.model.Talk;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static d2.modules.talk.model.Talk.getTalksMapper;


public abstract class SetTalkStartDateApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "set_talk_start_date"; }

	protected abstract Storage getStorage();

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm");

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String id 				= requestJson.getString("id");
		final String startDateString 	= requestJson.getString("startDate");

		final Date startDate; try {
			startDate = DATE_FORMAT.parse(startDateString);
		} catch (ParseException e) { throw new UnableToParse(e); }

		final Talk talk = getTalksMapper(getStorage()).getItem(id);

		talk.setStartDate(startDate);

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
