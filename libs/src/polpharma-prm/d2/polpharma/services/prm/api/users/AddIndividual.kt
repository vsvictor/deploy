package d2.polpharma.services.prm.api.users

import d2.polpharma.services.polpharmaServerKey
import d2.polpharma.services.prm.model.IndividualEvent
import d2.polpharma.services.prm.model.User
import d2.polpharma.services.prm.polpharmaPrmIndividualStorage
import d2.polpharma.services.prm.polpharmaPrmStorage
import ic.id.IdGenerator
import ic.id.SecureStringIdGenerator
import ic.id.StorageMapper
import ic.mongodb.localMongoDbStorage
import ic.network.SocketAddress
import ic.network.http.*
import ic.storage.BindingStorage
import ic.text.Charset
import ic.throwables.AccessDenied
import ic.throwables.NotExists
import ic.throwables.WrongType
import org.json.JSONObject
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class AddIndividual  : ProtectedHttpEndpoint() {
    override val name = "add_individual"

    override fun checkServerKey(serverKey: String) {
        if (serverKey != polpharmaServerKey) throw AccessDenied.ACCESS_DENIED
    }
    override fun checkUserAuth(auth: BasicHttpAuth) {
        throw AccessDenied.ACCESS_DENIED
    }
    override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

        val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

        val userId = requestJson.getString("quadrosoft_id")
        val eventName = requestJson.getString("eventName")
        val date = requestJson.getString("date")
        val locationName = requestJson.getString("locationName")
        val isApply = requestJson.getBoolean("isApply")
        val id = UUID.randomUUID()
        val ev = IndividualEvent(id.toString(), userId, eventName,date, locationName, isApply)
        try {
            IndividualEvent.mapper.saveItem(ev)
            //IndividualEvent.mapper.set(id.toString(), ev)

        } catch (notExists : NotExists) {

            return object : JsonResponse() {
                override val json : JSONObject get() {
                    val responseJson = JSONObject()
                    responseJson.put("status", "NOT_EXISTS")
                    return responseJson
                }
            }

        }catch (ex : WrongType){
            return object : JsonResponse() {
                override val json : JSONObject get() {
                    val responseJson = JSONObject()
                    responseJson.put("status", "NOT_EXISTS")
                    return responseJson
                }
            }
        }


        val responseJson = JSONObject(); run {
            responseJson.put("status", "SUCCESS")
            responseJson.put("userId", ev.userId)
        }

        return object : JsonResponse () {
            override val json = responseJson
        }
    }
}