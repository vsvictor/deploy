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

class GetAllIndividual  : ProtectedHttpEndpoint() {
    override val name = "get_all_individual"

    override fun checkServerKey(serverKey: String) {
        if (serverKey != polpharmaServerKey) throw AccessDenied.ACCESS_DENIED
    }
    override fun checkUserAuth(auth: BasicHttpAuth) {
        //throw AccessDenied.ACCESS_DENIED
    }
    override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

        //val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

        //val userId = requestJson.getString("quadrosoft_id")
        var result : JSONArray = JSONArray()
        val count = IndividualEvent.mapper.items.count
        try {
            for(e in IndividualEvent.mapper.items){
                var all_points = 0
                for(i in Agreement.mapper.items){
                    if(i.userId.equals(e.userId)){
                        all_points = i.all_point;
                        break
                    }
                }
                val ev = e
                var res = JSONObject()
                res.put("uuid", ev.uuid)
                res.put("status", "SUCCESS")
                res.put("quadrosoft_id", ev.userId)
                res.put("eventName", ev.eventName)
                res.put("date", ev.date)
                res.put("locationName", ev.locationName)
                res.put("all_points", all_points)
                res.put("isApply", ev.isApply)
                result.put(res)
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