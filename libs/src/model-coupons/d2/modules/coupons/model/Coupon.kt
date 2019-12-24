package d2.modules.coupons.model


import ic.annotations.Necessary
import ic.event.Event
import ic.id.SecureStringIdGenerator
import ic.id.StorageMapper
import ic.interfaces.changeable.Changeable
import ic.serial.json.JsonSerializable
import ic.storage.BindingStorage
import ic.struct.map.EditableMap
import org.json.JSONObject


class Coupon : JsonSerializable, Changeable {


	private var categoryIdValue : String?
	var categoryId : String?; get() = categoryIdValue; set(value) { categoryIdValue = value; changeEvent.run() }

	private var codeValue : String?
	var code : String?; get() = codeValue; set(value) { codeValue = value; changeEvent.run() }

	private var codeImageUrlValue: String?
	var codeImageUrl : String?; get() = codeImageUrlValue; set(value) { codeImageUrlValue = value; changeEvent.run() }

	private var addedCountValue: Int
	var addedCount : Int; get() = addedCountValue; set(value) { addedCountValue = value; changeEvent.run() }


	override val changeEvent = Event.Trigger()

	override val classToDeclare = Coupon::class.java
	override fun serialize (json: JSONObject) {
		json.putOpt("categoryId", 	categoryIdValue)
		json.putOpt("code", 		codeValue)
		json.putOpt("codeImageUrl", codeImageUrlValue)
		json.put("addedCount", 		addedCountValue)
	}
	@Necessary constructor(json: JSONObject) {
		categoryIdValue		= json.optString("categoryId", 		null)
		codeValue 			= json.optString("code", 			null)
		codeImageUrlValue 	= json.optString("codeImageUrl", 	null)
		addedCountValue		= json.optInt("addedCount", 0)
	}

	constructor(
		categoryId		: String?,
		code 			: String?,
		codeImageUrl 	: String?
	) {
		categoryIdValue 	= categoryId
		codeValue 			= code
		codeImageUrlValue 	= codeImageUrl
		addedCountValue		= 0
	}

	companion object {
		private val mappersByStorage = EditableMap.Default<StorageMapper<Coupon>, BindingStorage>()
		fun getMapper(storage: BindingStorage) : StorageMapper<Coupon> {
			return mappersByStorage.createIfNull(storage) {
				object : StorageMapper<Coupon>(storage.createFolderIfNotExists("definitions")) {
					override fun initIdGenerator() = SecureStringIdGenerator()
				}
			}
		}
	}

}