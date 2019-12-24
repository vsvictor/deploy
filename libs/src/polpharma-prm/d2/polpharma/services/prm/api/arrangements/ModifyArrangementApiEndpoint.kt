package d2.polpharma.services.prm.api.arrangements


import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.Arrangement
import ic.date.Millis.millisToDate
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject


class ModifyArrangementApiEndpoint : ProtectedHttpEndpoint() { override val name = "modify"

	override fun checkServerKey(serverKey: String) = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val arrangementId = requestJson.getString("id")

		val name = requestJson.opt("name") as String?
		val startDate 	= if (requestJson.has("startDate")) 	millisToDate(requestJson.getLong("startDate")) 	else null
		val endDate 	= if (requestJson.has("endDate")) 		millisToDate(requestJson.getLong("endDate")) 	else null
		val cancelDate 	= if (requestJson.has("cancelDate")) 	millisToDate(requestJson.getLong("cancelDate")) else null
		val costPoints = requestJson.opt("costPoints") as Int?
		val place = requestJson.opt("place") as String?
		val isActive = requestJson.opt("isActive") as Boolean?

		val responseJson = JSONObject()

		try {

			val arrangement = Arrangement.mapper.getItemOrThrow(arrangementId)

			if (name != null) arrangement.name = name
			if (startDate != null) arrangement.startDate = startDate
			if (endDate != null) arrangement.endDate = endDate
			if (cancelDate != null) arrangement.cancelDate = cancelDate
			if (costPoints != null) arrangement.costPoints = costPoints
			if (place != null) arrangement.place = place
			if (isActive != null) arrangement.isActive = isActive

			responseJson.put("status", "SUCCESS")

		} catch (notExists : NotExists) {

			responseJson.put("status", "NOT_EXISTS")

		}

		return JsonResponse.Final(responseJson)

	}

}