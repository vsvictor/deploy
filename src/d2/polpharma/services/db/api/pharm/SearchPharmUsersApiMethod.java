package d2.polpharma.services.db.api.pharm;


import d2.polpharma.services.db.api.ProtectedApiMethod;
import d2.polpharma.services.db.model.PharmUser;
import ic.interfaces.getter.Safe2Getter1;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.json.JsonArrays;
import ic.network.http.JsonResponse;
import ic.struct.collection.Collection;
import ic.struct.collection.FilterCollection;
import ic.text.Charset;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static d2.polpharma.services.db.model.PharmUser.getPharmUsers;
import static java.util.Objects.equals;


public class SearchPharmUsersApiMethod extends ProtectedApiMethod {

	@Override protected String getName() { return "search_users"; }

	private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String surname 	= requestJson.optString("surname", 	null);
		final String name 		= requestJson.optString("name", 	null);
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
		final String role 			= requestJson.optString("role", 			null);
		final Date registrationDate; {
			final String registrationDateString = requestJson.optString("date_of_registration", null);
			if (registrationDateString == null) {
				registrationDate = null;
			} else try {
				registrationDate = API_DATE_FORMAT.parse(registrationDateString);
			} catch (ParseException parseException) { throw new UnableToParse(parseException); }
		}
		final String pause 	= requestJson.optString("pause", null);
		final String auth 	= requestJson.optString("auth", null);

		final Collection<PharmUser> filteredUsers = new FilterCollection<PharmUser>(
			getPharmUsers(),
			user -> {
				if (surname != null) 			if (!Objects.equals(	surname, 			user.getSurname())			) return false;
				if (name != null) 				if (!Objects.equals(	name, 				user.getName())				) return false;
				if (birthDate != null) 			if (!Objects.equals(	birthDate, 			user.getBirthDate())		) return false;
				if (city != null) 				if (!Objects.equals(	city, 				user.getCity())				) return false;
				if (pharmacyName != null) 		if (!Objects.equals(	pharmacyName, 		user.getPharmacyName())		) return false;
				if (position != null) 			if (!Objects.equals(	position, 			user.getPosition())			) return false;
				if (line != null) 				if (!Objects.equals(	line, 				user.getLine())				) return false;
				if (teamLead != null) 			if (!Objects.equals(	teamLead, 			user.getTeamLead())			) return false;
				if (role != null) 				if (!Objects.equals(	role, 				user.getRole())				) return false;
				if (registrationDate != null) 	if (!Objects.equals(	registrationDate, 	user.getRegistrationDate())	) return false;
				if (pause != null) 				if (!Objects.equals(	pause, 				user.getPause())			) return false;
				if (auth != null) 				if (!Objects.equals(	auth, 				user.getAuth())				) return false;
				return true;
			}
		);

		final JSONArray filteredUsersJson = JsonArrays.toJsonArray(filteredUsers, (Safe2Getter1<Object, PharmUser, JSONException, Skip>) PharmUser::getPhone);

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject json = new JSONObject();
			json.put("status", "SUCCESS");
			json.put("users", filteredUsersJson);
			return json;
		} };

	}

}
