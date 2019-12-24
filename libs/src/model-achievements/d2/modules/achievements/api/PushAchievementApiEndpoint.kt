package d2.modules.achievements.api


import d2.modules.achievements.model.pushAchievement
import ic.network.SocketAddress
import ic.network.http.*
import ic.storage.Storage
import ic.text.Charset
import ic.throwables.AlreadyExists
import org.json.JSONObject


abstract class PushAchievementApiEndpoint : ProtectedHttpEndpoint() { override val name get() = "push"

	abstract val storage : Storage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId 			= requestJson["userId"] 			as String
		val achievementName = requestJson["achievementName"] 	as String
		val toStack 		= requestJson["toStack"] 			as Boolean

		val responseJson = JSONObject()

		try {
			pushAchievement(
				storage 		= storage,
				userId 			= userId,
				achievementName = achievementName,
				toStack 		= toStack
			) {}
			responseJson.put("status", "SUCCESS")
		} catch (alreadyExists : AlreadyExists) {
			responseJson.put("status", "ALREADY_EXISTS")
		}

		return JsonResponse.Final(responseJson)

	}

}