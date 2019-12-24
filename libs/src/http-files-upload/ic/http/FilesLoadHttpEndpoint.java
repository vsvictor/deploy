package ic.http;


import org.jetbrains.annotations.Nullable;
import ic.id.IdGenerator;
import ic.id.SecureStringIdGenerator;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.StreamStorage;
import ic.text.Charset;
import ic.throwables.IOException;
import ic.throwables.UnableToParse;
import org.json.JSONObject;


abstract class FilesLoadHttpEndpoint extends HttpEndpoint {

	@Override protected String getName() { return "load"; }

	protected abstract StreamStorage getStorage();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String url 					= requestJson.getString("url");
		final @Nullable String extension 	= requestJson.optString("ext", null);

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

		try {

			storage.write(fileName, HttpClient.request(url).getBody());

		} catch (IOException e) 	{ throw new IOException.Runtime(e);
		} catch (HttpException e) 	{ throw new HttpException.Runtime(e); }

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
