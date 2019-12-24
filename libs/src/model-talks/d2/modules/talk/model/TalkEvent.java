package d2.modules.talk.model;


import ic.annotations.ByIdentity;
import ic.annotations.Necessary;
import ic.event.Event;
import ic.id.IdGenerator;
import ic.id.Mapper;
import ic.id.SecureStringIdGenerator;
import ic.id.StorageMapper;
import ic.interfaces.changeable.Changeable;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.stringmap.StringMapSerializable;
import ic.serial.stringmap.StringMapSerializer;
import ic.storage.Storage;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.map.EditableMap;
import ic.struct.map.EphemeralKeysMap;
import ic.struct.set.EditableSet;
import ic.struct.variable.Weak;
import ic.throwables.AlreadyExists;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

import static d2.modules.talk.model.Speaker.getSpeakersMapper;
import static d2.modules.talk.model.Talk.getTalksMapper;
import static ic.util.Hex.hexStringToLong;
import static ic.util.Hex.longToFixedSizeHexString;


public class TalkEvent implements StringMapSerializable, Changeable {


	private @NotNull String title;

	public synchronized @NotNull String getTitle() { return title; }

	public synchronized void setTitle(@NotNull String title) {
		this.title = title;
		changeEventTrigger.run();
	}


	private @Nullable String description;

	public synchronized @Nullable String getDescription() { return description; }

	public synchronized void setDescription(@NotNull String description) {
		this.description = description;
		changeEventTrigger.run();
	}


	private @Nullable String imageUrl;

	public synchronized @Nullable String getImageUrl() { return imageUrl; }

	public synchronized void setImageUrl(@NotNull String imageUrl) {
		this.imageUrl = imageUrl;
		changeEventTrigger.run();
	}


	private @Nullable Date startDate;
	private @Nullable Date endDate;

	public synchronized @Nullable Date getStartDate() 	{ return startDate; }
	public synchronized @Nullable Date getEndDate() 	{ return endDate; 	}

	public synchronized void setStartDate(@NotNull Date startDate) {
		this.startDate = startDate;
		changeEventTrigger.run();
	}

	public synchronized void setEndDate(@NotNull Date endDate) {
		this.endDate = endDate;
		changeEventTrigger.run();
	}


	private final EditableSet<String> talkIds;

	public synchronized Collection<String> getTalkIds() {
		return talkIds;
	}

	public synchronized Collection<Talk> getTalks(@NotNull @ByIdentity final Storage storage) {
		return new ConvertCollection<>(
			talkIds,
			talkId -> getTalksMapper(storage).getItem(talkId)
		);
	}

	public synchronized void addTalk(@NotNull String talkId) throws AlreadyExists {
		talkIds.add(talkId);
		changeEventTrigger.run();
	}

	public synchronized void removeTalk(@NotNull String talkId) throws NotExists {
		talkIds.remove(talkId);
		changeEventTrigger.run();
	}

	public synchronized void clearTalks() {
		talkIds.empty();
		changeEventTrigger.run();
	}


	private final EditableSet<String> stageIds;

	public synchronized Collection<String> getStageIds() {
		return stageIds;
	}

	public synchronized void addStage(@NotNull String stageId) throws AlreadyExists {
		stageIds.add(stageId);
		changeEventTrigger.run();
	}

	public synchronized void removeStage(@NotNull String stageId) throws NotExists {
		stageIds.remove(stageId);
		changeEventTrigger.run();
	}


	private final EditableSet<String> speakerIds;

	public synchronized Collection<String> getSpeakerIds() {
		return speakerIds;
	}

	public synchronized Collection<Speaker> getSpeakers(@NotNull @ByIdentity final Storage storage) {
		return new ConvertCollection<>(
			speakerIds,
			speakerId -> getSpeakersMapper(storage).getItem(speakerId)
		);
	}

	public synchronized void addSpeaker(@NotNull String speakerId) throws AlreadyExists {
		speakerIds.add(speakerId);
		changeEventTrigger.run();
	}

	public synchronized void removeSpeaker(@NotNull String speakerId) throws NotExists {
		speakerIds.remove(speakerId);
		changeEventTrigger.run();
	}

	public synchronized void clearSpeakers() {
		speakerIds.empty();
		changeEventTrigger.run();
	}


	// StringMapSerializable

	@Override public Class getClassToDeclare() { return TalkEvent.class; }

	@Override public void serialize(Setter1<String, String> output) {
		output.set("title", title);
		output.set("description", 	description);
		output.set("imageUrl",		imageUrl);
		output.set("startDate", 	startDate == null ? null : longToFixedSizeHexString(startDate.getTime()));
		output.set("endDate", 		endDate == null ? null : longToFixedSizeHexString(endDate.getTime()));
		StringMapSerializer.write(output, "talkIds", talkIds);
		StringMapSerializer.write(output, "stageIds", stageIds);
		StringMapSerializer.write(output, "speakerIds", speakerIds);

	}

	@Necessary public TalkEvent(Getter1<String, String> input) throws UnableToParse {
		this.title 			= input.getNotNull("title");
		this.description 	= input.get("description");
		this.imageUrl 		= input.get("imageUrl");
		{ final String startDateString = input.get("startDate");
			this.startDate = startDateString == null ? null : new Date(hexStringToLong(startDateString));
		}
		{ final String endDateString = input.get("endDate");
			this.endDate = endDateString == null ? null : new Date(hexStringToLong(endDateString));
		}
		this.talkIds = StringMapSerializer.read(input, "talkIds");
		this.stageIds = StringMapSerializer.read(input, "stageIds");
		this.speakerIds = StringMapSerializer.read(input, "speakerIds");
	}


	public TalkEvent(@NotNull @ByIdentity Storage storage, @NotNull String title) {
		this.title 			= title;
		this.description 	= "";
		this.talkIds = new EditableSet.Default<>();
		this.stageIds = new EditableSet.Default<>();
		this.speakerIds = new EditableSet.Default<>();
		getTalkEventsMapper(storage).getId(this);
	}


	// Changeable implementation:

	private final Event.Trigger changeEventTrigger = new Event.Trigger();

	@Override public Event getChangeEvent() { return changeEventTrigger; }


	private static final EditableMap<Mapper<TalkEvent, String>, Storage> MAPPERS = new EphemeralKeysMap<>(Weak::new);

	public static synchronized Mapper<TalkEvent, String> getTalkEventsMapper(@NotNull @ByIdentity final Storage storage) {
		return MAPPERS.createIfNull(
			storage,
			() -> new StorageMapper<>(
				storage.createFolderIfNotExists("talkEvents")
			) {
				@Override protected IdGenerator<String> initIdGenerator() {
					return new SecureStringIdGenerator();
				}
			}
		);
	}


}
