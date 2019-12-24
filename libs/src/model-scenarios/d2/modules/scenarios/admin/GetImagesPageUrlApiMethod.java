package d2.modules.scenarios.admin;


import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.throwables.UnableToParse;
import org.json.JSONObject;


@Deprecated
public abstract class GetImagesPageUrlApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "get_images_page_url"; }

	protected abstract String getImagesPageUrl();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final String imagesPageUrl = getImagesPageUrl();

		return new JsonResponse() {
			@Override protected JSONObject getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUCCESS");
				responseJson.put("imagesPageUrl", imagesPageUrl);
				return responseJson;
			}
		};

	}

}
