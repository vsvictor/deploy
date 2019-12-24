package ic.network.http;


import ic.network.SocketAddress;
import ic.throwables.AccessDenied;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import ic.throwables.WrongValue;
import org.jetbrains.annotations.NotNull;


public abstract class ProtectedHttpEndpoint extends HttpEndpoint {


	protected abstract void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied;

	protected abstract void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied;


	protected abstract HttpResponse implementSafeEndpoint(@NotNull SocketAddress socketAddress, @NotNull HttpRequest request) throws UnableToParse;


	@Override protected final HttpResponse implementEndpoint(@NotNull SocketAddress socketAddress, @NotNull HttpRequest request) throws UnableToParse {

		final HttpAuth auth = request.auth;

		if (auth == null) {
			return new HttpResponse() {
				@Override public int getStatus() { return STATUS_UNAUTHORIZED; }
			};
		}

		if (auth instanceof BearerHttpAuth) { final BearerHttpAuth bearerAuth = (BearerHttpAuth) auth;

			try {

				checkServerKey(bearerAuth.bearer);

			} catch (WrongValue | AccessDenied accessDenied) {
				return new HttpResponse() {
					@Override public int getStatus() { return STATUS_FORBIDDEN; }
				};
			}

			return implementSafeEndpoint(socketAddress, request);

		} else

		if (auth instanceof BasicHttpAuth) { final BasicHttpAuth basicAuth = (BasicHttpAuth) auth;

			try {

				checkUserAuth(basicAuth);

			} catch (NotExists | WrongValue | AccessDenied accessDenied) {
				return new HttpResponse() {
					@Override public int getStatus() { return STATUS_FORBIDDEN; }
				};
			}

			return implementSafeEndpoint(socketAddress, request);

		}

		else {

			return new HttpResponse() {
				@Override public int getStatus() { return STATUS_FORBIDDEN; }
			};

		}

	}

}
