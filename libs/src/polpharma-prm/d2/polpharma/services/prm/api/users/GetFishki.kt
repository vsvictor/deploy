package d2.polpharma.services.prm.api.users


import d2.modules.points.model.getPoints
import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.PRMFishki
import d2.polpharma.services.prm.model.User
import d2.polpharma.services.prm.polpharmaPrmPointsStorage
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import org.json.JSONObject


class GetFishki : ProtectedHttpEndpoint() {


	override val name = "list_fishki"

	override fun checkServerKey (serverKey: String) = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)


	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		val userId = requestJson.getString("quadrosoft_id")

		var summ = 0
		for(f in PRMFishki.mapper.items){
			if(f.userID.equals(userId)) summ += f.fishki
		}


		responseJson.put("userId", userId)
		responseJson.put("fishki", summ)
		return JsonResponse.Final(responseJson)

	}


}