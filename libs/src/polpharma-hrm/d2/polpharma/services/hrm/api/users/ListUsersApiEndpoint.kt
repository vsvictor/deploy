package d2.polpharma.services.hrm.api.users


import d2.polpharma.services.hrm.model.User
import ic.date.Millis
import ic.date.Millis.dateToMillis
import ic.date.Millis.nullableDateToMillis
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

		val role : String?
		val phone : String?
		val surname : String?
		val name : String?
		val city : String?
		val line : String?; run {
			if (request.method == "GET") {
				role = null
				phone = null
				surname = null
				name = null
				city = null
				line = null
			} else {
				val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))
				role = requestJson.optString("role", null)
				phone = requestJson.optString("phone", null)
				surname = requestJson.optString("surname", null)
				name = requestJson.optString("name", null)
				city = requestJson.optString("city", null)
				line = requestJson.optString("line", null)
			}
		}

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		responseJson.put("users", toJsonArray(User.mapper.items) { user ->

			if (role != null && user.role != role) 			throw SKIP
			if (phone != null && user.phone != phone) 		throw SKIP
			if (surname != null && user.surname != surname) throw SKIP
			if (name != null && user.name != name) 			throw SKIP
			if (city != null && user.city != city) 			throw SKIP
			if (line != null && user.line != city) 			throw SKIP

			val userJson = JSONObject()

			userJson.put("id", User.mapper.getId(user))

			userJson.put("role", user.role)
			userJson.put("registrationDate", dateToMillis(user.registrationDate))
			userJson.putOpt("phone", user.phone)
			userJson.putOpt("surname", user.surname)
			userJson.putOpt("name", user.name)
			userJson.putOpt("birthDate", nullableDateToMillis(user.birthDate))
			userJson.putOpt("city", user.city)
			userJson.putOpt("line", user.line)

			userJson

		})

		return JsonResponse.Final(responseJson)

	}

}