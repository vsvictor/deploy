package d2.polpharma.services.ophtik.api.users


import d2.polpharma.services.ophtik.model.User
import ic.date.Millis.millisToDate
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject
import java.util.*


class ModifyUserApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "modify"

	override fun checkServerKey (serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId = requestJson.getString("id")

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
		val isSubscribedToWeather = requestJson.opt("isSubscribedToWeather") as Boolean?

		val user = try {
			User.mapper.getItemOrThrow(userId)
		} catch (noSuchUser : NotExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "NO_SUCH_USER")
			return JsonResponse.Final(responseJson)
		}

		if (phone != null) 				user.phone = phone
		if (expert != null) 			user.expert = expert
		if (confidentiality != null) 	user.confidentiality = confidentiality
		if (surname != null) 			user.surname = surname
		if (name != null) 				user.name = name
		if (birthDate != null) 			user.birthDate = birthDate
		if (city != null) 				user.city = city
		if (isSubscribedToWeather != null) user.isSubscribedToWeather = isSubscribedToWeather

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		return JsonResponse.Final(responseJson)

	}

}