package d2.polpharma.services.db.model;


import ic.annotations.Necessary;
import ic.event.Event;
import ic.interfaces.changeable.Changeable;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.id.IdGenerator;
import ic.id.SecureStringIdGenerator;
import ic.id.StorageMapper;
import ic.serial.stringmap.StringMapSerializable;
import ic.struct.collection.Collection;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static d2.polpharma.services.db.PolpharmaRedisDataBase.POLPHARMA_REDIS_DATABASE;


public class PharmUser implements StringMapSerializable, Changeable {


	protected static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");


	private String phone;

	public synchronized String getPhone() { return phone; }

	public synchronized void setPhone(String phone) { this.phone = phone; changeEventTrigger.run(); }


	private String secondPhone;

	public synchronized String getSecondPhone() { return secondPhone; }

	public synchronized void setSecondPhone(String secondPhone) { this.secondPhone = secondPhone; changeEventTrigger.run(); }


	private String role;

	public synchronized String getRole() { return role; }

	public synchronized void setRole(String role) { this.role = role; changeEventTrigger.run(); }


	private boolean admin;

	public synchronized boolean isAdmin() { return admin; }


	private String surname;

	public synchronized String getSurname() { return surname; }

	public synchronized void setSurname(String surname) { this.surname = surname; changeEventTrigger.run(); }


	private String name;

	public synchronized String getName() { return name; }

	public synchronized void setName(String name) { this.name = name; changeEventTrigger.run(); }


	private Date birthDate;

	public synchronized Date getBirthDate() { return birthDate; }

	public synchronized void setBirthDate(Date birthDate) { this.birthDate = birthDate; changeEventTrigger.run(); }


	private String city;

	public synchronized String getCity() { return city; }

	public synchronized void setCity(String city) { this.city = city; changeEventTrigger.run(); }


	private String pharmacyName;

	public synchronized String getPharmacyName() { return pharmacyName; }

	public synchronized void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; changeEventTrigger.run(); }


	private String position;

	public synchronized String getPosition() { return position; }

	public synchronized void setPosition(String position) { this.position = position; changeEventTrigger.run(); }


	private String line;

	public synchronized String getLine() { return line; }

	public synchronized void setLine(String line) { this.line = line; changeEventTrigger.run(); }


	private String teamLead;

	public synchronized String getTeamLead() { return teamLead; }

	public synchronized void setTeamLead(String teamLead) { this.teamLead = teamLead; changeEventTrigger.run(); }


	private Date registrationDate;

	public synchronized Date getRegistrationDate() { return registrationDate; }

	public synchronized void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; changeEventTrigger.run(); }


	private String pause;

	public synchronized String getPause() { return pause; }

	public synchronized void setPause(String pause) { this.pause = pause; changeEventTrigger.run(); }


	private String auth;

	public synchronized String getAuth() { return auth; }

	public synchronized void setAuth(String auth) { this.auth = auth; changeEventTrigger.run(); }


	// Changeable implementation:

	private final Event.Trigger changeEventTrigger = new Event.Trigger();

	@Override public Event getChangeEvent() { return changeEventTrigger; }


	// StringMapSerializable implementation:

	@Override public Class<?> getClassToDeclare() { return PharmUser.class; }

	@Override public synchronized void serialize(Setter1<String, String> output) {
		output.set("phone", 		phone);
		output.set("secondPhone", 	secondPhone);
		output.set("role",			role);
		output.set("admin",			Boolean.toString(admin));
		output.set("surname", 		surname);
		output.set("name", 			name);
		if (birthDate != null) {
			output.set("birthDate", DB_DATE_FORMAT.format(birthDate));
		}
		output.set("city", 			city);
		output.set("pharmacyName", 	pharmacyName);
		output.set("position",		position);
		output.set("line",			line);
		output.set("teamLead",		teamLead);
		if (registrationDate != null) {
			output.set("registrationDate", DB_DATE_FORMAT.format(registrationDate));
		}
		output.set("pause",	pause);
		output.set("auth", auth);
	}

	@Necessary public PharmUser(Getter1<String, String> input) throws UnableToParse {
		this.phone 			= input.get("phone");
		this.secondPhone 	= input.get("secondPhone");
		this.role			= input.get("role");
		{
			final String adminString = input.get("admin");
			if (adminString == null) 	this.admin = false;
			else 						this.admin = Boolean.parseBoolean(adminString);
		}
		this.surname 		= input.get("surname");
		this.name 			= input.get("name");
		{ final String birthDateString = input.get("birthDate");
			if (birthDateString == null) {
				birthDate = null;
			} else try {
				birthDate = DB_DATE_FORMAT.parse(birthDateString);
			} catch (ParseException parseException) { throw new UnableToParse(parseException); }
		}
		this.city 			= input.get("city");
		this.pharmacyName 	= input.get("pharmacyName");
		this.position		= input.get("position");
		this.line			= input.get("line");
		this.teamLead		= input.get("teamLead");
		{ final String registrationDateString = input.get("registrationDateString");
			if (registrationDateString == null) {
				registrationDate = null;
			} else try {
				registrationDate = DB_DATE_FORMAT.parse(registrationDateString);
			} catch (ParseException parseException) { throw new UnableToParse(parseException); }
		}
		this.pause = input.get("pause");
		this.auth = input.get("auth");
	}


	private PharmUser(
		String phone,
		String secondPhone,
		String role,
		boolean admin,
		String surname,
		String name,
		Date birthDate,
		String city,
		String pharmacyName,
		String position,
		String line,
		String teamLead,
		Date registrationDate,
		String pause,
		String auth
	) {
		assert role != null;
		this.phone 				= phone;
		this.secondPhone		= secondPhone;
		this.role				= role;
		this.admin				= admin;
		this.surname 			= surname;
		this.name 				= name;
		this.birthDate 			= birthDate;
		this.city 				= city;
		this.pharmacyName 		= pharmacyName;
		this.position 			= position;
		this.line 				= line;
		this.teamLead 			= teamLead;
		this.registrationDate 	= registrationDate;
		this.pause 				= pause;
		this.auth 				= auth;
	}


	// Static:

	private static final StorageMapper<PharmUser> PHARM_USERS_MAPPER = new StorageMapper<PharmUser>(
		POLPHARMA_REDIS_DATABASE.createFolderIfNotExists("pharmUsers")
	) {
		@Override protected IdGenerator<String> initIdGenerator() { return new SecureStringIdGenerator(); }
	};

	public static Collection<String> getPharmUserIds() {
		return PHARM_USERS_MAPPER.getIds();
	}

	public static Collection<PharmUser> getPharmUsers() {
		return PHARM_USERS_MAPPER.getItems();
	}


	// Users by phone:

	public static synchronized String putPharmUser(
		String phone,
		String secondPhone,
		String role,
		boolean admin,
		String surname,
		String name,
		Date birthDate,
		String city,
		String pharmacyName,
		String position,
		String line,
		String teamLead,
		Date registrationDate,
		String pause,
		String auth
	) {
		return PHARM_USERS_MAPPER.getId(
			new PharmUser(phone, secondPhone, role, admin, surname, name, birthDate, city, pharmacyName, position, line, teamLead, registrationDate, pause, auth)
		);
	}

	public static synchronized PharmUser getPharmUserById(@NotNull String id) throws NotExists {
		return PHARM_USERS_MAPPER.getItem(id);
	}


}
