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


public class GetRegionsApiMethod extends ProtectedApiMethod {

	@Override protected String getName() {
		return "get_regions";
	}

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String startsWith = requestJson.optString("startsWith", "").toUpperCase();

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
		}

		responseJson.put("regions", toJsonArray(
			new OrderedCountableSet.Default<>(
				ALPHABETIC_STRING_ORDER,
				new ConvertCollection<>(
					getPharmaciesMapper().getItems(),
					pharmacy -> {
						if (!pharmacy.region.toUpperCase().startsWith(startsWith)) throw SKIP;
						return pharmacy.region;
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
