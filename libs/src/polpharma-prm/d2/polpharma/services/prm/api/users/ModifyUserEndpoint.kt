package d2.polpharma.services.prm.api.users


import d2.polpharma.services.prm.model.User
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject


class ModifyUserEndpoint : ProtectedHttpEndpoint() { override val name = "modify"

	override fun checkServerKey (serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId = requestJson.getString("id")
		val email = requestJson.optString("email", null)

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

		if (email != null) user.email = email

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		return object : JsonResponse() { override val json = responseJson }

	}


}