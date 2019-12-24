package d2.modules.admins.api


import d2.modules.admins.model.Admin
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.*
import ic.storage.Storage
import ic.throwables.AccessDenied.ACCESS_DENIED
import org.json.JSONObject


internal abstract class ListAdminsApiEndpoint : ProtectedHttpEndpoint() { override val name = "list"

	override fun checkServerKey (serverKey: String) = throw ACCESS_DENIED

	abstract val storage : Storage

	abstract fun checkSuperadminAuth (auth: BasicHttpAuth)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		responseJson.put("admins", toJsonArray(storage.keys) { username ->

			val adminJson = JSONObject()

			adminJson.put("username", username)

			val admin : Admin = storage[username]

			adminJson.put("profile", admin.profile)

			adminJson

		})

		return JsonResponse.Final(responseJson)

	}

}