package d2.polpharma.services.db.model;


import ic.annotations.Necessary;
import ic.id.IdGenerator;
import ic.id.Mapper;
import ic.id.SimpleStringIdGenerator;
import ic.id.StorageMapper;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.stringmap.StringMapSerializable;
import ic.storage.CacheStorage;
import ic.storage.Storage;
import ic.struct.collection.Collection;
import ic.struct.collection.FilterCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

import static d2.polpharma.services.db.PolpharmaRedisDataBase.POLPHARMA_REDIS_DATABASE;
import static ic.date.DateFormat.formatDate;


@Deprecated
public class Event implements StringMapSerializable {


	public static final String TYPE_WIKI = "WIKI";

	public final String type;

	public final @Nullable String content;

	public final Date date;


	@Override public Class getClassToDeclare() { return Event.class; }

	@Override public void serialize(Setter1<String, String> output) {
		output.set("type", 		type);
		output.set("content", 	content);
		output.set("date", Long.toString(date.getTime()));
	}

	@Necessary public Event(Getter1<String, String> input) {
		this.type = input.get("type");
		this.date = new Date(Long.parseLong(input.get("date")));
		this.content = input.get("content");
	}


	public Event(@NotNull String type, @Nullable String content) {
		this.type = type;
		this.date = new Date(System.currentTimeMillis());
		this.content = content;
	}

	public Event(@NotNull String type, @Nullable String content, @NotNull Date date) {
		this.type = type;
		this.date = date;
		this.content = content;
	}


	private static volatile CacheStorage eventsStorage;

	private static synchronized Storage getEventsStorage(@NotNull final String namespace, @NotNull final String userId) {
		if (eventsStorage == null) {
			eventsStorage = POLPHARMA_REDIS_DATABASE.createFolderIfNotExists("events");
		}
		return eventsStorage.createFolderIfNotExists(namespace).createFolderIfNotExists(userId);
	}


	private static synchronized Mapper<Event, String> getEventsMapper(@NotNull final String namespace, @NotNull final String userId) {
		return new StorageMapper<>(getEventsStorage(namespace, userId)) {
			@Override protected IdGenerator<String> initIdGenerator() { return new SimpleStringIdGenerator(); }
		};
	}


	public static synchronized Collection<Event> getEvents(@NotNull final String namespace, @NotNull final String userId) {
		return getEventsMapper(namespace, userId).getItems();
	}


	public static synchronized void pushEvent(@NotNull final String namespace, @NotNull final String userId, @NotNull Event event) {

		getEventsMapper(namespace, userId).getId(event);

	}


}
