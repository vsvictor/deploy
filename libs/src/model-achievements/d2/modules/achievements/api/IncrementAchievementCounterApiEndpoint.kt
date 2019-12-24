package d2.modules.achievements.api


import d2.modules.achievements.model.incrementAchievementCounter
import ic.json.JsonArrays.jsonArrayToList
import ic.network.SocketAddress
import ic.network.http.*
import ic.storage.Storage
import ic.text.Charset
import org.json.JSONArray
import org.json.JSONObject


abstract class IncrementAchievementCounterApiEndpoint : ProtectedHttpEndpoint() {

	override val name get() = "increment_achievement_counter"

	abstract val storage : Storage

	abstract fun notifyAchievement (userId: String, achievementName: String)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId 		= requestJson["userId"] 		as String
		val counterName = requestJson["counterName"] 	as String
		val thresholds	= jsonArrayToList<Int, Int>(requestJson["thresholds"] as JSONArray) { it }

		incrementAchievementCounter(
			storage 	= storage,
			counterName = counterName,
			userId 		= userId,
			thresholds 	= thresholds
		) { achievementName -> notifyAchievement(userId, achievementName) }

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		return JsonResponse.Final(responseJson)

	}

}