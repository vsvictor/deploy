package ic.http;


import ic.network.SocketAddress;
import ic.network.http.HttpEndpoint;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.StreamStorage;
import ic.stream.ByteSequence;
import ic.text.Charset;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static ic.util.Base64.bytesToBase64String;


public abstract class FilesGetBase64Endpoint extends HttpEndpoint {

	@Override protected String getName() { return "get-base64"; }

	protected abstract StreamStorage getStorage();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String fileName = requestJson.getString("fileName");

		StreamStorage storage = getStorage(); {
			assert storage != null;
		}

		final ByteSequence content; try {
			content = storage.readOrThrow(fileName);
		} catch (NotExists notExists) {
			return new JsonResponse() {
				@Override protected Object getJson() {
					final JSONObject responseJson = new JSONObject();
					responseJson.put("status", "NOT_EXISTS");
					return responseJson;
				}
			};
		}

		final String contentBase64 = bytesToBase64String(content);

		return new JsonResponse() {
			@Override protected Object getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUCCESS");
				responseJson.put("content", contentBase64);
				return responseJson;
			}
		};

	}

}
