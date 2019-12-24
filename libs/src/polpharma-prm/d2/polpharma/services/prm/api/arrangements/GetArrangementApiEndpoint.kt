package d2.polpharma.services.prm.api.arrangements


import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.Arrangement
import ic.date.Millis
import ic.json.JsonArrays
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject


class GetArrangementApiEndpoint : ProtectedHttpEndpoint() { override val name = "get"

	override fun checkServerKey(serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val arrangementId = requestJson.getString("id")

		val responseJson = JSONObject()

		try {

			val arrangement = Arrangement.mapper.getItemOrThrow(arrangementId)

			responseJson.put("status", "SUCCESS")

			synchronized (arrangement) {

				responseJson.putOpt("name", arrangement.name)
				responseJson.putOpt("startDate", Millis.nullableDateToMillis(arrangement.startDate))
				responseJson.putOpt("endDate", Millis.nullableDateToMillis(arrangement.endDate))
				responseJson.putOpt("cancelDate", Millis.nullableDateToMillis(arrangement.cancelDate))
				responseJson.putOpt("costPoints", arrangement.costPoints)
				responseJson.putOpt("place", arrangement.place)
				responseJson.putOpt("isActive", arrangement.isActive)

			}

		} catch (notExists : NotExists) {

			responseJson.put("status", "NOT_EXISTS")
			
		}

		return JsonResponse.Final(responseJson)

	}

}