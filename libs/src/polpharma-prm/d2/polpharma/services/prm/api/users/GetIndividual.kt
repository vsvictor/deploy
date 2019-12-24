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

class GetIndividual  : ProtectedHttpEndpoint() {
    override val name = "get_individual"

    override fun checkServerKey(serverKey: String) {
        if (serverKey != polpharmaServerKey) throw AccessDenied.ACCESS_DENIED
    }
    override fun checkUserAuth(auth: BasicHttpAuth) {
        //throw AccessDenied.ACCESS_DENIED
    }
    override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

        val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

        val userId = requestJson.getString("quadrosoft_id")
        var result : JSONArray = JSONArray()
        val count = IndividualEvent.mapper.items.count
        try {
            var all_point = 0;
            for(us in Agreement.mapper.items){
                if(us.userId.equals(userId)) {
                    all_point = us.all_point
                    break
                }
            }
            for(e in IndividualEvent.mapper.items){
                //val ev = IndividualEvent.mapper.get(e)
                val ev = e
                if(ev.userId.equals(userId)) {
                    var res = JSONObject()
                    res.put("uuid", ev.uuid)
                    res.put("status", "SUCCESS")
                    res.put("quadrosoft_id", ev.userId)
                    res.put("eventName", ev.eventName)
                    res.put("date", ev.date)
                    res.put("locationName", ev.locationName)
                    res.put("all_points", all_point)
                    res.put("isApply", ev.isApply)
                    result.put(res)
                }
                //IndividualEvent.mapper.remove(ev)
            }
        } catch (notExists : NotExists) {

            return object : JsonResponse() {
                override val json : JSONObject get() {
                    val responseJson = JSONObject()
                    responseJson.put("status", "NOT_EXISTS")
                    return responseJson
                }
            }

        }
        return object : JsonResponse () {
            override val json = result
        }
    }
}