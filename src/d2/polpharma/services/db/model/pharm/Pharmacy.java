package d2.polpharma.services.db.model.pharm;


import ic.annotations.Necessary;
import ic.event.Event;
import ic.geo.Location;
import ic.id.*;
import ic.interfaces.changeable.Changeable;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.stringmap.StringMapSerializable;
import ic.serial.stringmap.StringMapSerializer;
import ic.struct.variable.SettableVariable;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static d2.polpharma.services.db.PolpharmaRedisDataBase.POLPHARMA_REDIS_DATABASE;


public class Pharmacy implements StringMapSerializable, Changeable {


	public final @NotNull String region;

	public final @NotNull String city;

	public final @NotNull String address;


	public final @NotNull String name;


	private @Nullable Location location;

	public synchronized @Nullable Location getLocation() { return location; }

	public synchronized void setLocation(@NotNull Location location) {
		this.location = location;
		changeEvent.run();
	}


	// Changeable implementation;

	private final Event.Trigger changeEvent = new Event.Trigger();

	@Override public Event getChangeEvent() { return changeEvent; }


	// StringMapSerializable implementation:

	@Override public Class getClassToDeclare() { return Pharmacy.class; }

	@Override public void serialize(Setter1<String, String> output) {
		output.set("region", 	region);
		output.set("city", 		city);
		output.set("address", 	address);
		output.set("name", 		name);
		StringMapSerializer.write(output, "location", location);
	}

	@Necessary public Pharmacy(Getter1<String, String> input) throws UnableToParse {
		this.region 	= input.get("region");
		this.city 		= input.get("city");
		this.address 	= input.get("address");
		this.name		= input.get("name");
		this.location	= StringMapSerializer.read(input, "location");
	}


	public Pharmacy(@NotNull String region, @NotNull String city, @NotNull String address, @NotNull String name) {

		this.region 	= region;
		this.city 		= city;
		this.address 	= address;
		this.name 		= name;

	}

	public Pharmacy(@NotNull String region, @NotNull String city, @NotNull String address, @NotNull String name, double latitude, double longitude) {

		this.region 	= region;
		this.city 		= city;
		this.address 	= address;
		this.name 		= name;
		this.location   = new Location(latitude, longitude);
	}

	private static final SettableVariable<Mapper<Pharmacy, String>> PHARMACIES_MAPPER_VARIABLE
		= new SettableVariable.Default<>(null)
	;

	public static Mapper<Pharmacy, String> getPharmaciesMapper() {
		return PHARMACIES_MAPPER_VARIABLE.createIfNull(() -> new StorageMapper<Pharmacy>(
			POLPHARMA_REDIS_DATABASE.createFolderIfNotExists("pharm").createFolderIfNotExists("pharmacies")
		) {
			@Override protected IdGenerator<String> initIdGenerator() {
				return new SecureStringIdGenerator();
			}
		});
	}


}
