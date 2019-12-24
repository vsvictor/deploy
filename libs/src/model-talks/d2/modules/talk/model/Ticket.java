package d2.modules.talk.model;


import ic.annotations.ByIdentity;
import ic.annotations.Necessary;
import ic.id.IdGenerator;
import ic.id.Mapper;
import ic.id.SecureStringIdGenerator;
import ic.id.StorageMapper;
import ic.interfaces.getter.Getter;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.stringmap.StringMapSerializable;
import ic.storage.CacheStorage;
import ic.storage.Storage;
import ic.struct.list.EditableList;
import ic.struct.map.EditableMap;
import ic.struct.map.EphemeralKeysMap;
import ic.struct.set.CountableSet;
import ic.struct.variable.Weak;
import org.jetbrains.annotations.NotNull;


public class Ticket implements StringMapSerializable {


	public final String userId;
	public final String talkEventId;

	@Override public Class getClassToDeclare() { return Ticket.class; }

	@Override public void serialize(Setter1<String, String> output) {
		output.set("userId", 		userId);
		output.set("talkEventId", 	talkEventId);
	}

	@Necessary public Ticket(Getter1<String, String> input) {
		this.userId			= input.get("userId");
		this.talkEventId 	= input.get("talkEventId");
	}


	private static final EditableMap<Mapper<Ticket, String>, Storage> MAPPERS = new EphemeralKeysMap<Mapper<Ticket, String>, Storage>(
		Weak::new
	);

	public static synchronized Mapper<Ticket, String> getTicketsMapper(@NotNull @ByIdentity final CacheStorage storage) {
		return MAPPERS.createIfNull(
			storage,
			() -> new StorageMapper<Ticket>(
				storage.createFolderIfNotExists("tickets")
			) {
				@Override protected IdGenerator<String> initIdGenerator() {
					return new SecureStringIdGenerator();
				}
			}
		);
	}


	public Ticket(@NotNull CacheStorage storage, @NotNull String userId, @NotNull String talkEventId) {
		this.userId 		= userId;
		this.talkEventId 	= talkEventId;
		final String id = getTicketsMapper(storage).getId(this);
		storage.createFolderIfNotExists("tickets-by-user").createIfNull(
			userId,
			(Getter<EditableList<String>>) () -> new EditableList.Default<String>()
		).add(id);
	}


	public static CountableSet<String> getUsersWithTickets(@NotNull CacheStorage storage) {
		return storage.createFolderIfNotExists("ticketsByUser").getKeys();
	}


}
