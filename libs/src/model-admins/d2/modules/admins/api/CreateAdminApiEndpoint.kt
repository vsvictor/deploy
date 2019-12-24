package d2.modules.admins.api


import d2.modules.admins.model.Admin
import ic.network.SocketAddress
import ic.network.http.*
import ic.storage.Storage
import ic.text.Charset
import ic.throwables.AccessDenied.ACCESS_DENIED
import org.json.JSONObject


internal abstract class CreateAdminApiEndpoint : ProtectedHttpEndpoint() { override val name = "create"

	override fun checkServerKey (serverKey: String) = throw ACCESS_DENIED

	abstract fun checkSuperadminAuth (auth: BasicHttpAuth)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkSuperadminAuth(auth)

	abstract val storage : Storage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val adminUsername = requestJson.getString("username")
		val adminPassword = requestJson.getString("password")

		val adminProfile = if (requestJson.has("profile")) requestJson.getJSONObject("profile") else JSONObject()

		val responseJson = JSONObject()

		synchronized (storage) {
			if (storage.contains(adminUsername)) {
				responseJson.put("status", "ALREADY_EXISTS")
			} else {
				responseJson.put("status", "SUCCESS")
				storage.set(adminUsername, Admin(adminPassword, adminProfile))
			}
		}

		return JsonResponse.Final(responseJson)

	}

}