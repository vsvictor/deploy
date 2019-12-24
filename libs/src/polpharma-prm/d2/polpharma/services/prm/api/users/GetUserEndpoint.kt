package d2.polpharma.services.prm.api.users


import d2.modules.points.model.getPoints
import d2.polpharma.services.polpharmaServerKey
import d2.polpharma.services.prm.model.User
import d2.polpharma.services.prm.polpharmaPrmPointsStorage
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.AccessDenied.ACCESS_DENIED
import ic.throwables.NotExists
import ic.throwables.WrongValue.WRONG_VALUE
import org.json.JSONObject


class GetUserEndpoint : ProtectedHttpEndpoint() {


	override val name = "get"

	override fun checkServerKey (serverKey: String) {
		if (serverKey != polpharmaServerKey) throw WRONG_VALUE
	}

	override fun checkUserAuth (auth: BasicHttpAuth) { throw ACCESS_DENIED }


	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId = requestJson.getString("id")

		val user = try {

			User.mapper.getItemOrThrow(userId)

		} catch (notExists : NotExists) {

			return object : JsonResponse() {
				override val json : JSONObject get() {
					val responseJson = JSONObject()
					responseJson.put("status", "NOT_EXISTS")
					return responseJson
				}
			}

		}


		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
			responseJson.put("newPoints", user.getNewPoints())
			responseJson.put("points", getPoints(polpharmaPrmPointsStorage, userId))
			responseJson.putOpt("email", user.email)
			responseJson.put("quadrasoftId", user.quadrasoftId)
		}

		return object : JsonResponse() {
			override val json = responseJson
		}

	}


}