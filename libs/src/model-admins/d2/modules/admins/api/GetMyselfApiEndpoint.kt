package d2.modules.admins.api


import d2.modules.admins.checkAdminAuth
import d2.modules.admins.model.Admin
import ic.network.SocketAddress
import ic.network.http.*
import ic.storage.Storage
import ic.throwables.AccessDenied.ACCESS_DENIED
import ic.throwables.NotExists
import org.json.JSONObject


internal abstract class GetMyselfApiEndpoint : ProtectedHttpEndpoint() { override val name = "me"

	override fun checkServerKey (serverKey: String) = throw ACCESS_DENIED

	abstract val storage : Storage

	abstract fun checkSuperadminAuth (auth: BasicHttpAuth)

	override fun checkUserAuth (auth: BasicHttpAuth) = checkAdminAuth(
		{ checkSuperadminAuth(it) },
		storage,
		auth
	)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val auth = request.auth as BasicHttpAuth

		try {

			checkSuperadminAuth(auth)

			val responseJson = JSONObject()
			responseJson.put("status", "SUPERADMIN")
			return JsonResponse.Final(responseJson)

		} catch (notASuperadmin : NotExists) {

			val admin : Admin = storage[auth.username]

			val responseJson = JSONObject(); run {
				responseJson.put("status", "ADMIN")
				responseJson.put("profile", admin.profile)
			}

			return JsonResponse.Final(responseJson)

		}

	}

}