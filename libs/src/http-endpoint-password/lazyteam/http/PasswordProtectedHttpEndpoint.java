package lazyteam.http;

import ic.network.SocketAddress;
import ic.network.http.*;
import ic.throwables.UnableToParse;


public @Deprecated abstract class PasswordProtectedHttpEndpoint extends HttpEndpoint {

	protected abstract String getPassword();

	protected abstract HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse;

	@Override protected final HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final HttpAuth auth = request.auth;

		if (auth == null) {
			return new HttpResponse() {
				@Override public int getStatus() { return STATUS_UNAUTHORIZED; }
			};
		}

		if (auth instanceof BearerHttpAuth) { final BearerHttpAuth bearerAuth = (BearerHttpAuth) auth;

			if (bearerAuth.bearer.equals(getPassword())) {

				return implementSafeEndpoint(socketAddress, request);

			} else {

				return new HttpResponse() {
					@Override public int getStatus() { return STATUS_FORBIDDEN; }
				};

			}

		} else {

			return new HttpResponse() {
				@Override public int getStatus() { return STATUS_FORBIDDEN; }
			};

		}

	}

}
