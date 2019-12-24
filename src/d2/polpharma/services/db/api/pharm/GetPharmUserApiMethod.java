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

import java.text.SimpleDateFormat;
import java.util.Date;

import static d2.polpharma.services.db.model.PharmUser.getPharmUserById;


public class GetPharmUserApiMethod extends ProtectedApiMethod {

	@Override protected String getName() { return "get_user"; }

	private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String userId = requestJson.getString("userId");

		final PharmUser user; try {
			user = getPharmUserById(userId);
		} catch (NotExists notFound) {
			return new JsonResponse() { @Override protected JSONObject getJson() {
				final JSONObject json = new JSONObject();
				json.put("status", "USER_NOT_FOUND");
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

	}


}
