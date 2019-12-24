package d2.polpharma.services.hrm.api.users


import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.checkPolpharmaSuperadminAuth
import d2.polpharma.services.hrm.model.User
import ic.date.Millis.millisToDate
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import org.json.JSONObject
import java.util.*


class CreateUserApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "create"

	override fun checkServerKey (serverKey: String) = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val role = requestJson.getString("role")
		val phone = requestJson.optString("phone", null)
		val surname = requestJson.optString("surname", null)
		val name = requestJson.optString("name", null)
		val birthDate : Date?; run {
			val birthDateMillis = requestJson.optLong("birthDate", Long.MIN_VALUE)
			birthDate = if (birthDateMillis == Long.MIN_VALUE) null else millisToDate(birthDateMillis)
		}
		val city = requestJson.optString("city", null)
		val line = requestJson.optString("line", null)

		val user = User(role, phone, surname, name, birthDate, city, line)

		val userId = User.mapper.getId(user)

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
			responseJson.put("id", userId)
		}

		return JsonResponse.Final(responseJson)

	}

}