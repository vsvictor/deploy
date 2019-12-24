package ic.http.proxy


import ic.network.SocketAddress
import ic.network.http.*


class ProxyRoute : HttpRoute() {

	override fun implementRoute (socketAddress: SocketAddress, request: HttpRequest, root: Boolean) : HttpResponse {

		try {
			return HttpClient.request(
				HttpRequest(
					request.path,
					request.method,
					request.headers,
					request.body
				)
			)
		} catch (httpException : HttpException) {
			return httpException.response
		}

	}

}