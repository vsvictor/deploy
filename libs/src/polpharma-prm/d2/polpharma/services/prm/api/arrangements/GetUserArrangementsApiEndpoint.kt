package d2.polpharma.services.prm.api.arrangements


import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.Arrangement
import ic.date.Millis
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import org.json.JSONObject


class GetUserArrangementsApiEndpoint : ProtectedHttpEndpoint() { override val name = "user"

	override fun checkServerKey(serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId = requestJson.getString("userId")

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		responseJson.put("arrangements", toJsonArray(Arrangement.mapper.ids) { arrangementId ->

			val arrangementJson = JSONObject()
			arrangementJson.put("id", arrangementId)

			val arrangement = Arrangement.mapper.getItemOrThrow(arrangementId)

			synchronized (arrangement) {

				arrangementJson.putOpt("name", arrangement.name)
				arrangementJson.putOpt("startDate", Millis.nullableDateToMillis(arrangement.startDate))
				arrangementJson.putOpt("endDate", Millis.nullableDateToMillis(arrangement.endDate))
				arrangementJson.putOpt("cancelDate", Millis.nullableDateToMillis(arrangement.cancelDate))
				arrangementJson.putOpt("costPoints", arrangement.costPoints)
				arrangementJson.putOpt("place", arrangement.place)
				arrangementJson.putOpt("isActive", arrangement.isActive)

				run {
					val state = arrangement.users.get(userId)
					if (state != null) {
						arrangementJson.put("state", state.name)
					}
				}

			}

			arrangementJson

		})

		return JsonResponse.Final(responseJson)

	}

}