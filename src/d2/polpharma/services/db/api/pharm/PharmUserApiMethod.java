package d2.polpharma.services.db.api.pharm;


import d2.polpharma.services.db.api.ProtectedApiMethod;
import d2.polpharma.services.db.model.PharmUser;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.text.Charset;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static d2.polpharma.services.db.model.PharmUser.*;


public class PharmUserApiMethod extends ProtectedApiMethod {


	@Override protected String getName() { return "user"; }


	private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");


	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		if (request.method.equals("GET")) {

			final String id; try {
				id = request.urlParams.getOrThrow("id");
			} catch (NotExists notFound) { throw new UnableToParse(notFound); }

			final PharmUser user; try {
				user = getPharmUserById(id);
			} catch (NotExists notFound) {
				return new JsonResponse() { @Override protected JSONObject getJson() {
					final JSONObject json = new JSONObject();
					json.put("status", "NOT_FOUND");
					return json;
				} };
			}


			final JSONObject json = new JSONObject();

			final String phone; synchronized (user) {
				phone = user.getPhone();
			}

			json.put("phone", phone);

			final String secondPhone; synchronized (user) {
				secondPhone = user.getSecondPhone();
			}

			json.put("phone_second", secondPhone);

			final String role;
			final boolean admin;
			final String surname;
			final String name;
			final Date birthDate;
			final String city;
			final String pharmacyName;
			final String position;
			final String line;
			final String teamLead;
			final Date registrationDate;
			final String pause;
			final String auth; synchronized (user) {
				role 				= user.getRole();
				admin				= user.isAdmin();
				surname 			= user.getSurname();
				name 				= user.getName();
				birthDate 			= user.getBirthDate();
				city 				= user.getCity();
				pharmacyName 		= user.getPharmacyName();
				position 			= user.getPosition();
				line 				= user.getLine();
				teamLead 			= user.getTeamLead();
				registrationDate 	= user.getRegistrationDate();
				pause 				= user.getPause();
				auth 				= user.getAuth();
			}
			json.putOpt("role",		role);
			json.put("admin",		admin);
			json.putOpt("surname", 	surname);
			json.putOpt("name", 	name);
			if (birthDate != null) json.put("dob", API_DATE_FORMAT.format(birthDate));
			json.putOpt("city", 			city);
			json.putOpt("pharmacy_name", 	pharmacyName);
			json.putOpt("position",			position);
			json.putOpt("line",				line);
			json.putOpt("team_lead",		teamLead);
			if (registrationDate != null) 	json.put("date_of_registration", 	API_DATE_FORMAT.format(registrationDate));
			json.putOpt("pause",			pause);
			json.putOpt("auth",				auth);

			return new JsonResponse() { @Override protected JSONObject getJson() { return json; } };

		} else

		if (request.method.equals("POST")) {

			final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

			final String id = requestJson.getString("id");

			final String phone = requestJson.optString("phone", null);
			final String secondPhone = requestJson.optString("phone_second", null);

			final PharmUser user; try {
				user = getPharmUserById(id);
			} catch (NotExists notFound) {
				return new JsonResponse() { @Override protected JSONObject getJson() {
					final JSONObject json = new JSONObject();
					json.put("status", "NOT_FOUND");
					return json;
				} };
			}

			final String surname 		= requestJson.optString("surname", 		null);
			final String name 			= requestJson.optString("name", 		null);
			final Date birthDate; {
				final String birthDateString = requestJson.optString("dob", null);
				if (birthDateString == null) {
					birthDate = null;
				} else try {
					birthDate = API_DATE_FORMAT.parse(birthDateString);
				} catch (ParseException parseException) { throw new UnableToParse(parseException); }
			}
			final String city 			= requestJson.optString("city", 			null);
			final String pharmacyName 	= requestJson.optString("pharmacy_name", 	null);
			final String position 		= requestJson.optString("position",			null);
			final String line			= requestJson.optString("line",				null);
			final String teamLead 		= requestJson.optString("team_lead",		null);
			final String role 			= requestJson.optString("role",				null);
			final Date registrationDate; {
				final String registrationDateString = requestJson.optString("date_of_registration", null);
				if (registrationDateString == null) {
					registrationDate = null;
				} else try {
					registrationDate = API_DATE_FORMAT.parse(registrationDateString);
				} catch (ParseException parseException) { throw new UnableToParse(parseException); }
			}
			final String pause = requestJson.optString("pause", null);
			final String auth = requestJson.optString("auth", null);
			synchronized (user) {
				if (phone != null)				user.setPhone(phone);
				if (secondPhone != null)		user.setSecondPhone(secondPhone);
				if (surname != null)			user.setSurname(surname);
				if (name != null)				user.setName(name);
				if (birthDate != null)			user.setBirthDate(birthDate);
				if (city != null)				user.setCity(city);
				if (pharmacyName != null)		user.setPharmacyName(pharmacyName);
				if (position != null)			user.setPosition(position);
				if (line != null)				user.setLine(line);
				if (teamLead != null)			user.setTeamLead(teamLead);
				if (role != null)				user.setRole(role);
				if (registrationDate != null)	user.setRegistrationDate(registrationDate);
				if (pause != null)				user.setPause(pause);
				if (auth != null)				user.setAuth(auth);
			}

			return new JsonResponse() { @Override protected JSONObject getJson() {
				final JSONObject json = new JSONObject();
				json.put("status", "SUCCESS");
				return json;
			} };

		} else

		if (request.method.equals("PUT")) {

			final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

			final String phone 			= requestJson.optString("phone", 		null);
			final String secondPhone 	= requestJson.optString("phone_second", null);
			final String role 			= requestJson.optString("role", 		null);
			final boolean admin			= requestJson.optBoolean("admin", 		false);

			final String surname 		= requestJson.optString("surname", 		null);
			final String name 			= requestJson.optString("name", 		null);
			final Date birthDate; {
				final String birthDateString = requestJson.optString("dob", null);
				if (birthDateString == null) {
					birthDate = null;
				} else try {
					birthDate = API_DATE_FORMAT.parse(birthDateString);
				} catch (ParseException parseException) { throw new UnableToParse(parseException); }
			}
			final String city 			= requestJson.optString("city", 			null);
			final String pharmacyName 	= requestJson.optString("pharmacy_name", 	null);
			final String position 		= requestJson.optString("position", 		null);
			final String line			= requestJson.optString("line",				null);
			final String teamLead 		= requestJson.optString("team_lead", 		null);
			final Date registrationDate; {
				final String registrationDateString = requestJson.optString("date_of_registration", null);
				if (registrationDateString == null) {
					registrationDate = null;
				} else try {
					registrationDate = API_DATE_FORMAT.parse(registrationDateString);
				} catch (ParseException parseException) { throw new UnableToParse(parseException); }
			}
			final String pause = requestJson.optString("pause", null);
			final String auth = requestJson.optString("auth", null);

			final String userId = putPharmUser(phone, secondPhone, role, admin, surname, name, birthDate, city, pharmacyName, position, line, teamLead, registrationDate, pause, auth);

			return new JsonResponse() { @Override protected JSONObject getJson() {
				final JSONObject json = new JSONObject();
				json.put("status", "SUCCESS");
				json.put("userId", userId);
				return json;
			} };

		}

		else throw new UnableToParse("requestMethod: " + request.method);

	}


}
