package d2.polpharma.services.hrm.api


import d2.modules.events.model.Event
import d2.modules.events.model.StatisticsEvent
import d2.polpharma.services.hrm.*
import d2.polpharma.services.hrm.model.User
import ic.date.Millis
import ic.json.JsonArrays.jsonArrayToList
import ic.network.SocketAddress
import ic.network.http.HttpEndpoint
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.serial.json.JsonSerializer
import ic.storage.Directory
import ic.storage.JsonStorage
import ic.text.Charset
import org.json.JSONObject
import java.util.*


class Migrate : HttpEndpoint() {

	override val name = "migrate"

	override fun implementEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		/*polpharmaHrmEducationStorage.empty()
		val ed = JsonStorage(Directory.createIfNotExists("/root/migrate/education"))
		ed.keys.forEach { key ->
			polpharmaHrmEducationStorage.set(key, ed.get(key))
		}

		polpharmaHrmSurveyStorage.empty()
		val su = JsonStorage(Directory.createIfNotExists("/root/migrate/survey"))
		su.keys.forEach { key ->
			polpharmaHrmSurveyStorage.set(key, su.get(key))
		}*/

		polpharmaHrmUsersStorage.empty()
		jsonArrayToList<Any, JSONObject>(requestJson.getJSONArray("users")) {
			val role = it.getString("role")
			val phone = it.optString("phone", null)
			val surname = it.optString("surname", null)
			val name = it.optString("name", null)
			val birthDate : Date?; run {
			val birthDateMillis = it.optLong("birthDate", Long.MIN_VALUE)
				birthDate = if (birthDateMillis == Long.MIN_VALUE) null else Millis.millisToDate(birthDateMillis)
			}
			val city = it.optString("city", null)
			val line = it.optString("line", null)
			val user = User(role, phone, surname, name, birthDate, city, line)
			polpharmaHrmUsersStorage.set(it.getString("id"), user)
			User.mapper.id(user)
		}

		/*polpharmaHrmAchievementsStorage.empty()
		val achievementsJson = requestJson.getJSONObject("achievements")
		achievementsJson.keySet().forEach {
			val achievementJson = achievementsJson.getJSONObject(it)
			polpharmaHrmAchievementsStorage.set(it, JsonSerializer.parse(achievementJson))
		}

		polpharmaHrmEventsStorage.empty()
		jsonArrayToList<Any, JSONObject>(requestJson.getJSONArray("events")) {
			Event.getMapper(polpharmaHrmEventsStorage).getId(
				StatisticsEvent(it)
			)
		}*/

		return JsonResponse.Final(JSONObject())

	}

}