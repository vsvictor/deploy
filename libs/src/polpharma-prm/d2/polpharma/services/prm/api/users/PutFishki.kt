package d2.polpharma.services.prm.api.users

import d2.polpharma.services.polpharmaServerKey
import d2.polpharma.services.prm.model.IndividualEvent
import d2.polpharma.services.prm.model.PRMFishki
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

class PutFishki  : ProtectedHttpEndpoint() {
    override val name = "fishki"

    override fun checkServerKey(serverKey: String) {
        if (serverKey != polpharmaServerKey) throw AccessDenied.ACCESS_DENIED
    }
    override fun checkUserAuth(auth: BasicHttpAuth) {
        throw AccessDenied.ACCESS_DENIED
    }
    override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

        val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

        val userId = requestJson.getString("quadrosoft_id")
        val fishki = requestJson.getInt("fishki")
        val points = requestJson.getInt("points")
        val ev = PRMFishki(userId, fishki, points)
        try {
            PRMFishki.mapper.saveItem(ev)
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
            responseJson.put("points", ev.points)
            responseJson.put("status", "SUCCESS")
            responseJson.put("userId", ev.userID)
            responseJson.put("fishki", fishki)
        }

        return object : JsonResponse () {
            override val json = responseJson
        }
    }
}