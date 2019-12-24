package d2.modules.points.api


import d2.modules.points.model.addPoints
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.Storage
import ic.text.Charset
import ic.throwables.NotEnough
import org.json.JSONObject


abstract class AddPointsApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "add"

	abstract val storage : Storage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId = requestJson.getString("userId")
		val points = requestJson.getInt("points")
		val creditLimit = requestJson.optInt("creditLimit", 0)

		val responseJson = JSONObject()

		try {
			addPoints(storage, userId, points, creditLimit)
			responseJson.put("status", "SUCCESS")
		} catch (notEnough : NotEnough) {
			responseJson.put("status", "NOT_ENOUGH")
		}

		return object : JsonResponse() {
			override val json = responseJson
		}

	}

}