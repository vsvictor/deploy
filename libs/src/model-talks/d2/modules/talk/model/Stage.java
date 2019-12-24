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
import ic.storage.Storage;
import ic.struct.map.EditableMap;
import ic.struct.map.EphemeralKeysMap;
import ic.struct.variable.Weak;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Stage implements StringMapSerializable, Changeable {


	private @NotNull String name;

	public synchronized @NotNull String getName() { return name; }

	public synchronized void setName(@NotNull String name) {
		this.name = name;
		changeEventTrigger.run();
	}


	private @Nullable String coverImageUrl;

	public synchronized @Nullable String getCoverImageUrl() { return coverImageUrl; }

	public synchronized void setCoverImageUrl(@NotNull String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
		changeEventTrigger.run();
	}


	private @Nullable String planImageUrl;

	public synchronized @Nullable String getPlanImageUrl() { return planImageUrl; }

	public synchronized void setPlanImageUrl(@NotNull String planImageUrl) {
		this.planImageUrl = planImageUrl;
		changeEventTrigger.run();
	}


	// StringMapSerializable implementation:

	@Override public Class getClassToDeclare() { return Stage.class; }

	@Override public void serialize(Setter1<String, String> output) {
		output.set("name", 			name);
		output.set("coverImageUrl", coverImageUrl);
		output.set("planImageUrl", 	planImageUrl);
	}

	@Necessary public Stage(Getter1<String, String> input) {
		this.name 			= input.getNotNull("name");
		this.coverImageUrl 	= input.get("coverImageUrl");
		this.planImageUrl 	= input.get("planImageUrl");
	}


	// Changeable implementation:

	private final Event.Trigger changeEventTrigger = new Event.Trigger();

	@Override public Event getChangeEvent() { return changeEventTrigger; }


	// Constructors:

	public Stage(@NotNull String name) {
		this.name = name;
	}


	// Static:

	private static final EditableMap<Mapper<Stage, String>, Storage> MAPPERS = new EphemeralKeysMap<>(
		Weak::new
	);

	public static synchronized Mapper<Stage, String> getStagesMapper(@NotNull @ByIdentity final Storage storage) {
		return MAPPERS.createIfNull(
			storage,
			() -> new StorageMapper<>(
				storage.createFolderIfNotExists("stages")
			) {
				@Override protected IdGenerator<String> initIdGenerator() {
					return new SecureStringIdGenerator();
				}
			}
		);
	}


}
