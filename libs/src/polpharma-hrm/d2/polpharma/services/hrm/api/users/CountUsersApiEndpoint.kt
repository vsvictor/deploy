package d2.polpharma.services.hrm.api.users


import d2.polpharma.services.hrm.model.User
import ic.network.SocketAddress
import ic.network.http.*
import org.json.JSONObject


class CountUsersApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "count"

	override fun checkServerKey (serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		responseJson.put("usersCount", User.mapper.ids.count)

		return JsonResponse.Final(responseJson)

	}

}