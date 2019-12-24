package d2.polpharma.services.hrm.api.achievements

import d2.polpharma.services.hrm.model.getAchievements
import ic.json.JsonArrays
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import org.json.JSONObject


class ListAchievementsApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "list"

	override fun checkServerKey(serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId = requestJson.getString("userId")

		val achievements = getAchievements(userId)

		val responseJson = JSONObject()
		responseJson.put("status", "SUCCESS")

		responseJson.put("achievements", JsonArrays.toJsonArray<String, JSONObject>(achievements.keys) { achievement ->
			val json = JSONObject()
			json.put("type", achievement)
			json.put("count", achievements.get(achievement))
			json
		})

		return JsonResponse.Final(responseJson)

	}

}