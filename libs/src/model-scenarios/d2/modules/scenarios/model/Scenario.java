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
import ic.storage.CacheStorage;
import ic.struct.list.List;
import ic.struct.map.EditableMap;
import ic.struct.set.CountableSet;
import ic.throwables.Empty;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import static ic.throwables.Empty.EMPTY;


public class Scenario implements StringMapSerializable, JsonSerializable, Changeable {


	private String title;

	public synchronized String getTitle() { return title; }

	public synchronized void setTitle(String title) {
		this.title = title;
		changeEventTrigger.run();
	}



	private List<Block> blocks;

	public synchronized List<Block> getBlocks() throws Empty {
		if (blocks == null) throw EMPTY;
		return blocks;
	}

	public synchronized void setBlocks(@NotNull List<Block> blocks) {
		this.blocks = blocks;
		changeEventTrigger.run();
	}

	public synchronized Block getBlockByIdOrThrow(@NotNull final String blockId) throws NotExists {
		return blocks.findOrThrow(block -> block.id.equals(blockId));
	}

	public synchronized Block getBlockById(@NotNull final String blockId) {
		try {
			return getBlockByIdOrThrow(blockId);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}


	private CountableSet<String> roles;

	public CountableSet<String> getRoles() { return roles; }

	public synchronized void setRoles(CountableSet<String> roles) {
		this.roles = roles;
		changeEventTrigger.run();
	}


	// Serializable implementation:

	@Override public Class<?> getClassToDeclare() { return Scenario.class; }


	// JsonSerializable implementation:

	@Override public void serialize(JSONObject json) throws JSONException {
		json.putOpt("title", title);
		json.put("blocks", JsonSerializer.serialize(blocks, true));
		json.put("roles", JsonSerializer.serialize(roles, true));
	}

	@Necessary public Scenario(JSONObject json) throws UnableToParse {
		this.title 	= json.optString("title", null);
		if (json.has("blocks")) {
			this.blocks = JsonSerializer.parse(json.get("blocks"));
		} else {
			this.blocks = new List.Default<>(
				new Block("1", "TEXT", "", "NONE", new List.Default<>()),
				new Block("FINISH 1", "FINISH", "", "FINISH", new List.Default<>())
			);
		}
		this.roles 	= JsonSerializer.parse(json.get("roles"));
	}


	// StringMapSerializable implementation:

	@Override public synchronized void serialize(Setter1<String, String> output) {
		output.set("title", title);
		StringMapSerializer.write(output, "blocks", blocks);
		StringMapSerializer.write(output, "roles", roles);
	}

	@Necessary public Scenario(Getter1<String, String> input) throws UnableToParse {
		this.title 	= input.get("title");
		this.blocks = StringMapSerializer.read(input, "blocks");
		this.roles 	= StringMapSerializer.read(input, "roles");
	}


	// Changeable implementation:

	private final Event.Trigger changeEventTrigger = new Event.Trigger();

	@Override public Event getChangeEvent() { return changeEventTrigger; }


	// Constructor:

	public Scenario(@NotNull String title) {
		this.title = title;
		this.blocks = new List.Default<>(
			new Block("1", "TEXT", "", "NONE", new List.Default<>()),
			new Block("FINISH 1", "FINISH", "", "FINISH", new List.Default<>())
		);
		this.roles = new CountableSet.Default<>();
	}


	// Static:

	private static final EditableMap<
		EditableMap<
			Mapper<Scenario, String>,
			String
		>,
		CacheStorage
	> SCENARIOS_IDENTITY_MAP_BY_STORAGE_AND_SUBJECT_ID = new EditableMap.Default<>();

	public static Mapper<Scenario, String> getScenariosMapper(@NotNull final BindingStorage storage, @NotNull final String subjectId) {
		assert storage != null;
		assert subjectId != null;
		return SCENARIOS_IDENTITY_MAP_BY_STORAGE_AND_SUBJECT_ID.createIfNull(
			storage,
			EditableMap.Default::new
		).createIfNull(
			subjectId,
			() -> new StorageMapper<>(
				storage.createFolderIfNotExists("scenarios").createFolderIfNotExists(subjectId)
			) {
				@Override protected IdGenerator<String> initIdGenerator() { return new SimpleStringIdGenerator(); }
			}
		);
	}

	public static void removeAllScenarios(@NotNull final BindingStorage storage, @NotNull final String subjectId) {
		getScenariosMapper(storage, subjectId).empty();
	}


}
