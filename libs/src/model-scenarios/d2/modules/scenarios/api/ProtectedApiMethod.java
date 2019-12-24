package d2.modules.scenarios.api;


import ic.network.SocketAddress;
import ic.network.http.*;
import ic.stream.ByteSequence;
import ic.throwables.UnableToParse;


public @Deprecated abstract class ProtectedApiMethod extends HttpEndpoint {

	protected abstract String getBearer();

	protected abstract HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse;

	@Override protected final HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		if (request.auth instanceof BearerHttpAuth) { final BearerHttpAuth bearerAuth = (BearerHttpAuth) request.auth;

			if (!bearerAuth.bearer.equals(getBearer())) return new HttpResponse() {
				@Override public int getStatus() { return STATUS_FORBIDDEN; }
				@Override public ByteSequence getBody() { return new ByteSequence.FromByteArray(new byte[] {}); }
			};

		} else return new HttpResponse() {
			@Override public int getStatus() { return STATUS_UNAUTHORIZED; }
			@Override public ByteSequence getBody() { return new ByteSequence.FromByteArray(new byte[] {}); }
		};

		return implementProtectedApi(socketAddress, request);

	}


}
