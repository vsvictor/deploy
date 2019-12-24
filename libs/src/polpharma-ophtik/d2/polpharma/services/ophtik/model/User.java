package d2.polpharma.services.ophtik.model;


import ic.annotations.Necessary;
import ic.event.Event;
import ic.id.IdGenerator;
import ic.id.SecureStringIdGenerator;
import ic.id.StorageMapper;
import ic.interfaces.changeable.Changeable;
import ic.serial.json.JsonSerializable;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Date;

import static d2.polpharma.services.ophtik.OphtikStoragesKt.ophtikUsersStorage;
import static ic.date.Millis.*;


public class User implements JsonSerializable, Changeable {


	private String phone;

	public synchronized String getPhone() { return phone; }

	public synchronized void setPhone(String phone) { this.phone = phone; changeEventTrigger.run(); }


	private @NotNull String role;

	public synchronized @NotNull String getRole() { return role; }

	public synchronized void setRole(@NotNull String role) { this.role = role; changeEventTrigger.run(); }


	private boolean isAdmin;

	public synchronized boolean isAdmin() { return isAdmin; }


	private String expert;
	public synchronized String getExpert() { return expert; }
	public synchronized void setExpert(String expert) { this.expert = expert; changeEventTrigger.run(); }


	private String confidentiality;
	public synchronized String getConfidentiality() { return confidentiality; }
	public synchronized void setConfidentiality(String confidentiality) {
		this.confidentiality = confidentiality; changeEventTrigger.run();
	}


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


	public final @NotNull Date registrationDate;


	private boolean isSubscribedToWeather;
	public synchronized boolean isSubscribedToWeather() { return isSubscribedToWeather; }
	public synchronized void setSubscribedToWeather(boolean isSubscribedToWeather) {
		this.isSubscribedToWeather = isSubscribedToWeather; changeEventTrigger.run();
	}


	// Changeable implementation:

	private final Event.Trigger changeEventTrigger = new Event.Trigger();

	@Override public Event getChangeEvent() { return changeEventTrigger; }


	// StringMapSerializable implementation:

	@Override @NotNull public Class<?> getClassToDeclare() { return User.class; }

	@Override public synchronized void serialize(JSONObject json) {
		json.putOpt("phone", 			phone);
		json.put("role",				role);
		json.putOpt("isAdmin",			isAdmin);
		json.putOpt("expert", 			expert);
		json.putOpt("confidentiality", 	confidentiality);
		json.putOpt("surname", 			surname);
		json.putOpt("name", 			name);
		json.putOpt("city", 			city);
		json.putOpt("birthDate", 		nullableDateToMillis(birthDate));
		json.putOpt("registrationDate", dateToMillis(registrationDate));
		json.put("isSubscribedToWeather", isSubscribedToWeather);
	}

	@Necessary public User(JSONObject json) throws UnableToParse {
		this.phone 				= json.optString("phone", null);
		this.role				= json.getString("role");
		this.isAdmin			= json.optBoolean("isAdmin", false);
		this.expert 			= json.optString("expert", null);
		this.confidentiality 	= json.optString("confidentiality", null);
		this.surname 			= json.optString("surname", null);
		this.name 				= json.optString("name", null);
		this.city 				= json.optString("city", null);
		{
			final long birthDateMillis = json.optLong("birthDate", Long.MIN_VALUE);
			if (birthDateMillis == Long.MIN_VALUE) {
				this.birthDate = null;
			} else {
				this.birthDate = millisToDate(birthDateMillis);
			}
		}
		this.registrationDate = millisToDate(json.getLong("registrationDate"));
		this.isSubscribedToWeather = json.optBoolean("isSubscribedToWeather", false);
	}


	public User(
		@NotNull String role,
		boolean isAdmin,
		String phone,
		String expert,
		String confidentiality,
		String surname,
		String name,
		Date birthDate,
		String city,
		boolean isSubscribedToWeather
	) {
		this.phone 				= phone;
		this.role 				= role;
		this.isAdmin 			= isAdmin;
		this.expert 			= expert;
		this.confidentiality	= confidentiality;
		this.surname 			= surname;
		this.name 				= name;
		this.birthDate 			= birthDate;
		this.city 				= city;
		this.registrationDate 	= now();
		this.isSubscribedToWeather = isSubscribedToWeather;
	}


	// Static:

	public static final StorageMapper<User> mapper = new StorageMapper<>(ophtikUsersStorage) {
		@Override protected IdGenerator<String> initIdGenerator() { return new SecureStringIdGenerator(); }
	};


}
