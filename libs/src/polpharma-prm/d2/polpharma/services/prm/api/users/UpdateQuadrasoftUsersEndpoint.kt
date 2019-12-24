package d2.polpharma.services.prm.api.users


import d2.modules.points.model.addPoints
import d2.polpharma.services.prm.model.User
import d2.polpharma.services.prm.model.User.Companion.getQuadrasoftIds
import d2.polpharma.services.prm.polpharmaPrmPointsStorage
import d2.polpharma.services.quadrasoftServerKey
import ic.network.SocketAddress
import ic.network.http.*
import ic.struct.set.EditableSet
import ic.struct.set.SubtractCountableSet
import ic.text.Charset
import ic.throwables.AccessDenied.ACCESS_DENIED
import ic.throwables.NotExists
import ic.throwables.WrongValue.WRONG_VALUE
import org.json.JSONObject


class UpdateQuadrasoftUsersEndpoint : ProtectedHttpEndpoint() {


	override val name = "quadrasoft-update"


	override fun checkServerKey (serverKey: String) { if (serverKey != quadrasoftServerKey) throw WRONG_VALUE }
	override fun checkUserAuth (auth: BasicHttpAuth) = throw ACCESS_DENIED


	override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val usersJson = requestJson.getJSONArray("users")

		val activeQuadrasoftIds = EditableSet.Default<String>()

		usersJson.forEach {

			val userJson = it as JSONObject

			val quadrasoftId = userJson.getString("id")

			activeQuadrasoftIds.addIfNotExists(quadrasoftId)

			val user = try {
				User.byQuadrasoftId(quadrasoftId)
			} catch (notExists : NotExists) {
				User(quadrasoftId)
			}

			synchronized (user) {
				user.setActive(true)
				user.setNewPoints(userJson.optInt("points", 0))
				if (userJson.has("month")) {
					user.setMonth(userJson.getInt("month"))
				}
				if (userJson.has("line")) {
					user.line = userJson.getString("line")
				}
				if (userJson.has("category")) {
					user.category = userJson.getString("category")
				}
			}

			if (userJson.has("points")) {
				addPoints(polpharmaPrmPointsStorage, User.mapper.getId(user), userJson.getInt("points"))
			}

		}

		SubtractCountableSet(getQuadrasoftIds(), activeQuadrasoftIds).forEach {
			User.byQuadrasoftId(it).setActive(false)
		}

		run {
			@Suppress("NAME_SHADOWING")
			val requestJson = JSONObject()
			HttpClient.request(HttpRequest(
				"https://www.corezoid.com/api/1/json/public/544341/75afb1b98f65e5e87ec5256cf63ccfc879baeeb5",
				"POST",
				Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
			))
		}

		return object : JsonResponse() {
			override val json : JSONObject get() {
				val responseJson = JSONObject()
				responseJson.put("status", "SUCCESS")
				return responseJson
			}
		}

	}

}