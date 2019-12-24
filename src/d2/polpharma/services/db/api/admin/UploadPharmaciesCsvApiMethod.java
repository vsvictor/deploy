package d2.polpharma.services.db.api.admin;


import d2.polpharma.services.db.model.pharm.Pharmacy;
import ic.id.Mapper;
import ic.interfaces.action.Action2;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.struct.list.List;
import ic.text.Charset;
import ic.text.Text;
import ic.throwables.UnableToParse;
import kotlin.Unit;
import org.json.JSONObject;

import static d2.polpharma.services.db.model.pharm.Pharmacy.getPharmaciesMapper;
import static ic.csv.Csv.parseCsv;


public class UploadPharmaciesCsvApiMethod extends ProtectedApiMethod {

	@Override protected String getName() { return "upload_pharmacies"; }

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final Text csvText = Charset.UTF_8.bytesToText(request.body);

		final List<List<String>> csv = parseCsv(csvText, ',');

		final Mapper<Pharmacy, String> pharmaciesMapper = getPharmaciesMapper();

		pharmaciesMapper.empty();

		csv.forEach((Action2<List<String>, Integer>) (row, rowIndex) -> {

			if (rowIndex == 0) return;

			pharmaciesMapper.getId(new Pharmacy(
				row.get(0),
				row.get(1),
				row.get(4),
				row.get(3)
			));

		});

		return new JsonResponse() { @Override protected JSONObject getJson() {
			final JSONObject responseJson = new JSONObject();
			responseJson.put("status", "SUCCESS");
			return responseJson;
		} };

	}

}
