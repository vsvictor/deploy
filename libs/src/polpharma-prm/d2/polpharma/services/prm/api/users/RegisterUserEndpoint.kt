package d2.polpharma.services.prm.api.users


import d2.polpharma.services.polpharmaServerKey
import d2.polpharma.services.prm.model.User
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.AccessDenied.ACCESS_DENIED
import ic.throwables.NotExists
import org.json.JSONObject


class RegisterUserEndpoint : ProtectedHttpEndpoint() {


	override val name = "register"


	override fun checkServerKey (serverKey: String) { if (serverKey != polpharmaServerKey) throw ACCESS_DENIED }

	override fun checkUserAuth (auth: BasicHttpAuth) { throw ACCESS_DENIED }


	override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val registrationCode = requestJson.getString("registrationCode")

		val userId : String; run {
			try {
				userId = User.idByRegistrationCodeAndRemove(registrationCode)
			} catch (notExists : NotExists) {
				return object : JsonResponse () {
					override val json : JSONObject get() {
						val responseJson = JSONObject()
						responseJson.put("status", "REGISTRATION_CODE_NOT_EXISTS")
						return responseJson
					}
				}
			}
		}

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
			responseJson.put("userId", userId)
		}

		return object : JsonResponse () {
			override val json = responseJson
		}

	}


}