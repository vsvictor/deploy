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

import static d2.modules.talk.model.Stage.getStagesMapper;
import static d2.modules.talk.model.Speaker.getSpeakersMapper;
import static ic.util.Hex.hexStringToLong;
import static ic.util.Hex.longToFixedSizeHexString;


public class Talk implements StringMapSerializable, Changeable {


	private @NotNull String name;

	public synchronized @NotNull String getName() { return name; }

	public synchronized void setName(@NotNull String name) {
		this.name = name;
		changeEvent.run();
	}


	private final @NotNull String type;

	public synchronized @NotNull String getType() { return type; }


	private @Nullable Integer placesCount;

	public synchronized @Nullable Integer getPlacesCount() { return placesCount; }

	public synchronized void setPlacesCount(@Nullable Integer placesCount) {
		this.placesCount = placesCount;
		changeEvent.run();
	}


	private @Nullable String shortDescription;

	public synchronized @Nullable String getShortDescription() { return shortDescription; }

	public synchronized void setShortDescription(@NotNull String shortDescription) {
		this.shortDescription = shortDescription;
		changeEvent.run();
	}


	private @Nullable String description;

	public synchronized @Nullable String getDescription() { return description; }

	public synchronized void setDescription(@NotNull String description) {
		this.description = description;
		changeEvent.run();
	}


	private @Nullable String imageUrl;

	public synchronized @Nullable String getImageUrl() { return imageUrl; }

	public synchronized void setImageUrl(@NotNull String imageUrl) {
		this.imageUrl = imageUrl;
		changeEvent.run();
	}


	private @Nullable Date startDate;
	private @Nullable Date endDate;

	public synchronized @Nullable Date getStartDate() 	{ return startDate; }
	public synchronized @Nullable Date getEndDate() 	{ return endDate; 	}

	public synchronized void setStartDate(@NotNull Date startDate) {
		this.startDate = startDate;
		changeEvent.run();
	}

	public synchronized void setEndDate(@NotNull Date endDate) {
		this.endDate = endDate;
		changeEvent.run();
	}


	private final EditableSet<String> speakerIds;

	public synchronized Collection<String> getSpeakerIds() { return speakerIds; }

	public synchronized Collection<Speaker> getSpeakers(@NotNull @ByIdentity final Storage storage) {
		final Mapper<Speaker, String> speakersMapper = getSpeakersMapper(storage);
		return new ConvertCollection<>(
			speakerIds,
			speakersMapper::getItem
		);
	}

	public synchronized void addSpeaker(@NotNull String speakerId) throws AlreadyExists {
		speakerIds.add(speakerId);
		changeEvent.run();
	}

	public synchronized void removeSpeaker(@NotNull String speakerId) throws NotExists {
		speakerIds.remove(speakerId);
		changeEvent.run();
	}


	private @Nullable String stageId;

	public synchronized @Nullable String getStageId() { return stageId; }

	public synchronized @Nullable Stage getStage(@NotNull @ByIdentity final Storage storage) {
		if (stageId == null) return null;
		return getStagesMapper(storage).getItem(stageId);
	}

	public void setStage(@NotNull String stageId) {
		this.stageId = stageId;
		changeEvent.run();
	}


	// StringMapSerializable

	@Override public Class getClassToDeclare() { return Talk.class; }

	@Override public void serialize(Setter1<String, String> output) {
		output.set("name", 				name);
		output.set("type",				type);
		StringMapSerializer.write(output, "placesCount", placesCount);
		output.set("shortDescription", 	shortDescription);
		output.set("description", 		description);
		output.set("imageUrl",			imageUrl);
		output.set("startDate", 		startDate == null ? null : longToFixedSizeHexString(startDate.getTime()));
		output.set("endDate", 			endDate == null ? null : longToFixedSizeHexString(endDate.getTime()));
		StringMapSerializer.write(output, "speakerIds", speakerIds);
		output.set("stageId", stageId);

	}

	@Necessary public Talk(Getter1<String, String> input) throws UnableToParse {
		this.name 				= input.getNotNull("name");
		{ final String type = input.get("type");
			if (type == null) 	this.type = "";
			else 				this.type = type;
		}
		this.placesCount = StringMapSerializer.read(input, "placesCount");
		this.shortDescription 	= input.get("shortDescription");
		this.description 		= input.get("description");
		this.imageUrl 			= input.get("imageUrl");
		{ final String startDateString = input.get("startDate");
			this.startDate = startDateString == null ? null : new Date(hexStringToLong(startDateString));
		}
		{ final String endDateString = input.get("endDate");
			this.endDate = endDateString == null ? null : new Date(hexStringToLong(endDateString));
		}
		this.speakerIds = StringMapSerializer.read(input, "speakerIds");
		this.stageId = input.get("stageId");
	}


	public Talk(@NotNull String type) {
		this.type = type;
		this.name = "";
		this.shortDescription = "";
		this.description = "";
		this.speakerIds = new EditableSet.Default<>();
	}


	public Talk(
		@NotNull String type,
		@NotNull String name,
		@NotNull String shortDescription,
		@NotNull String description,
		@Nullable Date startDate,
		@Nullable Date endDate,
		@Nullable Integer placesCount,
		@NotNull String stageId,
		@NotNull EditableSet<String> speakerIds
	) {
		this.type = type;
		this.name = name;
		this.shortDescription = shortDescription;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.placesCount = placesCount;
		this.stageId = stageId;
		this.speakerIds = speakerIds;
	}


	// Changeable implementation:

	private final Event.Trigger changeEvent = new Event.Trigger();

	@Override public Event getChangeEvent() { return changeEvent; }


	private static final EditableMap<Mapper<Talk, String>, Storage> MAPPERS = new EphemeralKeysMap<>(Weak::new);

	public static synchronized Mapper<Talk, String> getTalksMapper(@NotNull @ByIdentity final Storage storage) {
		return MAPPERS.createIfNull(
			storage,
			() -> new StorageMapper<>(storage.createFolderIfNotExists("talks")) {
				@Override protected IdGenerator<String> initIdGenerator() {
					return new SecureStringIdGenerator();
				}
			}
		);
	}


}
