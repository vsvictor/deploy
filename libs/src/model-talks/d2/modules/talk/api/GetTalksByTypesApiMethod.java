package d2.modules.talk.api;


import d2.modules.talk.model.Speaker;
import d2.modules.talk.model.Stage;
import d2.modules.talk.model.Talk;
import d2.modules.talk.model.TalkEvent;
import ic.id.Mapper;

import static d2.modules.talk.model.Selection.*;
import static ic.date.DayIndex.dateToDayIndex;
import static ic.date.Millis.nullableDateToMillis;

import ic.interfaces.getter.Getter;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.CacheStorage;
import ic.struct.list.EditableList;
import ic.struct.map.CountableMap;
import ic.struct.map.EditableMap;
import ic.struct.order.ConvertOrder;
import ic.struct.order.OrderedCountableSet;
import ic.struct.set.CountableSet;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Date;

import static d2.modules.talk.model.Speaker.getSpeakersMapper;
import static d2.modules.talk.model.Stage.getStagesMapper;
import static d2.modules.talk.model.Talk.getTalksMapper;
import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;
import static ic.date.DateFormat.parseNullableDate;
import static ic.date.DayIndex.*;
import static ic.json.JsonArrays.toJsonArray;
import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;
import static ic.util.Hex.longToFixedSizeHexString;


public abstract class GetTalksByTypesApiMethod extends HttpEndpoint {

	protected abstract CacheStorage getStorage();

	@Override protected String getName() { return "get_talks_by_types"; }

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final CacheStorage storage = getStorage();

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String userId 		= requestJson.getString("userId");
		final String talkEventId 	= requestJson.getString("talkEventId");

		final @Nullable Integer day = nullableDateToDayIndex(parseNullableDate(requestJson.optString("date"), "yyyy.MM.dd"));

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
		}

		final TalkEvent talkEvent = getTalkEventsMapper(storage).getItem(talkEventId);

		final Mapper<Stage, String> stagesMapper 		= getStagesMapper(storage);
		final Mapper<Talk, String> talksMapper 			= getTalksMapper(storage);
		final Mapper<Speaker, String> speakersMapper 	= getSpeakersMapper(storage);

		final CountableSet<String> selectedTalkIds = Companion.getSelectedTalkIds(storage, userId, talkEventId);

		final JSONObject talksByTypesJson = new JSONObject();

		final EditableMap<EditableList<Talk>, String> talksByTypes = new EditableMap.Default<>();

		talkEvent.getTalks(storage).forEach(talk -> {

			final @Nullable Date startDate 	= talk.getStartDate();
			final @Nullable Date endDate 	= talk.getEndDate();

			if (day != null) {

				if (startDate == null) 	return;
				if (endDate == null) 	return;

				if (day < dateToDayIndex(startDate)) 	return;
				if (day > dateToDayIndex(endDate))		return;

			}

			talksByTypes.createIfNull(talk.getType(), () -> new EditableList.Default<>()).add(talk);

		});

		talksByTypes.getKeys().forEach(talkType -> talksByTypesJson.put(
			talkType,
			toJsonArray(
				new OrderedCountableSet.Default<Talk>(
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
					talksByTypes.getNotNull(talkType)
				),
				talk -> {

					final String talkId = talksMapper.getId(talk);

					final JSONObject talkJson = new JSONObject();

					talkJson.put("id", 					talkId);
					talkJson.put("name", 				talk.getName());
					talkJson.put("shortDescription", 	talk.getShortDescription());
					talkJson.put("description",			talk.getDescription());
					talkJson.put("imageUrl",			talk.getImageUrl());

					talkJson.put("startDate", 	nullableDateToMillis(talk.getStartDate()));
					talkJson.put("endDate", 	nullableDateToMillis(talk.getEndDate()));

					talkJson.put("speakers", toJsonArray(
						talk.getSpeakerIds(),
						speakerId -> {

							final Speaker speaker = speakersMapper.getItem(speakerId);

							final JSONObject speakerJson = new JSONObject();

							speakerJson.put("id", 				speakerId);
							speakerJson.put("name", 			speaker.getName() + " " + speaker.getSurname());
							speakerJson.put("shortDescription", speaker.getShortDescription());
							speakerJson.put("description",		speaker.getDescription());
							speakerJson.put("imageUrl",			speaker.getImageUrl());

							return speakerJson;

						}
					));

					{ final JSONObject stageJson = new JSONObject();
						final String stageId = talk.getStageId();
						final Stage stage = stagesMapper.getItem(stageId);
						stageJson.put("id", 			stageId);
						stageJson.put("name", 			stage.getName());
						stageJson.put("coverImageUrl", 	stage.getCoverImageUrl());
						stageJson.put("planImageUrl",	stage.getPlanImageUrl());
						talkJson.put("stage", stageJson);
					}

					talkJson.put("availableToSelect", 	Companion.isTalkAvailableToSelect(storage, talkEventId, talkId));
					talkJson.put("selected", 			selectedTalkIds.contains(talkId));

					return talkJson;

				}
			)
		));

		responseJson.put("talksByTypes", talksByTypesJson);

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
			@Override public CountableMap<String, String> getHeaders() { return new CountableMap.Default<String, String>(
				"Access-Control-Allow-Origin", 	"*",
				"Access-Control-Allow-Methods", "*",
				"Access-Control-Allow-Headers", "*"
			); }
		};

	}

}
