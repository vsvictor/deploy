package d2.polpharma.services.prm.api.arrangements


import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.Arrangement
import ic.date.Millis.millisToDate
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import org.json.JSONObject


class CreateArrangementApiEndpoint : ProtectedHttpEndpoint() { override val name = "create"

	override fun checkServerKey(serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val name = requestJson.opt("name") as String?
		val startDate 	= if (requestJson.has("startDate")) 	millisToDate(requestJson.getLong("startDate")) 	else null
		val endDate 	= if (requestJson.has("endDate")) 		millisToDate(requestJson.getLong("endDate")) 	else null
		val cancelDate 	= if (requestJson.has("cancelDate")) 	millisToDate(requestJson.getLong("cancelDate")) else null
		val costPoints = requestJson.opt("costPoints") as Int?
		val place = requestJson.opt("place") as String?
		val isActive = requestJson.optBoolean("isActive", false)

		val arrangement = Arrangement(name, startDate, endDate, cancelDate, costPoints, place, isActive)

		val arrangementId = Arrangement.mapper.getId(arrangement)

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
			responseJson.put("id", arrangementId)
		}

		return JsonResponse.Final(responseJson)

	}

}