package d2.modules.admins.api


import ic.network.SocketAddress
import ic.network.http.*
import ic.storage.Storage
import ic.text.Charset
import ic.throwables.AccessDenied.ACCESS_DENIED
import ic.throwables.NotExists
import org.json.JSONObject


internal abstract class DeleteAdminApiEndpoint : ProtectedHttpEndpoint() { override val name = "delete"

	override fun checkServerKey (serverKey: String) = throw ACCESS_DENIED

	abstract fun checkSuperadminAuth (auth: BasicHttpAuth)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkSuperadminAuth(auth)

	abstract val storage : Storage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val adminUsername = requestJson.getString("username")

		val responseJson = JSONObject()

		try {
			storage.removeOrThrow(adminUsername)
			responseJson.put("status", "SUCCESS")
		} catch (notExists : NotExists) {
			responseJson.put("status", "NOT_EXISTS")
		}

		return JsonResponse.Final(responseJson)

	}

}