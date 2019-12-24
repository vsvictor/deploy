package d2.modules.admins.api


import d2.modules.admins.checkAdminAuth
import d2.modules.admins.model.Admin
import ic.network.SocketAddress
import ic.network.http.*
import ic.storage.Storage
import ic.text.Charset
import ic.throwables.AccessDenied.ACCESS_DENIED
import ic.throwables.NotExists
import org.json.JSONObject


internal abstract class SetAdminPasswordApiEndpoint : ProtectedHttpEndpoint() { override val name = "set-password"

	override fun checkServerKey (serverKey: String) = throw ACCESS_DENIED

	abstract val storage : Storage

	abstract fun checkSuperadminAuth (auth: BasicHttpAuth)

	override fun checkUserAuth (auth: BasicHttpAuth) = checkAdminAuth(
		{ checkSuperadminAuth(it) },
		storage,
		auth
	)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))
		val auth = request.auth as BasicHttpAuth

		val adminUsername = requestJson.getString("username")
		val adminPassword = requestJson.getString("password")

		val responseJson = JSONObject()

		synchronized (storage) {
			try {
				checkSuperadminAuth(auth)
				try {
					val admin : Admin = storage.getOrThrow(adminUsername)
					admin.password = adminPassword
					responseJson.put("status", "SUCCESS")
				} catch (notExists : NotExists) {
					responseJson.put("status", "NOT_EXISTS")
				}
			} catch (notSuperadmin : NotExists) {
				if (adminUsername == auth.username) {
					val admin : Admin = storage.getNotNull(adminUsername)
					admin.password = adminPassword
					responseJson.put("status", "SUCCESS")
				} else {
					responseJson.put("status", "ACCESS_DENIED")
				}
			}
		}

		return JsonResponse.Final(responseJson)

	}

}