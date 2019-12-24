package d2.polpharma.services.prm.api.arrangements


import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.Arrangement
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.AlreadyExists
import ic.throwables.NotExists
import org.json.JSONObject


class AddArrangementUserApiEndpoint : ProtectedHttpEndpoint() { override val name = "add-user"

	override fun checkServerKey(serverKey: String) = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val arrangementId = requestJson.getString("arrangementId")
		val userId = requestJson.getString("userId")

		val arrangement = try {
			Arrangement.mapper.getItemOrThrow(arrangementId)
		} catch (notExists : NotExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "ARRANGEMENT_NOT_EXISTS")
			return JsonResponse.Final(responseJson)
		}

		try {
			arrangement.addArrangementUser(userId)
		} catch (alreadyExists: AlreadyExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "ARRANGEMENT_USER_ALREADY_EXISTS")
			return JsonResponse.Final(responseJson)
		}

		val responseJson = JSONObject()
		responseJson.put("status", "SUCCESS")
		return JsonResponse.Final(responseJson)

	}

}