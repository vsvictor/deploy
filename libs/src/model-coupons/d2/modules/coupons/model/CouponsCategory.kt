package d2.modules.coupons.model


import ic.event.Event
import ic.id.SecureStringIdGenerator
import ic.id.StorageMapper
import ic.interfaces.changeable.Changeable
import ic.serial.json.JsonSerializable
import ic.storage.BindingStorage
import ic.struct.map.EditableMap
import org.json.JSONObject


class CouponsCategory : JsonSerializable, Changeable {


	private var nameValue : String?
	var name : String?; get() = nameValue; set(value) { nameValue = value; changeEvent.run() }

	private var imageUrlValue: String?
	var imageUrl : String?; get() = imageUrlValue; set(value) { imageUrlValue = value; changeEvent.run() }

	private var priceValue: Int?
	var price : Int?; get() = priceValue; set(value) { priceValue = value; changeEvent.run() }


	override val changeEvent = Event.Trigger()


	override val classToDeclare = CouponsCategory::class.java
	override fun serialize (json: JSONObject) {
		json.putOpt("name", 	nameValue)
		json.putOpt("imageUrl", imageUrlValue)
		json.putOpt("price", 	priceValue)
	}
	constructor (json: JSONObject) {
		nameValue 		= json.optString("name", 		null)
		imageUrlValue 	= json.optString("imageUrl", 	null)
		priceValue		= json.opt("price") as Int?
	}


	constructor (name: String?, imageUrl: String?, price: Int?) {
		nameValue 		= name
		imageUrlValue 	= imageUrl
		priceValue 		= price
	}

	companion object {
		private val mappersByStorage = EditableMap.Default<StorageMapper<CouponsCategory>, BindingStorage>()
		fun getMapper(storage: BindingStorage) : StorageMapper<CouponsCategory> {
			return mappersByStorage.createIfNull(storage) {
				object : StorageMapper<CouponsCategory>(storage.createFolderIfNotExists("categories")) {
					override fun initIdGenerator() = SecureStringIdGenerator()
				}
			}
		}
	}


}