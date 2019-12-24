package ic.geo;


import ic.interfaces.getter.Safe3Getter1;
import ic.json.JsonArrays;
import ic.network.http.HttpClient;
import ic.network.http.HttpException;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.stream.ByteSequence;
import ic.struct.collection.Collection;
import ic.struct.map.CountableMap;
import ic.text.Charset;
import ic.text.Language;
import ic.throwables.AccessDenied;
import ic.throwables.IOException;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;


public class Place {


	public final @NotNull Location location;

	public final @NotNull String address;

	public Place(@NotNull Location location, @NotNull String address) {

		this.location = location;
		this.address = address;

	}


	public static Collection<Place> findPlacesByAddress(@NotNull String address, @NotNull Language language, @NotNull String googleApiKey) throws IOException, HttpException, AccessDenied {

		final HttpResponse response = HttpClient.request(
			new HttpRequest(
				(
					"https://maps.googleapis.com/maps/api/geocode/json?" +
					"address=" + Charset.DEFAULT_HTTP.encodeUrl(address) + "&" +
					"language=" + language.code + "&" +
					"key=" + googleApiKey
				),
				"GET",
				new CountableMap.Default<String, String>(),
				ByteSequence.EMPTY
			)
		);

		final String responseString = Charset.DEFAULT_HTTP.bytesToString(response.getBody());

		final JSONObject responseJson = new JSONObject(responseString);

		final String responseStatus = responseJson.getString("status");

		if (responseStatus.equals("OVER_QUERY_LIMIT")) 	throw new AccessDenied(responseString);
		if (responseStatus.equals("REQUEST_DENIED")) 	throw new AccessDenied(responseString);

		if (responseStatus.equals("ZERO_RESULTS")) return new Collection.Default<Place>();

		if (!responseStatus.equals("OK")) throw new HttpException(response);

		try {
			return JsonArrays.jsonArrayToList(
				responseJson.getJSONArray("results"),
				(Safe3Getter1<Place, JSONObject, JSONException, UnableToParse, Skip>) placeJson -> {
					final JSONObject locationJson = placeJson.getJSONObject("geometry").getJSONObject("location");
					return new Place(
						new Location(
							locationJson.getDouble("lat"),
							locationJson.getDouble("lng")
						),
						placeJson.getString("formatted_address")
					);
				}
			);
		} catch (UnableToParse unableToParse) { throw new HttpException(response, unableToParse); }

	}


}
