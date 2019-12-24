package d2.polpharma.services.prm.model

import d2.modules.scenarios.model.History
import d2.polpharma.services.prm.polpharmaPrmAgreementStorage
import d2.polpharma.services.prm.polpharmaPrmIndividualStorage
import d2.polpharma.services.prm.polpharmaPrmStorage
import ic.event.Event
import ic.id.IdGenerator
import ic.id.SecureStringIdGenerator
import ic.id.StorageMapper
import ic.serial.json.JsonSerializable
import ic.storage.BindingStorage
import ic.struct.map.EditableMap
import org.json.JSONObject


class Agreement : JsonSerializable {
    var userId: String? = ""
    var all_point = 0
    var points = 0
    var started = false
    var confirmed = false
    var uuid : String = ""
    init {
        userId = ""
        all_point = 0
        points = 0
        started = false
        confirmed = false
        uuid = ""
    }


    override val classToDeclare = Agreement::class.java
    @Synchronized
    override fun serialize(json: JSONObject) {
        //val responseJson = JSONObject();
        run {
            json.put("quadrosoft_id", userId)
            json.put("started", started)
            json.put("all_points", all_point)
            json.put("points", points)
            json.put("confirmed", confirmed)
            json.put("uuid", uuid)
        }

    }
/*
    constructor(userId: String, started : Boolean = false) {
        this.userId = userId
        this.started = started
        this.all_point = 0
        this.points = 0
    }
 */
    constructor(userId: String, points : Int, allPoints : Int, started : Boolean = false, confirmed : Boolean = false, uuid : String = "") {
        this.userId = userId
        this.started = started
        this.all_point = allPoints
        this.points = points
        this.confirmed = confirmed
        this.uuid = uuid
    }

    constructor(json: JSONObject) {
        try {
            userId = json.getString("quadrosoft_id")
            started = json.getBoolean("started")
            all_point = json.getInt("all_points")
            points = json.getInt("points")
            confirmed = json.getBoolean("confirmed")
            uuid = json.getString("uuid")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    companion object {

        val mapper = object : StorageMapper<Agreement>(BindingStorage(polpharmaPrmAgreementStorage.createFolderIfNotExists("agreement"))) {
            override fun initIdGenerator(): IdGenerator<String> {
                return SecureStringIdGenerator()
            }
        }
    }
}