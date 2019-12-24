package d2.modules.points.api

import d2.modules.points.model.getPoints
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.Storage
import ic.text.Charset
import org.json.JSONObject


abstract class GetPointsApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "get"

	abstract val storage : Storage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId = requestJson.getString("userId")

		val points = getPoints(storage, userId)

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
			responseJson.put("points", points)
		}

		return object : JsonResponse() {
			override val json = responseJson
		}

	}

}