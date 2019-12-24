package d2.polpharma.services.prm.api.arrangements


import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.Arrangement
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject


class GetArrangementUsersApiEndpoint : ProtectedHttpEndpoint() { override val name = "users"

	override fun checkServerKey(serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val arrangementId = requestJson.getString("arrangementId")

		val arrangement = try {
			Arrangement.mapper.getItemOrThrow(arrangementId)
		} catch (notExists : NotExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "NOT_EXISTS")
			return JsonResponse.Final(responseJson)
		}

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		val arrangementUsers = arrangement.users

		responseJson.put("arrangementUsers", toJsonArray(arrangementUsers.keys) { userId ->
			val arrangementUserJson = JSONObject()
			arrangementUserJson.put("userId", userId)
			arrangementUserJson.put("state", arrangementUsers.getNotNull(userId).name)
			arrangementUserJson
		})

		return JsonResponse.Final(responseJson)

	}

}