package ic.network.http;


import java.net.HttpURLConnection;
import java.net.URL;

import ic.annotations.Overload;
import ic.struct.map.EditableMap;
import org.jetbrains.annotations.NotNull;
import ic.stream.*;
import ic.struct.map.CountableMap;
import ic.text.Charset;
import ic.throwables.IOException;
import ic.mimetypes.MimeType;


public class HttpClient {


	public static HttpResponse request(@NotNull final HttpRequest request) throws IOException, HttpException {
		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL(request.url).openConnection();
			try {
				connection.setConnectTimeout(16000);
				connection.setReadTimeout(16000);
				request.headers.getKeys().forEach(header -> connection.setRequestProperty(header, request.headers.get(header)));
				connection.setRequestMethod(request.method);
				final byte[] body = request.body.toByteArray();
				if (body.length > 0) {
					connection.setDoOutput(true);
					connection.getOutputStream().write(body);
				}
				final int responseStatus = connection.getResponseCode();
				final EditableMap<String, String> headers = new EditableMap.Default<>(); {
					connection.getHeaderFields().forEach((key, value) -> {
						if (key == null) return;
						headers.set(key, value.get(0));
					});
				}
				final ByteSequence responseBody; {
					if (responseStatus == HttpURLConnection.HTTP_OK) {
						responseBody = new ByteInput.FromInputStream(connection.getInputStream()).read();
					} else {
						responseBody = new ByteInput.FromInputStream(connection.getErrorStream()).read();
					}
				}
				final HttpResponse response = new HttpResponse() {
					@Override public int getStatus() 			{ return responseStatus; }
					@Override public MimeType getContentType() 	{ return MimeType.byName(connection.getHeaderField("Content-Type")); }
					@Override public ByteSequence getBody() 	{ return responseBody; }
					@Override public Charset getCharset() 		{ return Charset.DEFAULT_HTTP; }
					@Override public CountableMap<String, String> getHeaders() { return headers; }
				};
				if (responseStatus == HttpURLConnection.HTTP_OK) {
					return response;
				} else {
					throw new HttpException(response);
				}
			} finally {
				connection.disconnect();
			}
		} catch (java.io.IOException ioException) { throw new IOException(ioException); }
	}


	@Overload public static HttpResponse request(@NotNull String url) throws IOException, HttpException {
		return request(new HttpRequest(
			url,
			"GET",
			new CountableMap.Default<>(),
			ByteSequence.EMPTY
		));
	}


}
