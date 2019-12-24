package d2.polpharma.services.prm.model


import d2.modules.events.model.Event
import d2.polpharma.services.prm.polpharmaPrmAgreementStorage
import d2.polpharma.services.prm.polpharmaPrmNewFishkiStorage
import ic.id.IdGenerator
import ic.id.SecureStringIdGenerator
import ic.id.StorageMapper
import ic.serial.json.JsonSerializable
import ic.storage.BindingStorage
import org.json.JSONException
import org.json.JSONObject


class PRMFishki : JsonSerializable {

	val userID 	: String
	val fishki 	: Int
	var points : Int?

	override val classToDeclare = PRMFishki::class.java
	override fun serialize (json: JSONObject) {
		json.put("userID", 	userID)
		json.put("fishki", 	fishki)
		json.put("points", points)
	}
	constructor (json: JSONObject) : super () {
		userID 	= json.getString("userID")
		fishki 	= json.getInt("fishki")
		try {
			points = json.getInt("points") ?: 0
		}catch (ex : JSONException){
			points = 0
		}
	}

	constructor (userID : String, fishki : Int, points : Int) {
		this.userID = userID
		this.fishki = fishki
		this.points = points
	}
	companion object {

		val mapper = object : StorageMapper<PRMFishki>(BindingStorage(polpharmaPrmNewFishkiStorage.createFolderIfNotExists("prmfishki"))) {
			override fun initIdGenerator(): IdGenerator<String> {
				return SecureStringIdGenerator()
			}
		}
	}

}