package d2.polpharma.services.ophtik.api.users


import d2.polpharma.services.ophtik.model.User
import ic.date.Millis
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.Skip.SKIP
import org.json.JSONObject


class ListUsersApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "list"

	override fun checkServerKey (serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val role = requestJson.optString("role", null)
		val isAdmin = if (requestJson.has("isAdmin")) requestJson.getBoolean("isAdmin") else null
		val phone = requestJson.optString("phone", null)
		val expert = requestJson.optString("expert", null)
		val confidentiality = requestJson.optString("confidentiality", null)
		val surname = requestJson.optString("surname", null)
		val name = requestJson.optString("name", null)
		val city = requestJson.optString("city", null)
		val isSubscribedToWeather = requestJson.opt("isSubscribedToWeather") as Boolean?

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		responseJson.put("users", toJsonArray(User.mapper.items) { user ->

			if (role != null && user.role != role) 									throw SKIP
			if (isAdmin != null && user.isAdmin != isAdmin) 						throw SKIP
			if (phone != null && user.phone != phone) 								throw SKIP
			if (expert != null && user.expert != expert) 							throw SKIP
			if (confidentiality != null && user.confidentiality != confidentiality) throw SKIP
			if (surname != null && user.surname != surname) 						throw SKIP
			if (name != null && user.name != name) 									throw SKIP
			if (city != null && user.city != city) 									throw SKIP
			if (isSubscribedToWeather != null && user.isSubscribedToWeather != isSubscribedToWeather) throw SKIP

			val userJson = JSONObject()

			userJson.put("id", User.mapper.getId(user))

			userJson.put("role", user.role)
			userJson.put("isAdmin", user.isAdmin)
			userJson.put("registrationDate", Millis.dateToMillis(user.registrationDate))
			userJson.putOpt("phone", user.phone)
			userJson.putOpt("expert", user.expert)
			userJson.putOpt("confidentiality", user.confidentiality)
			userJson.putOpt("surname", user.surname)
			userJson.putOpt("name", user.name)
			userJson.putOpt("birthDate", Millis.nullableDateToMillis(user.birthDate))
			userJson.putOpt("city", user.city)
			userJson.put("isSubscribedToWeather", user.isSubscribedToWeather)

			userJson

		})

		return JsonResponse.Final(responseJson)

	}

}