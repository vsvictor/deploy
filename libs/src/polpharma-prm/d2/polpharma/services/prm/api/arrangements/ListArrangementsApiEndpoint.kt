package d2.polpharma.services.prm.api.arrangements


import d2.modules.points.model.getPoints
import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.Arrangement
import d2.polpharma.services.prm.model.User
import d2.polpharma.services.prm.polpharmaPrmPointsStorage
import ic.date.Millis
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.NotExists
import ic.throwables.Skip.SKIP
import org.json.JSONObject


class ListArrangementsApiEndpoint : ProtectedHttpEndpoint() { override val name = "list"

	override fun checkServerKey(serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val userId = if (request.method == "GET") null else {
			val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))
			requestJson.getString("userId")
		}

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

				if (userId != null) {
					val state = arrangement.users.get(userId)
					if (state != null) {
						arrangementJson.putOpt("userState", state.name)
					}
				}

				if (request.auth is BasicHttpAuth) {

					val arrangementUsers = arrangement.users
					arrangementJson.put("arrangementUsers", toJsonArray(arrangementUsers.keys) { userId ->

						val arrangementUserJson = JSONObject()

						val user = try {
							User.mapper.getItemOrThrow(userId)
						} catch (notExists : NotExists) { throw SKIP }
						val userJson = JSONObject()
						userJson.put("id", userId)
						userJson.put("quadrasoftId", user.quadrasoftId)
						userJson.put("points", getPoints(polpharmaPrmPointsStorage, userId))
						arrangementUserJson.put("user", userJson)

						arrangementUserJson.put("state", arrangementUsers.getNotNull(userId).name)
						arrangementUserJson

					})

				}

			}

			arrangementJson

		})

		return JsonResponse.Final(responseJson)

	}

}