package d2.polpharma.services.prm.model


import d2.polpharma.services.prm.polpharmaPrmArrangementsStorage
import ic.date.Millis.*
import ic.event.Event
import ic.id.SecureStringIdGenerator
import ic.id.StorageMapper
import ic.interfaces.changeable.Changeable
import ic.json.JsonArrays.jsonArrayToList
import ic.json.JsonArrays.toJsonArray
import ic.serial.json.JsonSerializable
import ic.struct.map.CountableMap
import ic.struct.map.EditableMap
import ic.throwables.AlreadyExists
import ic.throwables.AlreadyExists.ALREADY_EXISTS
import ic.throwables.NotExists
import ic.throwables.NotExists.NOT_EXISTS
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class Arrangement : Changeable, JsonSerializable {


	private var nameValue : String?
	var name : String?
		@Synchronized get() = nameValue
		@Synchronized set(value) { nameValue = value; changeEvent.run() }

	private var startDateValue : Date?
	var startDate : Date?
		@Synchronized get() = startDateValue
		@Synchronized set(value) { startDateValue = value; changeEvent.run() }

	private var endDateValue : Date?
	var endDate : Date?
		@Synchronized get() = endDateValue
		@Synchronized set(value) { endDateValue = value; changeEvent.run() }

	private var cancelDateValue : Date?
	var cancelDate : Date?
		@Synchronized get() = cancelDateValue
		@Synchronized set(value) { cancelDateValue = value; changeEvent.run() }

	private var costPointsValue : Int?
	var costPoints : Int?
		@Synchronized get() = costPointsValue
		@Synchronized set(value) { costPointsValue = value; changeEvent.run() }

	private var placeValue : String?
	var place : String?
		@Synchronized get() = placeValue
		@Synchronized set(value) { placeValue = value; changeEvent.run() }

	private var isActiveValue : Boolean
	var isActive : Boolean
		@Synchronized get() = isActiveValue
		@Synchronized set(value) { isActiveValue = value; changeEvent.run() }


	private val arrangementUsersValue : EditableMap<UserArrangementState, String>
	val users : CountableMap<UserArrangementState, String>
		@Synchronized get() = CountableMap.Default(arrangementUsersValue)

	@Throws(AlreadyExists::class)
	@Synchronized fun addArrangementUser (userId: String) {
		if (arrangementUsersValue.keys.contains(userId)) throw ALREADY_EXISTS
		arrangementUsersValue.set(userId, UserArrangementState.OPEN)
		changeEvent.run()
	}

	@Throws(NotExists::class)
	@Synchronized fun confirmArrangementUser (userId: String) {
		if (!arrangementUsersValue.keys.contains(userId)) throw NOT_EXISTS
		arrangementUsersValue.set(userId, UserArrangementState.CONFIRMED)
		changeEvent.run()
	}

	@Throws(NotExists::class)
	@Synchronized fun cancelArrangementUser (userId: String) {
		if (!arrangementUsersValue.keys.contains(userId)) throw NOT_EXISTS
		arrangementUsersValue.set(userId, UserArrangementState.CANCELED)
		changeEvent.run()
	}


	override val changeEvent = Event.Trigger()

	override val classToDeclare = Arrangement::class.java
	@Synchronized override fun serialize (json: JSONObject) {
		json.putOpt("name", 			nameValue)
		json.putOpt("startDate", 		nullableDateToMillis(startDateValue))
		json.putOpt("endDate", 			nullableDateToMillis(endDateValue))
		json.putOpt("cancelDate", 		nullableDateToMillis(cancelDateValue))
		json.putOpt("costPoints", 		costPointsValue)
		json.putOpt("place", 			placeValue)
		json.putOpt("isActive",			isActiveValue)
		json.put("arrangementUsers", toJsonArray(arrangementUsersValue.keys) {
			val arrangementUserJson = JSONObject()
			arrangementUserJson.put("userId", it)
			arrangementUserJson.put("state", arrangementUsersValue.getNotNull(it).name)
		})
	}
	constructor (json: JSONObject) {
		nameValue = json.opt("name") as String?
		startDateValue 	= if (json.has("startDate")) 	millisToDate(json.getLong("startDate")) 	else null
		endDateValue 	= if (json.has("endDate")) 		millisToDate(json.getLong("endDate")) 		else null
		cancelDateValue = if (json.has("cancelDate")) 	millisToDate(json.getLong("cancelDate")) 	else null
		costPointsValue = json.opt("costPoints") as Int?
		placeValue = json.opt("place") as String?
		arrangementUsersValue = EditableMap.Default(); run {
			val arrangementUsersJson = json.get("arrangementUsers")
			if (arrangementUsersJson is JSONArray) {
				jsonArrayToList<Unit, JSONObject>(arrangementUsersJson) { arrangementUserJson ->
					arrangementUsersValue.set(
						arrangementUserJson.getString("userId"),
						UserArrangementState.byName(arrangementUserJson.getString("state"))
					)
				}
			}
		}
		isActiveValue = json.optBoolean("isActive", true)
	}

	constructor (
		name: String?,
		startDate : Date?,
		endDate : Date?,
		cancelDate : Date?,
		costPoints : Int?,
		place : String?,
		isActive : Boolean
	) {
		nameValue = name
		startDateValue = startDate
		endDateValue = endDate
		cancelDateValue = cancelDate
		costPointsValue = costPoints
		placeValue = place
		isActiveValue = isActive
		arrangementUsersValue = EditableMap.Default()
	}

	companion object {

		val mapper = object : StorageMapper<Arrangement>(polpharmaPrmArrangementsStorage) {
			override fun initIdGenerator() = SecureStringIdGenerator()
		}

	}

}