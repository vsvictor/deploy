package ic.network.http


import ic.network.SocketAddress
import ic.throwables.NotSupported
import ic.throwables.UnableToParse


/**
 * Request handler designed for API.
 */
abstract class HttpEndpoint : HttpRoute() {


	protected abstract val name: String?


	@Throws(UnableToParse::class)
	protected abstract fun implementEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse


	@Throws(UnableToParse::class, NotSupported::class)
	override fun implementRoute(socketAddress: SocketAddress, request: HttpRequest, root: Boolean): HttpResponse {

		if (!root) {

			val name = name

			if (name == null) {
				if (!request.path.isEmpty()) throw NotSupported()
			} else {
				if (request.path != name) throw NotSupported()
			}

		}

		return implementEndpoint(socketAddress, request)

	}


}
