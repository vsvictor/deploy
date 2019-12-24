package ic.network.http


import ic.network.SocketAddress
import ic.network.http.HttpResponse.Companion.STATUS_MOVED_PERMANENTLY
import ic.struct.list.List
import ic.struct.map.CountableMap
import ic.throwables.NotSupported
import ic.throwables.UnableToParse


/**
 * FolderHttpRoute implementing hierarchy of URL path like "folder/child-folder/child-of-child".
 * Represents "folder" with child HttpRoute's
 */
abstract class FolderHttpRoute : HttpRoute() {


	protected abstract val name: String

	protected abstract val children : List<HttpRoute>


	@Throws(UnableToParse::class, NotSupported::class)
	override fun implementRoute(socketAddress: SocketAddress, request: HttpRequest, root: Boolean): HttpResponse {

		val startsWith = if (name.isEmpty()) "" else "$name/"

		val childPath: String; run {
			if (root) {
				childPath = request.path
			} else {
				if (name.isNotEmpty() && request.path == name) {
					return object : HttpResponse {
						override val status = STATUS_MOVED_PERMANENTLY
						override val headers = CountableMap.Default<String, String>(
							"Location", "$name/"
						)
					}
				}
				if (!request.path.startsWith(startsWith)) throw NotSupported()
				childPath = request.path.substring(startsWith.length)
			}
		}

		for (child in children) {
			try {
				return child.implementRoute(
					socketAddress,
					HttpRequest(
						request.url,
						childPath,
						request.urlParams,
						request.method,
						request.headers,
						request.body
					),
					false
				)
			} catch (notSupported: NotSupported) {}
		}; throw NotSupported()

	}


	abstract class Static : FolderHttpRoute() {

		protected abstract fun initChildren() : List<HttpRoute>
		override val children = initChildren()

	}


	class Final (override val name: String, override val children: List<HttpRoute>) : FolderHttpRoute() {

		constructor (children: List<HttpRoute>) : this("", children)

		constructor (vararg children: HttpRoute) : this("", List.Default<HttpRoute>(*children))

	}


}