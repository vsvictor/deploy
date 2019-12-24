package d2.modules.talk.api;


import d2.modules.talk.model.Selection;
import d2.modules.talk.model.Speaker;
import d2.modules.talk.model.Stage;
import d2.modules.talk.model.Talk;
import ic.id.Mapper;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.CacheStorage;
import ic.struct.collection.FilterCollection;
import ic.struct.order.ConvertOrder;
import ic.struct.order.OrderedEditableSet;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Date;

import static d2.modules.talk.model.Speaker.getSpeakersMapper;
import static d2.modules.talk.model.Stage.getStagesMapper;
import static d2.modules.talk.model.Talk.getTalksMapper;
import static ic.date.DateFormat.parseNullableDate;
import static ic.date.DayIndex.nullableDateToDayIndex;
import static ic.date.Millis.nullableDateToMillis;
import static ic.json.JsonArrays.toJsonArray;
import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;
import static ic.util.Hex.longToFixedSizeHexString;
import java.util.Objects;


public abstract class GetSelectedTalksApiMethod extends HttpEndpoint {

	@Override protected String getName() { return "get_selected_talks"; }

	protected abstract CacheStorage getStorage();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final CacheStorage storage = getStorage();

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String userId 		= requestJson.getString("userId");
		final String talkEventId 	= requestJson.getString("talkEventId");

		final @Nullable Integer day = nullableDateToDayIndex(parseNullableDate(requestJson.optString("date"), "yyyy.MM.dd"));

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
		}

		final Mapper<Talk, String> talksMapper = getTalksMapper(storage);
		final Mapper<Speaker, String> speakersMapper = getSpeakersMapper(storage);
		final Mapper<Stage, String> stagesMapper = getStagesMapper(storage);

		final OrderedEditableSet<Talk> selectedTalks = new OrderedEditableSet.Default<Talk>(
			new ConvertOrder<>(
				ALPHABETIC_STRING_ORDER,
				talk -> {
					final long dateMillis; {
						final Date startDate = talk.getStartDate();
						if (startDate == null) 	dateMillis = Long.MAX_VALUE;
						else					dateMillis = startDate.getTime();
					}
					return longToFixedSizeHexString(dateMillis) + " " + talksMapper.getId(talk);
				}
			),
			new FilterCollection<Talk>(
				Selection.getSelectedTalks(storage, userId, talkEventId),
				talk -> {
					if (day != null) {
						if (!Objects.equals(
							nullableDateToDayIndex(talk.getStartDate()),
							day
						)) return false;
					}
					return true;
				}
			)
		);

		responseJson.put("selectedTalks", toJsonArray(selectedTalks, talk -> {
			final JSONObject talkJson = new JSONObject();
			talkJson.put("id", talksMapper.getId(talk));
			talkJson.put("name", talk.getName());
			talkJson.put("type", talk.getType());
			talkJson.put("placesCount", talk.getPlacesCount());
			talkJson.put("shortDescription", talk.getShortDescription());
			talkJson.put("description", talk.getDescription());
			talkJson.put("imageUrl", talk.getImageUrl());
			talkJson.put("startDate", nullableDateToMillis(talk.getStartDate()));
			talkJson.put("endDate", nullableDateToMillis(talk.getEndDate()));
			talkJson.put("speakers", toJsonArray(talk.getSpeakers(storage), speaker -> {
				final JSONObject speakerJson = new JSONObject();
				speakerJson.put("id", speakersMapper.getId(speaker));
				speakerJson.put("name", 	speaker.getName());
				speakerJson.put("surname", 	speaker.getSurname());
				return speakerJson;
			}));
			{ final String stageId = talk.getStageId();
				final Stage stage = stagesMapper.getItem(stageId);
				final JSONObject stageJson = new JSONObject();
				stageJson.put("id", stageId);
				stageJson.put("name", stage.getName());
				talkJson.put("stage", stageJson);
			}
			return talkJson;
		}));

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}
