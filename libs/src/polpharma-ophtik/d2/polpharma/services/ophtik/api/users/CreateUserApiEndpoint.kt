package d2.polpharma.services.ophtik.api.users


import d2.polpharma.services.ophtik.model.User
import ic.date.Millis.millisToDate
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import org.json.JSONObject
import java.util.*


class CreateUserApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "create"

	override fun checkServerKey (serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val role = requestJson.getString("role")
		val isAdmin = requestJson.optBoolean("isAdmin")
		val phone = requestJson.optString("phone", null)
		val expert = requestJson.optString("expert", null)
		val confidentiality = requestJson.optString("confidentiality", null)
		val surname = requestJson.optString("surname", null)
		val name = requestJson.optString("name", null)
		val birthDate : Date?; run {
			val birthDateMillis = requestJson.optLong("birthDate", Long.MIN_VALUE)
			birthDate = if (birthDateMillis == Long.MIN_VALUE) null else millisToDate(birthDateMillis)
		}
		val city = requestJson.optString("city", null)
		val isSubscribedToWeather = requestJson.optBoolean("isSubscribedToWeather")

		val user = User(role, isAdmin, phone, expert, confidentiality, surname, name, birthDate, city, isSubscribedToWeather)

		val userId = User.mapper.getId(user)

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
			responseJson.put("id", userId)
		}

		return JsonResponse.Final(responseJson)

	}

}