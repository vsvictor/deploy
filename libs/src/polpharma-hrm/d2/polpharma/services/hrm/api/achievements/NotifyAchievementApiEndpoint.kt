package d2.polpharma.services.hrm.api.achievements


import d2.polpharma.services.hrm.model.pushAchievement
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import org.json.JSONObject


class NotifyAchievementApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "notify"

	override fun checkServerKey(serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))
		val userId = requestJson.getString("userId")
		val achievementType = requestJson.getString("type")
		val toStack = requestJson.optBoolean("toStack", false)
		val notificationCategory = requestJson.optString("notificationCategory", null)

		pushAchievement(userId, achievementType, toStack, notificationCategory)

		val responseJson = JSONObject()
		responseJson.put("status", "SUCCESS")
		return JsonResponse.Final(responseJson)

	}

}