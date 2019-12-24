package d2.modules.docs.api


import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.*
import ic.struct.list.List
import org.json.JSONObject


abstract class DocsApiRoute : FolderHttpRoute.Static() {

	override val name = "docs"

	protected abstract fun initApiDocs() : List<ApiCategoryDoc>

	private val apiDocs = initApiDocs()

	override fun initChildren() = List.Default<HttpRoute>(

		object : HttpEndpoint() {

			override val name = "get"

			override fun implementEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

				val responseJson = JSONObject()

				responseJson.put("status", "SUCCESS")

				responseJson.put("categories", toJsonArray(apiDocs) { categoryDoc ->
					val categoryJson = JSONObject()
					categoryJson.put("name", categoryDoc.name)
					categoryJson.put("requests", toJsonArray(categoryDoc.requests) { requestDoc ->
						val requestJson = JSONObject()
						requestJson.put("name", requestDoc.name)
						requestJson.put("url", requestDoc.url)
						requestJson.put("httpMethod", requestDoc.httpMethod)
						if (requestDoc.requestExample != null) {
							requestJson.put("requestExample", requestDoc.requestExample.toString())
						}
						requestJson.put("responseExample", requestDoc.responseExample.toString())
						requestJson
					})
					categoryJson
				})

				return object : JsonResponse() {
					override val json = responseJson
				}

			}

		}

	)

}