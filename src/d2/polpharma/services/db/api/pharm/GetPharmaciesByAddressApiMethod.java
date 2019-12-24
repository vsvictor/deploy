package d2.polpharma.services.db.api.pharm;


import d2.polpharma.services.db.api.ProtectedApiMethod;
import d2.polpharma.services.db.model.pharm.Pharmacy;
import ic.id.Mapper;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.struct.collection.FilterCollection;
import ic.struct.set.CountableSet;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.polpharma.services.db.model.pharm.Pharmacy.getPharmaciesMapper;
import static ic.json.JsonArrays.toJsonArray;


public class GetPharmaciesByAddressApiMethod extends ProtectedApiMethod {

	@Override protected String getName() {
		return "get_pharmacies_by_address";
	}

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String region = requestJson.optString("region");
		final String city 	= requestJson.optString("city");
		final String startsWith = requestJson.optString("startsWith", "").toUpperCase();

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
		}

		final Mapper<Pharmacy, String> pharmaciesMapper = getPharmaciesMapper();

		responseJson.put("pharmacies", toJsonArray(
			new CountableSet.Default<Pharmacy>(
				new FilterCollection<Pharmacy>(
					pharmaciesMapper.getItems(),
					pharmacy -> {
						if (region != null) {
							if (!pharmacy.region.equals(region)) return false;
						}
						if (city != null) {
							if (!pharmacy.city.equals(city)) return false;
						}
						String address = pharmacy.address.toUpperCase();
						if (address.startsWith("ВУЛ. ")) 	address = address.substring("ВУЛ. ".length()); 		else
						if (address.startsWith("ПРОСП. ")) 	address = address.substring("ПРОСП. ".length()); 	else
						if (address.startsWith("БУЛ. ")) 	address = address.substring("БУЛ. ".length()); 		else
						if (address.startsWith("ПЛ. ")) 	address = address.substring("ПЛ. ".length()); 		else
						if (address.startsWith("ШОСЕ ")) 	address = address.substring("ШОСЕ ".length()); 		else
						if (address.startsWith("З/М. ")) 	address = address.substring("З/М. ".length()); 		else
						if (address.startsWith("МКР-Н ")) 	address = address.substring("МКР-Н ".length());
						return (address.startsWith(startsWith));
					}
				)
			),
			pharmacy -> {
				final JSONObject pharmacyJson = new JSONObject();
				pharmacyJson.put("id", 		pharmaciesMapper.getId(pharmacy));
				pharmacyJson.put("region", 	pharmacy.region);
				pharmacyJson.put("city", 	pharmacy.city);
				pharmacyJson.put("address", pharmacy.address);
				pharmacyJson.put("name", 	pharmacy.name);
				return pharmacyJson;
			}
		));

		return new JsonResponse() { @Override protected JSONObject getJson() {
			return responseJson;
		} };

	}

}
