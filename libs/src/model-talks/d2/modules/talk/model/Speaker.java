package d2.modules.talk.model;


import ic.annotations.ByIdentity;
import ic.annotations.Necessary;
import ic.event.Event;
import ic.id.IdGenerator;
import ic.id.Mapper;
import ic.id.SecureStringIdGenerator;
import ic.id.StorageMapper;
import ic.interfaces.changeable.Changeable;
import ic.interfaces.getter.Getter;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.stringmap.StringMapSerializable;
import ic.storage.Storage;
import ic.struct.map.EditableMap;
import ic.struct.map.EphemeralKeysMap;
import ic.struct.variable.Ephemeral;
import ic.struct.variable.Variable;
import ic.struct.variable.Weak;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Boolean.parseBoolean;


public class Speaker implements StringMapSerializable, Changeable {


	private @NotNull String name;

	public synchronized @NotNull String getName() { return name; }

	public synchronized void setName(@NotNull String name) {
		this.name = name;
		changeEventTrigger.run();
	}


	private @NotNull String surname;

	public synchronized @NotNull String getSurname() { return surname; }

	public synchronized void setSurname(@NotNull String surname) {
		this.surname = surname;
		changeEventTrigger.run();
	}


	private @Nullable String shortDescription;

	public synchronized @Nullable String getShortDescription() { return shortDescription; }

	public synchronized void setShortDescription(@NotNull String shortDescription) {
		this.shortDescription = shortDescription;
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


	private boolean showInList;

	public synchronized boolean toShowInList() { return showInList; }

	public synchronized void setShowInList(boolean showInList) {
		this.showInList = showInList;
		changeEventTrigger.run();
	}


	@Override public Class getClassToDeclare() { return Speaker.class; }

	@Override public void serialize(Setter1<String, String> output) {
		output.set("name", 				name);
		output.set("surname", 			surname);
		output.set("shortDescription", 	shortDescription);
		output.set("description", 		description);
		output.set("imageUrl",			imageUrl);
		output.set("showInList", 		Boolean.toString(showInList));
	}

	@Necessary public Speaker(Getter1<String, String> input) {
		this.name 				= input.getNotNull("name");
		this.surname 			= input.getNotNull("surname");
		this.shortDescription 	= input.get("shortDescription");
		this.description 		= input.get("description");
		this.imageUrl 			= input.get("imageUrl");
		this.showInList			= parseBoolean(input.get("showInList", new Variable.Constant<String>("true")));
	}


	public Speaker() {
		this.name = "";
		this.surname = "";
		this.shortDescription = "";
		this.description = "";
		this.showInList = false;
	}

	public Speaker(@NotNull String name, @NotNull String surname) {
		this.name = name;
		this.surname = surname;
		this.shortDescription = "";
		this.description = "";
		this.showInList = false;
	}



	// Changeable implementation:

	private final Event.Trigger changeEventTrigger = new Event.Trigger();

	@Override public Event getChangeEvent() { return changeEventTrigger; }


	private static final EditableMap<Mapper<Speaker, String>, Storage> MAPPERS = new EphemeralKeysMap<Mapper<Speaker, String>, Storage>(
		Weak::new
	);

	public static synchronized Mapper<Speaker, String> getSpeakersMapper(@NotNull @ByIdentity final Storage storage) {
		return MAPPERS.createIfNull(
			storage,
			() -> new StorageMapper<Speaker>(
				storage.createFolderIfNotExists("speakers")
			) {
				@Override protected IdGenerator<String> initIdGenerator() {
					return new SecureStringIdGenerator();
				}
			}
		);
	}


}
