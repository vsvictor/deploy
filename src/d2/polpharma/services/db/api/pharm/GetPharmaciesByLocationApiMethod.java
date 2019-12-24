package d2.polpharma.services.db.api.pharm;


import d2.polpharma.services.db.api.ProtectedApiMethod;
import d2.polpharma.services.db.model.pharm.Pharmacy;
import ic.geo.Location;
import ic.id.Mapper;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.struct.collection.Collection;
import ic.struct.collection.FilterCollection;
import ic.struct.list.List;
import ic.struct.order.ConvertOrder;
import ic.struct.order.Order;
import ic.struct.order.OrderedCountableSet;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONArray;
import org.json.JSONObject;

import static d2.polpharma.services.db.model.pharm.Pharmacy.getPharmaciesMapper;
import static ic.geo.Location.getDistance;
import static ic.json.JsonArrays.toJsonArray;
import static ic.struct.order.Order.FLOAT_ORDER;


public class GetPharmaciesByLocationApiMethod extends ProtectedApiMethod {


	@Override protected String getName() { return "get_pharmacies_by_location"; }


	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final JSONObject locationJson = requestJson.getJSONObject("location");

		final Location location = new Location(
			locationJson.getDouble("latitude"),
			locationJson.getDouble("longitude")
		);

		final int count = requestJson.getInt("count");

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
		}

		final Mapper<Pharmacy, String> pharmaciesMapper = getPharmaciesMapper();

		JSONArray arr = toJsonArray(
				new List.Default<Pharmacy>(
						(Collection<Pharmacy>) (new OrderedCountableSet.Default<Pharmacy>(
								new ConvertOrder<>(
										FLOAT_ORDER,
										pharmacy -> getDistance(location, pharmacy.getLocation())
								),
								new FilterCollection<Pharmacy>(
										pharmaciesMapper.getItems(),
										pharmacy -> pharmacy.getLocation() != null
								)
						)),
						count
				),
				pharmacy -> {
					if(pharmacy == null) throw new NullPointerException();
					final JSONObject pharmacyJson = new JSONObject();
					pharmacyJson.put("id", pharmaciesMapper.getId(pharmacy));
					pharmacyJson.put("region", 	pharmacy.region);
					pharmacyJson.put("city", 	pharmacy.city);
					pharmacyJson.put("address", pharmacy.address);
					pharmacyJson.put("name", 	pharmacy.name);
					return pharmacyJson;
				}
		);

		responseJson.put("pharmacies", arr);

		return new JsonResponse() { @Override protected JSONObject getJson() { return responseJson; } };

	}


}
