package ic.network.http;


@SuppressWarnings("serial")


public class HttpException extends Exception {

	public final HttpResponse response;

	public HttpException(HttpResponse response) {
		super(response.getStatusMessage() + " " + response.getCharset().bytesToString(response.getBody()));
		this.response = response;
	}

	public HttpException(HttpResponse response, Throwable cause) {
		super(response.getStatusMessage() + " " + response.getCharset().bytesToString(response.getBody()), cause);
		this.response = response;
	}

	public static class Runtime extends RuntimeException {

		public final HttpResponse response;

		public Runtime(HttpResponse response) {
			super(response.getCharset().bytesToString(response.getBody()));
			this.response = response;
		}

		public Runtime(HttpException httpException) {
			super(
				httpException.response.getCharset().bytesToString(httpException.response.getBody()),
				httpException
			);
			this.response = httpException.response;
		}

	}

}
