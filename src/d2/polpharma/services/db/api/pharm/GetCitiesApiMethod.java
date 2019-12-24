package d2.polpharma.services.db.api.pharm;


import d2.polpharma.services.db.api.ProtectedApiMethod;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.struct.collection.ConvertCollection;
import ic.struct.order.OrderedCountableSet;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.polpharma.services.db.model.pharm.Pharmacy.getPharmaciesMapper;
import static ic.json.JsonArrays.toJsonArray;
import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;
import static ic.throwables.Skip.SKIP;


public class GetCitiesApiMethod extends ProtectedApiMethod {

	@Override protected String getName() {
		return "get_cities";
	}

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String region = requestJson.optString("region");
		final String startsWith = requestJson.optString("startsWith", "").toUpperCase();

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
		}

		responseJson.put("cities", toJsonArray(
			new OrderedCountableSet.Default<>(
				ALPHABETIC_STRING_ORDER,
				new ConvertCollection<>(
					getPharmaciesMapper().getItems(),
					pharmacy -> {
						if (region != null) {
							if (!pharmacy.region.equals(region)) throw SKIP;
						}
						String city = pharmacy.city.toUpperCase();
						if (city.startsWith("Г. ")) 	city = city.substring("Г. ".length()); 		else
						if (city.startsWith("ПГТ. ")) 	city = city.substring("ПГТ. ".length()); 	else
						if (city.startsWith("С. ")) 	city = city.substring("С. ".length());
						if (!city.startsWith(startsWith)) throw SKIP;
						return pharmacy.city;
					}
				)
			),
			string -> string
		));

		return new JsonResponse() { @Override protected JSONObject getJson() {
			return responseJson;
		} };

	}

}
