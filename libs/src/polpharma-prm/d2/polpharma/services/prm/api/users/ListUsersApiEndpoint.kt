package d2.polpharma.services.prm.api.users


import d2.modules.points.model.getPoints
import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.User
import d2.polpharma.services.prm.polpharmaPrmPointsStorage
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.*
import org.json.JSONObject


class ListUsersApiEndpoint : ProtectedHttpEndpoint() {


	override val name = "list"

	override fun checkServerKey (serverKey: String) = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)


	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		responseJson.put("users", toJsonArray(User.mapper.ids) { userId ->

			val userJson = JSONObject()

			userJson.put("id", userId)

			run {
				val user = User.mapper.getItem(userId)
				userJson.put("newPoints", user.getNewPoints())
				userJson.putOpt("email", user.email)
			}

			userJson.put("points", getPoints(polpharmaPrmPointsStorage, userId))

			userJson

		})

		return JsonResponse.Final(responseJson)

	}


}