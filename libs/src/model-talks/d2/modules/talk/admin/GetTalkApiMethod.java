package d2.modules.talk.admin;


import d2.modules.talk.model.Speaker;
import d2.modules.talk.model.Stage;
import d2.modules.talk.model.Talk;
import ic.id.Mapper;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static d2.modules.talk.model.Speaker.getSpeakersMapper;
import static d2.modules.talk.model.Talk.getTalksMapper;
import static ic.json.JsonArrays.toJsonArray;


public abstract class GetTalkApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "get_talk"; }

	protected abstract Storage getStorage();

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm");

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final Storage storage = getStorage();

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String id = requestJson.getString("id");

		final Talk talk = getTalksMapper(storage).getItem(id);

		final JSONObject responseJson = new JSONObject();

		responseJson.put("name", 				talk.getName());
		responseJson.put("shortDescription", 	talk.getShortDescription());
		responseJson.put("description", 		talk.getDescription());
		responseJson.put("imageUrl", 			talk.getImageUrl());
		responseJson.put("stageId", 			talk.getStageId());
		responseJson.put("placesCount",			talk.getPlacesCount());

		{ final Date startDate = talk.getStartDate();
			if (startDate != null) {
				responseJson.put("startDate", DATE_FORMAT.format(startDate));
			}
		}

		{ final Date endDate = talk.getEndDate();
			if (endDate != null) {
				responseJson.put("endDate", DATE_FORMAT.format(endDate));
			}
		}

		final Mapper<Speaker, String> speakersMapper = getSpeakersMapper(storage);

		responseJson.put("speakers", toJsonArray(
			talk.getSpeakerIds(),
			speakerId -> {
				final Speaker speaker = speakersMapper.getItem(speakerId);
				final JSONObject speakerJson = new JSONObject();
				speakerJson.put("id", speakerId);
				speakerJson.put("name", speaker.getName());
				speakerJson.put("surname", speaker.getSurname());
				return speakerJson;
			}
		));

		{
			final @Nullable Stage stage = talk.getStage(storage);
			if (stage != null) {
				final JSONObject stageJson = new JSONObject();
				stageJson.put("name", stage.getName());
				responseJson.put("stage", stageJson);
			}
		}

		responseJson.put("status", "SUCCESS");

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
