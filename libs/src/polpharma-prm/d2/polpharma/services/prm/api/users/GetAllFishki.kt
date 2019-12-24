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
import org.json.JSONArray
import org.json.JSONObject


class GetAllFishki : ProtectedHttpEndpoint() {


	override val name = "list_all_fishki"

	override fun checkServerKey (serverKey: String) = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)


	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		val arr = JSONArray()
		var list = ArrayList<TempObj>()

			for(f in PRMFishki.mapper.items){
				val id = f.userID
				val r = list.find { it -> it.id == id }
				if( r != null){
					r.fishki += f.fishki
					r.points += f.points?:0
				}else{
					list.add(TempObj(f.userID, f.fishki, f.points?:0))
				}
			}

			for( fh in list){
				val obj = JSONObject()
				obj.put("userId", fh.id)
				obj.put("fishki", fh.fishki)
				obj.put("points", fh.points)
				arr.put(obj)
			}

		responseJson.put("data", arr)

		return JsonResponse.Final(responseJson)

	}

	data class TempObj( var id : String, var fishki : Int, var points : Int){

	}

}