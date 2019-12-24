package d2.polpharma.services.ophtik.api.users


import d2.polpharma.services.ophtik.model.User
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject


class DeleteUserApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "delete"

	override fun checkServerKey (serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId = requestJson.getString("id")

		try {
			User.mapper.removeOrThrow(userId)
		} catch (noSuchUser : NotExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "NO_SUCH_USER")
			return JsonResponse.Final(responseJson)
		}

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}
		return JsonResponse.Final(responseJson)

	}

}