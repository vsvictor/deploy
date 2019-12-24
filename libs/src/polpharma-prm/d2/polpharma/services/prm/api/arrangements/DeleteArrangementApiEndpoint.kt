package d2.polpharma.services.prm.api.arrangements


import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.Arrangement
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject


class DeleteArrangementApiEndpoint : ProtectedHttpEndpoint() { override val name = "delete"

	override fun checkServerKey(serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val arrangementId = requestJson.getString("id")

		val responseJson = JSONObject()

		try {

			Arrangement.mapper.removeOrThrow(arrangementId)

			responseJson.put("status", "SUCCESS")

		} catch (notExists : NotExists) {

			responseJson.put("status", "NOT_EXISTS")

		}

		return JsonResponse.Final(responseJson)

	}

}