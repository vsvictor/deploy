package d2.modules.admins.model


import ic.event.Event
import ic.interfaces.changeable.Changeable
import ic.serial.json.JsonSerializable
import org.json.JSONObject


class Admin : JsonSerializable, Changeable {

	private var passwordValue : String
	var password : String get() = passwordValue; set(value) { passwordValue = value; changeEvent.run() }

	private var profileValue : JSONObject
	var profile : JSONObject get() = profileValue; set(value) { profileValue = value; changeEvent.run() }

	override val changeEvent = Event.Trigger()

	override val classToDeclare = Admin::class.java
	override fun serialize (json: JSONObject) {
		json.put("password", passwordValue)
		json.put("profile", profileValue)
	}
	constructor (json: JSONObject) {
		passwordValue = json.getString("password")
		profileValue = if (json.has("profile")) json.getJSONObject("profile") else JSONObject()
	}

	constructor (password : String, profile : JSONObject) {
		passwordValue = password
		profileValue = profile
	}

}