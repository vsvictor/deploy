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

class GetAgreement  : ProtectedHttpEndpoint() {
    override val name = "get_agreement"

    override fun checkServerKey(serverKey: String) {
        if (serverKey != polpharmaServerKey) throw AccessDenied.ACCESS_DENIED
    }
    override fun checkUserAuth(auth: BasicHttpAuth) {
        //throw AccessDenied.ACCESS_DENIED
    }
    override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

        val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

        val userId = requestJson.getString("quadrosoft_id")
        var result : JSONObject = JSONObject()
        try {

            for(e in Agreement.mapper.items){
                if(e.userId.equals(userId)) {
                    return object : JsonResponse() {
                        override val json : JSONObject get() {
                            val responseJson = JSONObject()
                            responseJson.put("userId", e.userId)
                            responseJson.put("started", e.started)
                            responseJson.put("all_points", e.all_point)
                            responseJson.put("points", e.points)
                            responseJson.put("confirmed", e.confirmed)
                            return responseJson
                        }
                    }
                }
            }
            throw  NotExists()
        } catch (notExists : NotExists) {
            return object : JsonResponse() {
                override val json : JSONObject get() {
                    val responseJson = JSONObject()
                    responseJson.put("userId", userId)
                    responseJson.put("started", false)
                    responseJson.put("all_points", 0)
                    responseJson.put("points", 0)
                    responseJson.put("confirmed", false)
                    return responseJson
                }
            }
        }
    }
}