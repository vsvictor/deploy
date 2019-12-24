package d2.polpharma.services.prm.api.users

import d2.polpharma.services.polpharmaServerKey
import d2.polpharma.services.prm.model.Agreement
import d2.polpharma.services.prm.model.IndividualEvent
import d2.polpharma.services.prm.model.User
import d2.polpharma.services.prm.polpharmaPrmIndividualStorage
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import ic.throwables.AccessDenied
import ic.throwables.NotExists
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class GetConfirmAgreement  : ProtectedHttpEndpoint() {
    override val name = "get_confirmed_agreement"

    override fun checkServerKey(serverKey: String) {
        if (serverKey != polpharmaServerKey) throw AccessDenied.ACCESS_DENIED
    }
    override fun checkUserAuth(auth: BasicHttpAuth) {
        //throw AccessDenied.ACCESS_DENIED
    }
    override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {
        var result : JSONArray = JSONArray()
        try {
            for(e in Agreement.mapper.items){
                if(e.started) {
                    val responseJson = JSONObject()
                    responseJson.put("userId", e.userId)
                    responseJson.put("started", e.started)
                    responseJson.put("all_points", e.all_point)
                    responseJson.put("points", e.points)
                    responseJson.put("confirmed", e.confirmed)
                    result.put(responseJson)
                }
                //Agreement.mapper.remove(e.userId.toString());
            }
        } catch (notExists : NotExists) {
            notExists.printStackTrace()
        }
         return object : JsonResponse () {
            override val json = result
        }
    }
}