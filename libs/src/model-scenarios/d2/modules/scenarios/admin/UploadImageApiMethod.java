package d2.modules.scenarios.admin;


import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.stream.ByteSequence;
import ic.throwables.UnableToParse;
import org.json.JSONObject;


@Deprecated
public abstract class UploadImageApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "upload_image"; }

	protected abstract String uploadImage(ByteSequence image, String name);

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final String name = request.urlParams.getNotNull("name");

		final String url = uploadImage(request.body, name);

		return new JsonResponse() {
			@Override protected JSONObject getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUCCESS");
				responseJson.put("url", url);
				return responseJson;
			}
		};

	}

}
