package ic.http;


import org.jetbrains.annotations.Nullable;
import ic.id.IdGenerator;
import ic.id.SecureStringIdGenerator;
import ic.network.SocketAddress;
import ic.network.http.HttpEndpoint;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.StreamStorage;
import ic.throwables.UnableToParse;
import org.json.JSONObject;


abstract class FilesUploadHttpEndpoint extends HttpEndpoint {

	@Override protected String getName() { return "upload"; }

	protected abstract StreamStorage getStorage();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final StreamStorage storage = getStorage();

		final IdGenerator<String> idGenerator = storage.get("id-generator", SecureStringIdGenerator::new);

		final String id = idGenerator.get();

		final String fileName; {
			final @Nullable String extension = request.urlParams.get("ext");
			if (extension == null) {
				fileName = id;
			} else {
				fileName = id + "." + extension;
			}
		}

		storage.write(fileName, request.body);

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
