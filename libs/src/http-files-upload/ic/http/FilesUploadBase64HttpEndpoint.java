package ic.http;


import ic.id.IdGenerator;
import ic.id.SecureStringIdGenerator;
import ic.network.SocketAddress;
import ic.network.http.HttpEndpoint;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.StreamStorage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static ic.util.Base64.base64StringToBytes;


abstract class FilesUploadBase64HttpEndpoint extends HttpEndpoint {

	@Override protected String getName() { return "upload-base64"; }

	protected abstract StreamStorage getStorage();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String contentBase64 	= requestJson.getString("content");
		final String extension 		= requestJson.getString("ext");

		final StreamStorage storage = getStorage();

		final IdGenerator<String> idGenerator = storage.get("id-generator", SecureStringIdGenerator::new);

		final String id = idGenerator.get();

		final String fileName; {
			if (extension == null) {
				fileName = id;
			} else {
				fileName = id + "." + extension;
			}
		}

		storage.write(fileName, base64StringToBytes(contentBase64));

		storage.set("id-generator", idGenerator);

		return new JsonResponse() {
			@Override protected Object getJson() {
				final JSONObject json = new JSONObject();
				json.put("status", "SUCCESS");
				json.put("fileName", fileName);
				return json;
			}
		};

	}

}
