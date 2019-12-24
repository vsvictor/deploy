package d2.polpharma.services.prm.api.users

import d2.polpharma.services.polpharmaServerKey
import d2.polpharma.services.prm.model.Agreement
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

class DeleteAgreement  : ProtectedHttpEndpoint() {
    override val name = "delete_agreement"

    override fun checkServerKey(serverKey: String) {
        if (serverKey != polpharmaServerKey) throw AccessDenied.ACCESS_DENIED
    }
    override fun checkUserAuth(auth: BasicHttpAuth) {
    }
    override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

        val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

        val userId = requestJson.getString("quadrosoft_id")
        //val uuid = requestJson.getString("uuid")
        try {

            for(er in Agreement.mapper.items){
                if(er.userId!!.equals(uintArrayOf())){
                    Agreement.mapper.removeOrThrow(Agreement.mapper.id(er))
                }
            }
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
        }

        return object : JsonResponse () {
            override val json = responseJson
        }
    }
}