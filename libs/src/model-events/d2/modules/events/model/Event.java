package d2.modules.events.model;


import ic.id.IdGenerator;
import ic.id.Mapper;
import ic.id.SimpleStringIdGenerator;
import ic.id.StorageMapper;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.json.JsonSerializable;
import ic.serial.stringmap.StringMapSerializable;
import ic.storage.DatabaseStorage;
import ic.storage.Storage;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.map.EditableMap;
import ic.struct.map.UntypedCountableMap;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Date;

import static ic.date.Millis.dateToMillis;
import static ic.date.Millis.millisToDate;
import static ic.util.Hex.*;
import static java.lang.System.currentTimeMillis;


public abstract class Event implements StringMapSerializable, JsonSerializable {


	public final @NotNull Date date;

	public final @NotNull String userId;


	public abstract void toJson(JSONObject eventJson);


	// StringMapSerializable implementation:

	@Override public void serialize(Setter1<String, String> output) {
		output.set("date", longToFixedSizeHexString(dateToMillis(date)));
		output.set("userId", userId);
	}

	public Event(Getter1<String, String> input) {
		this.date = millisToDate(hexStringToLong(input.getNotNull("date")));
		this.userId = input.get("userId", () -> "");
	}


	// JsonSerializable implementation:

	@Override public void serialize(JSONObject json) {
		json.put("date", dateToMillis(date));
		json.put("userId", userId);
	}

	public Event(JSONObject json) {
		this.date = millisToDate(json.getLong("date"));
		this.userId = json.optString("userId", "");
	}


	@Deprecated public Event() {
		this.date = new Date(currentTimeMillis());
		this.userId = "";
	}

	public Event(@NotNull String userId) {
		this.date = new Date(currentTimeMillis());
		this.userId = userId;
	}


	private static final EditableMap<EditableMap<StorageMapper<Event>, String>, Storage> EVENTS_MAPPERS_BY_STORAGE_AND_USER_ID = new EditableMap.Default<>();

	@Deprecated public static Mapper<Event, String> getMapper(Storage storage, String userId) {
		assert storage != null;
		assert userId != null;
		return EVENTS_MAPPERS_BY_STORAGE_AND_USER_ID.createIfNull(
			storage,
			EditableMap.Default::new
		).createIfNull(
			userId,
			() -> new StorageMapper<>(
				storage.createFolderIfNotExists(userId)
			) { @Override protected IdGenerator<String> initIdGenerator() {
				return new SimpleStringIdGenerator();
			} }
		);
	}


	private static final EditableMap<StorageMapper<Event>, Storage> EVENTS_MAPPERS_BY_STORAGE = new EditableMap.Default<>();

	public static Mapper<Event, String> getMapper(@NotNull Storage storage) {
		return EVENTS_MAPPERS_BY_STORAGE.createIfNull(
			storage,
			() -> new StorageMapper<>(storage) { @Override protected IdGenerator<String> initIdGenerator() {
				return new SimpleStringIdGenerator();
			} }
		);
	}

	public static Collection<Event> byUser(@NotNull DatabaseStorage storage, String userId) {
		return new ConvertCollection<Event, Pair<String, Event>>(
			storage.select(new UntypedCountableMap.Default<>(
				"userId", userId
			)),
			result -> result.getSecond()
		);
	}


}
