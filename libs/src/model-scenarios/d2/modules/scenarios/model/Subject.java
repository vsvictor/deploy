package d2.modules.scenarios.model;


import ic.annotations.Necessary;
import ic.event.Event;
import ic.id.IdGenerator;
import ic.id.Mapper;
import ic.id.SimpleStringIdGenerator;
import ic.id.StorageMapper;
import ic.interfaces.changeable.Changeable;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.json.JsonSerializable;
import ic.serial.json.JsonSerializer;
import ic.serial.stringmap.StringMapSerializable;
import ic.serial.stringmap.StringMapSerializer;
import ic.storage.BindingStorage;
import ic.struct.map.EditableMap;
import ic.struct.set.CountableSet;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;


public class Subject implements StringMapSerializable, JsonSerializable, Changeable {


	private String title;

	public String getTitle() { return title; }

	public synchronized void setTitle(String title) {
		this.title = title;
		changeEventTrigger.run();
	}


	private CountableSet<String> roles;

	public CountableSet<String> getRoles() { return roles; }

	public synchronized void setRoles(CountableSet<String> roles) {
		this.roles = roles;
		changeEventTrigger.run();
	}


	private final Event.Trigger changeEventTrigger = new Event.Trigger();

	@Override public Event getChangeEvent() { return changeEventTrigger; }


	// Serializable implementation:

	@Override public Class<?> getClassToDeclare() { return Subject.class; }


	// JsonSerializable implementation:

	@Override public void serialize(JSONObject json) throws JSONException {
		json.putOpt("title", title);
		json.put("roles", JsonSerializer.serialize(roles, true));
	}

	@Necessary public Subject(JSONObject json) throws UnableToParse {
		this.title = json.optString("title", null);
		this.roles = JsonSerializer.parse(json.get("roles"));
	}


	// StringMapSerializable implementation:

	@Override public synchronized void serialize(Setter1<String, String> output) {
		output.set("title", title);
		StringMapSerializer.write(output, "roles", roles);
	}

	@Necessary public Subject(Getter1<String, String> input) throws UnableToParse {
		this.title = input.get("title");
		this.roles = StringMapSerializer.read(input, "roles");
	}


	// Constructor:

	public Subject(@NotNull String title) {
		this.title = title;
		this.roles = new CountableSet.Default<>();
	}


	// Topics by name:

	private static final EditableMap<Mapper<Subject, String>, BindingStorage> SUBJECTS_MAPPERS_BY_STORAGE = new EditableMap.Default<>();

	public static Mapper<Subject, String> getSubjectsMapper(@NotNull final BindingStorage storage) {
		return SUBJECTS_MAPPERS_BY_STORAGE.createIfNull(storage, () -> new StorageMapper<>(
			storage.createFolderIfNotExists("subjects")
		) { @Override protected IdGenerator<String> initIdGenerator() {
			return new SimpleStringIdGenerator();
		} });
	}


}
