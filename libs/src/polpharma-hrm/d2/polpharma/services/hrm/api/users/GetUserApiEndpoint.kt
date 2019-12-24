package d2.polpharma.services.hrm.api.users


import d2.polpharma.services.hrm.model.User
import ic.date.Millis.*
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject


class GetUserApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "get"

	override fun checkServerKey (serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId = requestJson.getString("id")

		val user = try {
			User.mapper.getItemOrThrow(userId)
		} catch (noSuchUser : NotExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "NO_SUCH_USER")
			return JsonResponse.Final(responseJson)
		}

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		responseJson.put("role", user.role)
		responseJson.put("registrationDate", dateToMillis(user.registrationDate))
		responseJson.putOpt("phone", user.phone)
		responseJson.putOpt("surname", user.surname)
		responseJson.putOpt("name", user.name)
		responseJson.putOpt("birthDate", nullableDateToMillis(user.birthDate))
		responseJson.putOpt("city", user.city)
		responseJson.putOpt("line", user.line)

		return JsonResponse.Final(responseJson)

	}

}