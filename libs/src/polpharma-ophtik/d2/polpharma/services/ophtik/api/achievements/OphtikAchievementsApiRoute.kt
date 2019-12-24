package d2.polpharma.services.ophtik.api.achievements


import d2.modules.achievements.api.AchievementsApiRoute
import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.checkPolpharmaSuperadminAuth
import d2.polpharma.services.ophtik.model.User
import d2.polpharma.services.ophtik.ophtikAchievementsStorage
import ic.network.http.BasicHttpAuth
import ic.network.http.HttpClient
import ic.network.http.HttpRequest
import ic.text.Charset
import org.json.JSONObject


class OphtikAchievementsApiRoute : AchievementsApiRoute() { override val name: String get() = "achievements"

	override fun checkServerKey(serverKey: String) = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)

	override val storage get() = ophtikAchievementsStorage

	override val userIds get() = User.mapper.ids

	override fun notifyAchievement (userId: String, achievementName: String) {

		HttpClient.request(HttpRequest(
			"https://www.corezoid.com/api/1/json/public/571207/48349f6cbe743c8239d6b22b99c5437787b4de43",
			"POST",
			run {
				val requestJson = JSONObject()
				requestJson.put("userId", userId)
				requestJson.put("achievementName", achievementName)
				Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
			}
		))

	}

}