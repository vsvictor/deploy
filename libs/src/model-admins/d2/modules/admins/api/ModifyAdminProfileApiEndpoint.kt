package d2.modules.admins.api


import d2.modules.admins.model.Admin
import ic.network.SocketAddress
import ic.network.http.*
import ic.storage.Storage
import ic.text.Charset
import ic.throwables.AccessDenied
import ic.throwables.NotExists
import org.json.JSONObject


abstract class ModifyAdminProfileApiEndpoint : ProtectedHttpEndpoint() { override val name = "modify-profile"

	override fun checkServerKey (serverKey: String) = throw AccessDenied.ACCESS_DENIED

	abstract val storage : Storage

	abstract fun checkSuperadminAuth (auth: BasicHttpAuth)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val adminUsername = requestJson.getString("username")
		val profileChanges = requestJson.getJSONObject("profile")

		val admin : Admin = try {
			storage.getOrThrow(adminUsername)
		} catch (notExists : NotExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "NOT_EXISTS")
			return JsonResponse.Final(responseJson)
		}

		val profile = admin.profile
		profileChanges.keySet().forEach {
			profile.put(it, profileChanges.get(it))
		}
		admin.profile = profile

		run {
			val responseJson = JSONObject()
			responseJson.put("status", "SUCCESS")
			return JsonResponse.Final(responseJson)
		}

	}

}