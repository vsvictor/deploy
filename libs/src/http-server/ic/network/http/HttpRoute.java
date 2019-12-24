package ic.network.http;



import ic.annotations.DoNotCall;
import ic.annotations.ToOverride;
import ic.network.SocketAddress;
import ic.throwables.NotSupported;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;


/**
 * HttpRoute get a listener for HTTP requests
 */
public abstract class HttpRoute {

	@DoNotCall @ToOverride
	public abstract HttpResponse implementRoute(

		@NotNull SocketAddress socketAddress,
		@NotNull HttpRequest request,
		boolean root

	) throws NotSupported, UnableToParse;

}
