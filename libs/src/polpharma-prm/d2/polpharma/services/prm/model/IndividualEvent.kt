package d2.polpharma.services.prm.model

import d2.modules.scenarios.model.History
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


class IndividualEvent : JsonSerializable {
    var uuid : String
    var userId: String? = ""
    var eventName: String? = ""
    var date: String? = ""
    var locationName: String? = ""
    var isApply = false

    init {
        uuid = ""
        userId = ""
        eventName = ""
        date = ""
        locationName = ""
        isApply = false
    }


    override val classToDeclare = IndividualEvent::class.java
    @Synchronized
    override fun serialize(json: JSONObject) {
        //val responseJson = JSONObject();
        run {
            json.put("uuid", uuid)
            json.put("quadrosoft_id", userId)
            json.put("eventName", eventName)
            json.put("date", date)
            json.put("locationName", locationName)
            json.put("isApply", isApply)
        }

    }

    constructor(uuid : String, userId: String, eventName: String, date: String, locationName: String, isApply: Boolean = false) {
        this.uuid = uuid
        this.userId = userId
        this.eventName = eventName
        this.date = date
        this.locationName = locationName
        this.isApply = isApply
    }

    constructor(json: JSONObject) {
        try {
            uuid = json.getString("uuid")
            userId = json.getString("quadrosoft_id")
            eventName = json.getString("eventName")
            date = json.getString("date")
            locationName = json.getString("locationName")
            isApply = json.getBoolean("isApply")
        } catch (ex: Exception) {
            //val v = initItemsMapImplementation
        }
    }

    companion object {

        val mapper = object : StorageMapper<IndividualEvent>(BindingStorage(polpharmaPrmIndividualStorage.createFolderIfNotExists("individual_events"))) {
            override fun initIdGenerator(): IdGenerator<String> {
                return SecureStringIdGenerator()
            }
        }
    }
}