package d2.polpharma.services.ophtik.api.users


import d2.polpharma.services.ophtik.model.User
import ic.network.SocketAddress
import ic.network.http.*
import ic.struct.collection.FilterCollection
import ic.text.Charset
import org.json.JSONObject


class CountUsersApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "count"

	override fun checkServerKey (serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	@Suppress("RedundantIf")
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

		responseJson.put("usersCount", FilterCollection(User.mapper.items) { user ->

			if (role != null && user.role != role) false else
			if (isAdmin != null && user.isAdmin != isAdmin) false else
			if (phone != null && user.phone != phone) false else
			if (expert != null && user.expert != expert) false else
			if (confidentiality != null && user.confidentiality != confidentiality) false else
			if (surname != null && user.surname != surname) false else
			if (name != null && user.name != name) false else
			if (city != null && user.city != city) false else
			if (isSubscribedToWeather != null && user.isSubscribedToWeather != isSubscribedToWeather) false

			else true

		}.count)

		return JsonResponse.Final(responseJson)

	}

}